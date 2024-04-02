package rs.edu.raf.banka1.e2e.usercontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import rs.edu.raf.banka1.dto.UserDto;
import rs.edu.raf.banka1.form.LoginResponseForm;
import rs.edu.raf.banka1.form.UserCreateForm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTestsSteps extends UserControllerTestsConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserControllerTestsState userControllerTestsState;

    @Given("logovali smo se kao administrator")
    public void logovaliSmoSeKaoAdministrator() {
        try {
            ResultActions resultActions = mockMvc.perform(
                    post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .content("{\"username\":\"admin\",\"password\":\"admin\"}")
            ).andExpect(status().isOk());

            MvcResult mvcResult = resultActions.andReturn();

            String loginResponse = mvcResult.getResponse().getContentAsString();
            LoginResponseForm loginResponseForm = objectMapper.readValue(loginResponse, LoginResponseForm.class);
            userControllerTestsState.setJwtToken(loginResponseForm.getJwt());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @When("naparvimo korisnika {string} sa usernameom {string} i passwordom {string}")
    public void kadaNaparvimoKorisnikaSaUsernameomIPasswordom(String imePrezime, String username, String password) {
        UserCreateForm userCreateForm = new UserCreateForm();
        userCreateForm.setUsername(username);
        userCreateForm.setImePrezime(imePrezime);
        userCreateForm.setPassword(password);
        userCreateForm.setIsAdmin(false);

        try {
            String jsonUserCreateForm = objectMapper.writeValueAsString(userCreateForm);

            ResultActions resultActions = mockMvc.perform(
                    post("/api")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .content(jsonUserCreateForm)
                            .header("Authorization", "Bearer " + userControllerTestsState.getJwtToken())
            ).andExpect(status().isOk());

            MvcResult mvcResult = resultActions.andReturn();

            String jsonUserDto = mvcResult.getResponse().getContentAsString();
            UserDto userDto = objectMapper.readValue(jsonUserDto, UserDto.class);
            assertEquals(userDto.getUsername(), userCreateForm.getUsername());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("mozemo da povucemo korisnika {string} preko API-a")
    public void mozemoDaPovucemoKorisnikaPrekoAPIA(String username) {
        try {
            ResultActions resultActions = mockMvc.perform(
                    get("/api/username/" + username)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", "Bearer " + userControllerTestsState.getJwtToken())
            ).andExpect(status().isOk());

            MvcResult mvcResult = resultActions.andReturn();

            String jsonUserDto = mvcResult.getResponse().getContentAsString();
            UserDto userDto = objectMapper.readValue(jsonUserDto, UserDto.class);
            assertEquals(userDto.getUsername(), username);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
