package rs.edu.raf.banka1.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateForm {

    private String username;
    private String password;
    private String imePrezime;
    private Boolean isAdmin;

}
