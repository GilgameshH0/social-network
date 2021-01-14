package social.network.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import social.network.dto.UserSignUpAndUpdateRequestDto;
import social.network.model.User;
import social.network.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private UserController userController;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    public void registerUserTest() throws Exception{
        assertThat(userController).isNotNull();
        UserSignUpAndUpdateRequestDto userSignUpAndUpdateRequestDto = new UserSignUpAndUpdateRequestDto();
        userSignUpAndUpdateRequestDto.setUsername("Oleg228");
        userSignUpAndUpdateRequestDto.setEmail("oleg@yandex.ru");
        this.mockMvc.perform(post("/api/auth/signup", userSignUpAndUpdateRequestDto)).andDo(print()).andExpect(status().isOk());
    }

    public void authenticateUserTest() throws Exception{
    }

    public void findUserTest() throws Exception{
    }

    public void updateCurrentUserTest() throws Exception{
    }

    public void updateUserTest() throws Exception{
    }

    public void removeCurrentUserTest() throws Exception{
    }

    public void removeUserTest() throws Exception{
    }

    public void findAllUserByCountryTest() throws Exception{
    }
}
