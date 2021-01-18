package social.network.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import social.network.dto.UserGetResponseDto;
import social.network.dto.UserLogInRequestDto;
import social.network.dto.UserSignUpAndUpdateRequestDto;
import social.network.exception.SocialNetworkException;
import social.network.mapper.UserMapper;
import social.network.model.ErrorCode;
import social.network.model.Gender;
import social.network.repository.UserRepository;
import social.network.security.jwt.JwtUtils;
import social.network.service.UserService;

import static org.mockito.ArgumentMatchers.any;
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
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserMapper userMapper;

    @Test
    public void registerTest() throws Exception {
        UserSignUpAndUpdateRequestDto userDTO = new UserSignUpAndUpdateRequestDto();
        userDTO.setUsername("testUsername");
        userDTO.setPassword("testPassword");
        userDTO.setEmail("testEmail");
        userDTO.setGender(Gender.MAN);
        mockMvc
                .perform(post("/api/user/signup")
                        .content(objectMapper.writeValueAsString(userDTO))
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
        UserLogInRequestDto userDTO = new UserLogInRequestDto();
        userDTO.setUsername("testUsername");
        userDTO.setPassword("testPassword");
        mockMvc
                .perform(post("/api/user/signin").content(objectMapper.writeValueAsString(userDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void LoginTestFails() throws Exception {
        UserLogInRequestDto notExistsUser = new UserLogInRequestDto();
        notExistsUser.setUsername("testUsername");
        notExistsUser.setPassword("testPassword");
        doThrow(new SocialNetworkException(ErrorCode.UserDoesNotExists, "User with id:" + notExistsUser.getUsername() + " does not exists!")).when(userService).authenticateUser(notExistsUser);
        mockMvc
                .perform(post("/api/user/signin").content(objectMapper.writeValueAsString(notExistsUser)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findUserTest() throws Exception {
        long userId = 1L;
        UserGetResponseDto userDTO = new UserGetResponseDto();
        userDTO.setId(userId);
        userDTO.setUsername("testUsername");
        when(userService.findUser(userId)).thenReturn(userDTO);
        mockMvc
                .perform(get("/api/user/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    public void findUserTestFails() throws Exception {
        long userId = 999L;
        when(userService.findUser(userId)).thenThrow(new SocialNetworkException(ErrorCode.UserDoesNotExists, "User with id:" + userId + " does not exists!"));
        mockMvc
                .perform(get("/api/user/" + userId))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(roles = "USER")
    public void updateCurrentUserTest() throws Exception {
        UserSignUpAndUpdateRequestDto updatedUser = new UserSignUpAndUpdateRequestDto();
        updatedUser.setUsername("newTestUsername");
        updatedUser.setPassword("testPassword");
        updatedUser.setEmail("testEmail@mail.com");
        updatedUser.setGender(Gender.MAN);
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(updateUserDTO.getUsername(), updateUserDTO.getPassword()));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String jwt = jwtUtils.generateJwtToken(authentication);
        mockMvc
                .perform(
                        put("/api/user/update-current-user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedUser)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void updateUserTest() throws Exception {
        long userId = 1L;
        UserSignUpAndUpdateRequestDto userDTO = new UserSignUpAndUpdateRequestDto();
        userDTO.setUsername("newTestUsername");
        userDTO.setPassword("testPassword");
        userDTO.setEmail("testEmail");
        userDTO.setGender(Gender.MAN);
        mockMvc
                .perform(put("/api/user/update-user/" + userId)
                        .content(objectMapper.writeValueAsString(userDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void updateUserTestFails() throws Exception {
        long userId = 999L;
        UserSignUpAndUpdateRequestDto userDTO = new UserSignUpAndUpdateRequestDto();
        userDTO.setUsername("newTestUsername");
        userDTO.setPassword("testPassword");
        userDTO.setEmail("testEmail");
        userDTO.setGender(Gender.MAN);
        doThrow(new SocialNetworkException(ErrorCode.UserDoesNotExists, "User with id:" + userId + " does not exists!")).when(userService).updateUser(userDTO, userId);
        mockMvc
                .perform(put("/api/user/update-user" + userId).content(objectMapper.writeValueAsString(userDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void removeCurrentUserTest() throws Exception {
        mockMvc
                .perform(delete("/api/user/remove-current-user"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void removeUserTest() throws Exception {
        long userId = 1L;
        mockMvc
                .perform(delete("/api/user/remove-user" + userId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void removeUserTestFails() throws Exception {
        long userId = 999L;
        doThrow(new SocialNetworkException(ErrorCode.UserDoesNotExists, "User with id:" + userId + " does not exists!")).when(userService).removeUser(userId);
        mockMvc
                .perform(delete("/api/user/remove-user" + userId))
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
        when(userService.findAllUserByCountry(country)).thenThrow(new SocialNetworkException(ErrorCode.CountryDoesNotExists, "Country with name:" + country + " does not exists!"));
        mockMvc
                .perform(get("/api/user/find-by-country/" + country))
                .andExpect(status().isNotFound());
    }
}
