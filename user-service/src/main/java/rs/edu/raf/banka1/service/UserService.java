package rs.edu.raf.banka1.service;

import rs.edu.raf.banka1.dto.UserDto;
import rs.edu.raf.banka1.form.UserCreateForm;
import rs.edu.raf.banka1.model.User;

import java.util.List;

public interface UserService {

    UserDto getUser(String username) throws Exception;
    List<UserDto> listUsers();
    UserDto createUser(UserCreateForm userCreateForm) throws Exception;
    UserDto editUser(UserCreateForm userCreateForm) throws Exception;
    UserDto deleteUser(String username) throws Exception;
    boolean isAdmin(String username) throws Exception;

}
