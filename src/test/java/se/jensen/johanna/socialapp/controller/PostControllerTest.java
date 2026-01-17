package se.jensen.johanna.socialapp.controller;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.jensen.johanna.socialapp.dto.PostResponseDTO;
import se.jensen.johanna.socialapp.service.PostService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @WithMockUser(username = "user1")
    @Test
    void getAllPosts() throws Exception {
        // Given
        Page<PostResponseDTO> emptyPage = Page.empty();
        when(postService.findAllPosts(any(Pageable.class))).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(postService, times(1)).findAllPosts(any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "user1")
    void shouldReturnUserPostsWhenUserIdProvided() throws Exception {
        // Given
        Long userId = 1L;
        Page<PostResponseDTO> emptyPage = Page.empty();
        when(postService.findAllPostsForUser(eq(userId), any(Pageable.class))).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/posts/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(postService, times(1)).findAllPostsForUser(eq(userId), any(Pageable.class));
    }
}

