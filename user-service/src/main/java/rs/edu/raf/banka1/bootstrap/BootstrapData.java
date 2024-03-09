package rs.edu.raf.banka1.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.repository.UserRepository;

import java.util.Optional;

@Component
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BootstrapData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Loading Data...");

        Optional<User> findUser = userRepository.findUserByUsername("admin");
        if(findUser.isEmpty()) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(this.passwordEncoder.encode("admin"));
            user.setImePrezime("RAF Admin");
            user.setIsAdmin(true);

            this.userRepository.save(user);

            System.out.println("Data loaded!");
        }
    }
}
