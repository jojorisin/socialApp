package se.jensen.johanna.socialapp.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import se.jensen.johanna.socialapp.dto.PostDTO;
import se.jensen.johanna.socialapp.dto.PostRequest;
import se.jensen.johanna.socialapp.dto.PostResponse;
import se.jensen.johanna.socialapp.dto.admin.AdminUpdatePostRequest;
import se.jensen.johanna.socialapp.dto.admin.AdminUpdatePostResponse;
import se.jensen.johanna.socialapp.security.MyUserDetails;
import se.jensen.johanna.socialapp.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{postId}")
    public ResponseEntity<AdminUpdatePostResponse> updatePostAdmin(@RequestBody
                                                                   AdminUpdatePostRequest
                                                                           adminRequest,
                                                                   @PathVariable Long postId) {
        AdminUpdatePostResponse adminUpdatePostResponse =
                postService.updatePostAdmin(adminRequest, postId);

        return ResponseEntity.ok(adminUpdatePostResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{postId}")
    public ResponseEntity<Void> deletePostAdmin(@PathVariable Long postId) {
        postService.deletePostAdmin(postId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> postDTOs = postService.findAllPosts();

        return ResponseEntity.ok(postDTOs);

    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long postId) {
        PostDTO postDTO = postService.findPost(postId);
        return ResponseEntity.ok(postDTO);
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<PostResponse> post(@AuthenticationPrincipal
                                             MyUserDetails userDetails,
                                             @RequestBody @Valid PostRequest post) {
        PostResponse postResponse = postService.addPost(post, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(postResponse);

    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> editPost(@AuthenticationPrincipal
                                                 MyUserDetails userDetails,
                                                 @PathVariable Long postId,
                                                 @RequestBody @Valid PostRequest postRequest) {

        PostResponse postResponse = postService.updatePost(
                postRequest,
                postId,
                userDetails.getUserId());

        return ResponseEntity.ok(postResponse);

    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@AuthenticationPrincipal
                                           MyUserDetails userDetails,
                                           @PathVariable Long postId) {
        postService.deletePost(postId, userDetails.getUserId());

        return ResponseEntity.noContent().build();
    }
}
