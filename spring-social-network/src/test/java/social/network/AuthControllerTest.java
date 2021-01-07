package social.network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import social.network.controller.UserController;
import social.network.dto.auth.SignUpUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private UserController userController;
    @Autowired
    private MockMvc mockMvc;


    public void test() throws Exception{
        assertThat(userController).isNotNull();
        SignUpUser signUpUser = new SignUpUser();
        signUpUser.setUsername("Oleg228");
        signUpUser.setPassword("kiska12");
        signUpUser.setEmail("oleg@yandex.ru");
        this.mockMvc.perform(post("/api/auth/signup", signUpUser)).andDo(print()).andExpect(status().isOk());
    }
}
