package social.network.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import social.network.dto.UserGetResponseDto;
import social.network.dto.UserLogInRequestDto;
import social.network.dto.UserSignUpAndUpdateRequestDto;
import social.network.exception.SocialNetworkException;
import social.network.model.ErrorCode;
import social.network.model.Gender;
import social.network.service.UserService;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest()
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void registerTest() throws Exception {
        UserSignUpAndUpdateRequestDto userDto = new UserSignUpAndUpdateRequestDto();
        userDto.setUsername("testUsername");
        userDto.setPassword("testPassword");
        userDto.setEmail("testEmail");
        userDto.setGender(Gender.MAN);
        mockMvc
                .perform(post("/api/user/signup")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void registerTestFails() throws Exception {
        UserSignUpAndUpdateRequestDto existUser = new UserSignUpAndUpdateRequestDto();
        existUser.setUsername("testUsername");
        existUser.setPassword("testPassword");
        existUser.setEmail("testEmail");
        existUser.setGender(Gender.MAN);
        doThrow(new SocialNetworkException(ErrorCode.UsernameOrEmailAlreadyInUse, "Username or email are already in use!")).when(userService).registerUser(existUser);
        mockMvc
                .perform(post("/api/user/signup").content(objectMapper.writeValueAsString(existUser)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void LoginTest() throws Exception {
        UserLogInRequestDto userDto = new UserLogInRequestDto();
        userDto.setUsername("testUsername");
        userDto.setPassword("testPassword");
        mockMvc
                .perform(post("/api/user/signin").content(objectMapper.writeValueAsString(userDto)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void LoginTestFails() throws Exception {
        UserLogInRequestDto notExistsUser = new UserLogInRequestDto();
        notExistsUser.setUsername("testUsername");
        notExistsUser.setPassword("testPassword");
        doThrow(new SocialNetworkException(ErrorCode.NotExists, "User with id:" + notExistsUser.getUsername() + " does not exists!")).when(userService).authenticateUser(notExistsUser);
        mockMvc
                .perform(post("/api/user/signin")
                        .content(objectMapper.writeValueAsString(notExistsUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findUserTest() throws Exception {
        long userId = 1L;
        UserGetResponseDto userDto = new UserGetResponseDto();
        userDto.setId(userId);
        userDto.setUsername("testUsername");
        when(userService.findUser(userId)).thenReturn(userDto);
        mockMvc
                .perform(get("/api/user/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    public void findUserTestFails() throws Exception {
        long userId = 999L;
        when(userService.findUser(userId)).thenThrow(new SocialNetworkException(ErrorCode.NotExists, "User with id:" + userId + " does not exists!"));
        mockMvc
                .perform(get("/api/user/" + userId))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "root", roles = {"USER"})
    public void updateCurrentUserTest() throws Exception {
        UserSignUpAndUpdateRequestDto userDto = new UserSignUpAndUpdateRequestDto();
        userDto.setUsername("newTestUsername");
        userDto.setPassword("testPassword");
        userDto.setEmail("testEmail@mail.com");
        userDto.setGender(Gender.MAN);
        mockMvc
                .perform(
                        put("/api/user/update-current-user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "root", roles = {"MODERATOR"})
    public void updateUserTest() throws Exception {
        long userId = 2L;
        UserSignUpAndUpdateRequestDto userDto = new UserSignUpAndUpdateRequestDto();
        userDto.setUsername("newTestUsername");
        userDto.setPassword("testPassword");
        userDto.setEmail("testEmail");
        userDto.setGender(Gender.MAN);
        mockMvc
                .perform(put("/api/user/update-user/" + userId)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "root", roles = {"MODERATOR"})
    public void updateUserTestFails() throws Exception {
        long userId = 999L;
        UserSignUpAndUpdateRequestDto userDTO = new UserSignUpAndUpdateRequestDto();
        userDTO.setUsername("newTestUsername");
        userDTO.setPassword("testPassword");
        userDTO.setEmail("testEmail");
        userDTO.setGender(Gender.MAN);
        doThrow(new SocialNetworkException(ErrorCode.NotExists, "User with id:" + userId + " does not exists!"))
                .when(userService).updateUser(userDTO, userId);
        mockMvc
                .perform(put("/api/user/update-user/" + userId)
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "root", roles = {"USER"})
    public void removeCurrentUserTest() throws Exception {
        mockMvc
                .perform(delete("/api/user/remove-current-user"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "root", roles = {"MODERATOR"})
    public void removeUserTest() throws Exception {
        long userId = 1L;
        mockMvc
                .perform(delete("/api/user/remove-user/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "root", roles = {"MODERATOR"})
    public void removeUserTestFails() throws Exception {
        long userId = 999L;
        doThrow(new SocialNetworkException(ErrorCode.NotExists, "User with id:" + userId + " does not exists!")).when(userService).removeUser(userId);
        mockMvc
                .perform(delete("/api/user/remove-user/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findAllUserByCountryTest() throws Exception {
        String country = "Russia";
        mockMvc
                .perform(get("/api/user/find-by-country/" + country))
                .andExpect(status().isOk());
    }

    @Test
    public void findAllUserByCountryTestFails() throws Exception {
        String country = "Spain";
        when(userService.findAllUserByCountry(country)).thenThrow(new SocialNetworkException(ErrorCode.NotExists, "Country with name:" + country + " does not exists!"));
        mockMvc
                .perform(get("/api/user/find-by-country/" + country))
                .andExpect(status().isNotFound());
    }
}
