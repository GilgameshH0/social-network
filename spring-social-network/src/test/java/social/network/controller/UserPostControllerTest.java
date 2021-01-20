package social.network.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import social.network.dto.PostRequestDto;
import social.network.dto.PostResponseDto;
import social.network.exception.SocialNetworkException;
import social.network.model.ErrorCode;
import social.network.service.UserPostService;
import social.network.service.UserService;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest()
class UserPostControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserPostService userPostService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "root", roles = {"USER"})
    void createPostOnUserWallTest() throws Exception {
        PostRequestDto postDto = new PostRequestDto();
        postDto.setText("New year!");
        mockMvc
                .perform(post("/api/post/create-post-on-user-wall")
                        .content(objectMapper.writeValueAsString(postDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "root", roles = {"USER"})
    void updatePostOnUserWallTest() throws Exception {
        PostRequestDto postDto = new PostRequestDto();
        postDto.setText("New Monitor!");
        mockMvc
                .perform(post("/api/post/create-post-on-user-wall")
                        .content(objectMapper.writeValueAsString(postDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "root", roles = {"USER"})
    void updatePostOnUserWallTestFails() throws Exception {
        PostRequestDto postDto = new PostRequestDto();
        postDto.setText("New Monitor!");
        long postId = 999L;
        doThrow(new SocialNetworkException(ErrorCode.NotExists, "Post with id:" + postId + " does not exists!"))
                .when(userPostService).updatePostOnUserWall("root", postDto, postId);
        mockMvc
                .perform(put("/api/post/update-post-on-user-wall/" + postId)
                        .content(objectMapper.writeValueAsString(postDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void findPostOnUserWallTest() throws Exception {
        PostResponseDto postDto = new PostResponseDto();
        postDto.setText("New Year!");
        long postId = 1L;
        when(userPostService.findPostOnUserWall(postId)).thenReturn(postDto);
        mockMvc
                .perform(get("/api/post/find-post-on-user-wall/" + postId))
                .andExpect(status().isOk());
    }

    @Test
    void findPostOnUserWallTestFails() throws Exception {
        long postId = 999L;
        when(userPostService.findPostOnUserWall(postId)).thenThrow(new SocialNetworkException(ErrorCode.NotExists, "Post with id:" + postId + " does not exists!"));
        mockMvc
                .perform(get("/api/post/find-post-on-user-wall/" + postId))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllPostsOnUserWallTest() {
    }

    @Test
    @WithMockUser(username = "root", roles = {"USER"})
    void removePostFromUserWallCurrentUserTest() {
    }

    @Test
    @WithMockUser(username = "root", roles = {"ADMIN"})
    void removePostFromUserWallTest() {
    }
}