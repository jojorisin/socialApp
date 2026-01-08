package se.jensen.johanna.socialapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import se.jensen.johanna.socialapp.dto.CommentDTO;
import se.jensen.johanna.socialapp.dto.CommentRequest;
import se.jensen.johanna.socialapp.dto.CommentResponse;
import se.jensen.johanna.socialapp.dto.ReplyCommentResponse;
import se.jensen.johanna.socialapp.security.MyUserDetails;
import se.jensen.johanna.socialapp.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments(@PathVariable
                                                           Long postId) {
        List<CommentDTO> commentDTOS = commentService.findAllMainComments(postId);

        return ResponseEntity.ok(commentDTOS);


    }

    @PostMapping
    public ResponseEntity<CommentResponse> postComment(@PathVariable
                                                       Long postId,
                                                       @AuthenticationPrincipal
                                                       MyUserDetails userDetails,
                                                       @RequestBody @Valid
                                                       CommentRequest commentRequest) {
        CommentResponse commentResponse = commentService.postComment(
                postId, userDetails.getUserId(), commentRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);

    }

    @PostMapping("/{commentId}")
    public ResponseEntity<ReplyCommentResponse> commentComment(@PathVariable Long postId,
                                                               @PathVariable Long commentId,
                                                               @AuthenticationPrincipal MyUserDetails userDetails,
                                                               @RequestBody @Valid CommentRequest commentRequest
    ) {

        ReplyCommentResponse commentResponse =
                commentService.replyComment(
                        postId,
                        commentId,
                        userDetails.getUserId(),
                        commentRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);


    }
}
