package rs.edu.raf.banka1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.edu.raf.banka1.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

}
