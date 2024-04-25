package rs.edu.raf.banka1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dto.UserDto;
import rs.edu.raf.banka1.form.UserCreateForm;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Napomene o kesiranju i Redis integraciji:
// U okviru ovog servisa demonstriramo koriscenje Redis kesiranja, gde se rezultat svake CRUD metode kesira
// na odgovarajuci nacin.
//
// Kao sto je pomenuto na vezbama, korisnicki servis nije idealan za kesiranje, ali to je jedini servis koji imamo
// u ovom projektu. Primera radi, nekada ne zelite da kesirate senzitivne podatke (npr. podatke o korisniku) ili
// njegove permisije. Kesiranje permisija moze da dovede do toga da vi korisniku oduzmete neke permisije, a da on i
// dalje ima pristup sistemu sve dok kes koji se cuva ne istekne (vreme posle kog kes istice se definise u
// application.properties).
//
// Dobri servisi za kesiranje su oni koji rade nad podacima kojima se cesto pristupa, a koji se ne menjaju toliko
// cesto. To mogu da budu razni sifarnici, npr. lista valuta, lista postanskih brojeva, itd.
//
// Kesiranje ima efekta samo ako se metode pozivaju iz neke druge klase. Ukoliko se metode pozivaju iz
// iste klase, kesiranje nece funkcionisati.
//
// Postoje tri razlicite anotacije koje se koriste da se kesiranje ukljuci za datu metodu:
//   - @Cacheable  - dohvata podatke iz kesa ukoliko su podaci kesirani, inace, izvrasava anotiranu metodu i kesira
//                   rezultat (koristi se prilikom listanja i dohvatanja podataka)
//   - @CachePut   - upisuje podatke u kes (koristi se prilikom kreiranja ili azuriranja objekta)
//   - @CacheEvict - brise podatke iz kesa (koristi se prilikom brisanja objekta)
// Dodatno, @Caching anotacija vam dozvoljava da kombinujete ili ponavljate anotacije (videti primer sa brisanjem).
// Sve tri anotacije uzimaju sledeca dva parametra (postoji jos parametara, ali ova dva su najbitnija):
//   - value - Redis objekat u kome se cuvaju podaci (gledati na ovo kao tabelu u relacionim bazama podataka)
//   - key   - kljuc pod kojim se cuva konkretan podatak
//             (koristi se samo za kesiranje konkretnog objekta, tj. ne koristi se kada se kesiraju liste)
// Strogo se preporucuje da se liste i klase cuvaju sa razlicitim value parametrima. Pogledati listUsers i getUser
// metode kao primer.

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Ova metoda dohvata listu svih korisnika i kesira tu listu u Redis objektu "users".
    // Kada se metoda pozove, Spring ce proveriti da li u Redis-u, u objektu "users" postoji zapis.
    // Ukoliko postoji, taj zapis iz Redis-s se automatski vraca, tj. metoda se uopste nece izvrsiti.
    // Ukoliko ne postoji, izvrsava se metoda, a rezultat izvrsavanja se cuva u Redis-u.
    //
    // U application.properties konfigurisati koliko dugo vazi kesiran podatak, npr. da se posle 15 minuta
    // kes invalidira (nakon invalidiranja, pri sledecem pozivu metode, ona se ponovo izvrsava).
    //
    // Primetiti da ovde nemamo "key" parametar u Cacheable anotaciji posto kesiramo listu.
    @Override
    @Cacheable(value = "users")
    public List<UserDto> listUsers() {
        System.err.println("ISPIS 1");
        return userRepository.findAll().stream().map(this::convertUserToDto).collect(Collectors.toList());
    }

    // Ovo je primer kesiranja konkretnog objekta, u ovom slucaju pojedinacnog korisnika.
    // Primetiti da je "value" parametar drugaciji u odnosu na "listUsers" metodu i da imamo "key" parametar.
    // Vrednost "key" parametra ima "#" sto nam dozvoljava da referenciramo argumente funkcije.
    @Override
    @Cacheable(value = "user", key = "#username")
    public UserDto getUser(String username) throws Exception {
        System.err.println("ISPIS 2");
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if (userOpt.isEmpty()) {
            throw new Exception("user does not exist");
        }

        return convertUserToDto(userOpt.get());
    }

    // Ovo je primer kesiranja prilikom dodavanja novog objekta, tj. korisnika u ovom slucaju.
    // Primetiti da ovde kombinujemo @CachePut i @CacheEvict anotacije:
    //   - @CachePut    - sluzi da se taj korisnik kesira odmah prilikom kreiranja
    //   - @CacheEvict  - sluzi da obrise kesiranu listu svih korisnika.
    //                    Ovo je izuzetno vazno zato sto ako bi ovaj korak preskocili i pokusali da izlistamo sve
    //                    korisnike, ne bi smo videli korisnika kojeg smo napravili sve dok kes ne istekne
    @Override
    @CachePut(value = "user", key = "#userCreateForm.username")
    @CacheEvict(value = "users", allEntries = true)
    public UserDto createUser(UserCreateForm userCreateForm) throws Exception {
        if (userCreateForm.getUsername().isBlank() ||
                userCreateForm.getPassword().isBlank() ||
                userCreateForm.getImePrezime().isBlank() ||
                userCreateForm.getIsAdmin() == null) {
            throw new Exception("user is missing data");
        }

        User user = new User();
        user.setUsername(userCreateForm.getUsername());
        user.setImePrezime(userCreateForm.getImePrezime());
        user.setIsAdmin(userCreateForm.getIsAdmin());

        String hashPW = BCrypt.hashpw(userCreateForm.getPassword(), BCrypt.gensalt());
        user.setPassword(hashPW);

        user = userRepository.save(user);

        return convertUserToDto(user);
    }

    // Ovo je primer kesiranja prilikom izmene postojeceg objekta, tj. korisnika.
    // @CachePut i @CacheEvict se koriste na isti nacin kao kod kreiranja novog korisnika.
    @Override
    @CachePut(value = "user", key = "#userCreateForm.username")
    @CacheEvict(value = "users", allEntries = true)
    public UserDto editUser(UserCreateForm userCreateForm) throws Exception {
        Optional<User> userOpt = userRepository.findUserByUsername(userCreateForm.getUsername());
        if (userOpt.isEmpty()) {
            throw new Exception("user does not exist");
        }
        User user = userOpt.get();

        if (!userCreateForm.getPassword().isBlank()) {
            String hashPW = BCrypt.hashpw(userCreateForm.getPassword(), BCrypt.gensalt());
            user.setPassword(hashPW);
        }
        if (!userCreateForm.getImePrezime().isBlank()) {
            user.setImePrezime(userCreateForm.getImePrezime());
        }
        if (userCreateForm.getIsAdmin() != null) {
            user.setIsAdmin(userCreateForm.getIsAdmin());
        }

        user = userRepository.save(user);

        return convertUserToDto(user);
    }

    // Ovo je primer brisanja objekta, u ovom slucaju korisnika.
    // U ovom slucaju moramo da:
    //   - Obrisemo pojedinacni kesirani objekat (value = "user").
    //     Ukoliko ovo preskocimo, dohvatanje pojedinacnog korisnika ce vratiti obrisanog korisnika iako on ne postoji
    //     u bazi sve dok kes ne istekne.
    //   - Obrisemo kesiranu listu svih korisnika (value = "users").
    //     Ukoliko ovo preskocimo, listanje svih korisnika ce vratiti obrisanog korisnika sve dok kes ne istekne.
    // Posto nam Java ne dozvoljava da navedemo istu anotaicju dva puta (@CacheEvict), koristimo @Caching anotaciju
    // da bi spojili dve razlicite @CacheEvict anotacije.
    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true),
            @CacheEvict(value = "user", key = "#username")
    })
    public UserDto deleteUser(String username) throws Exception {
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if (userOpt.isEmpty()) {
            throw new Exception("user does not exist");
        }

        userRepository.delete(userOpt.get());

        return convertUserToDto(userOpt.get());
    }

    @Override
    public boolean isAdmin(String username) throws Exception {
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if (userOpt.isEmpty()) {
            throw new Exception("user does not exist");
        }

        return userOpt.get().getIsAdmin();
    }

    private UserDto convertUserToDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setIsAdmin(user.getIsAdmin());
        userDto.setImePrezime(user.getImePrezime());

        return userDto;
    }

}
