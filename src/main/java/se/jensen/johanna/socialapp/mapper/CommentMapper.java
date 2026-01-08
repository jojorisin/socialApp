package se.jensen.johanna.socialapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import se.jensen.johanna.socialapp.dto.CommentDTO;
import se.jensen.johanna.socialapp.dto.CommentRequest;
import se.jensen.johanna.socialapp.dto.CommentResponse;
import se.jensen.johanna.socialapp.dto.ReplyCommentResponse;
import se.jensen.johanna.socialapp.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment toComment(CommentRequest commentRequest);

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "postId", source = "post.postId")
    CommentResponse toResponse(Comment comment);

    @Mapping(target = "parentId", source = "parent.commentId")
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "username", source = "user.username")
    ReplyCommentResponse toReplyCommentResponse(Comment comment);

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "username", source = "user.username")
    CommentDTO toCommentDTO(Comment comment);
}
