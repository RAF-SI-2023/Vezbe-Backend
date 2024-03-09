package rs.edu.raf.banka1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.dto.UserDto;
import rs.edu.raf.banka1.form.UserCreateForm;
import rs.edu.raf.banka1.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDto>> listUsers() {
        try {
            return ResponseEntity.ok(userService.listUsers());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getUser(@PathVariable String username) {
        try {
            return ResponseEntity.ok(userService.getUser(username));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> createUser(@RequestBody UserCreateForm userCreateForm) {
        try {
            return ResponseEntity.ok().body(userService.createUser(userCreateForm));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> editUser(@RequestBody UserCreateForm userCreateForm) {
        try {
            return ResponseEntity.ok().body(userService.editUser(userCreateForm));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
