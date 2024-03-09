package rs.edu.raf.banka1.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequestForm {

    private String username;
    private String password;

}
