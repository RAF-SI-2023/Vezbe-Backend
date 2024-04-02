package rs.edu.raf.banka1.integration.userservice;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import rs.edu.raf.banka1.dto.UserDto;
import rs.edu.raf.banka1.form.UserCreateForm;
import rs.edu.raf.banka1.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class UserServiceTestsSteps extends UserServiceTestsConfig {

    @Autowired
    private UserService userService;

    List<UserDto> users;

    /***
     * METODE KOJE SE KORISTE U VISE RAZLICITIH SCENARIJA
     */

    @When("izlistamo sve korisnike")
    public void izlistamoSveKorisnike() {
        users = userService.listUsers();
    }

    @Then("dobicemo korisnika koji ima username {string}")
    public void dobicemoKorisnikaKojiImaUsername(String expectedUsername) {
        for(UserDto user: users) {
            if(user.getUsername().equals(expectedUsername)) {
                return;
            }
        }

        fail("korisnik sa username-om " + expectedUsername + " ne postoji");
    }

    /***
     * METODE ZA SCENARIO "Listanje svih korisnika"
     */

    @Given("imamo administratorskog korisnika sa username-om {string}")
    public void imamoAdministratorskogKorisnikaSaUsernameOm(String adminUsername) {
        // Ovaj korisnik vec postoji, pa ovde ne radimo nesto.
        // Administratorski korisnik se pravi pri podizanju Spring aplikacije, koristeci bootstrap mehanizam definisan
        // u BootstrapData klasi.
    }

    /***
     * METODE ZA SCENARIO "Pretraga korisnika Pera koji ne postoji"
     */

    @Then("necemo dobiti korisnika koji ima username {string}")
    public void necemoDobitiKorisnikaKojiImaUsername(String unexpectedUsername) {
        for(UserDto user: users) {
            if(user.getUsername().equals(unexpectedUsername)) {
                fail("korisnik sa username-om " + unexpectedUsername + " postoji, a ocekujemo da ne postoji");
            }
        }
    }

    /***
     * METODE ZA SCENARIO "Kreiranje korisnika"
     */

    @When("napravimo korisnika {string} sa username-om {string} i passwordom {string}")
    public void napravimoKorisnikaSaUsernameOmIPasswordom(String imePrezime, String username, String password) {
        UserCreateForm userCreateForm = new UserCreateForm();
        userCreateForm.setUsername(username);
        userCreateForm.setPassword(password);
        userCreateForm.setImePrezime(imePrezime);
        userCreateForm.setIsAdmin(false);

        try {
            userService.createUser(userCreateForm);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /***
     * METODE ZA SCENARIO "Brisanje korisnika koji je prethodno napravljen"
     */

    @When("obrisemo korisnika {string}")
    public void obrisemoKorisnika(String username) {
        try {
            userService.deleteUser(username);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    @Then("povlacanje korisnika {string} baca exception")
    public void povlacanjeKorisnikaBacaException(String username) {
        assertThrows(Exception.class, () -> userService.getUser(username));
    }
}
