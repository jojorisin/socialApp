package se.jensen.johanna.socialapp.mapper;

<<<<<<< HEAD
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import se.jensen.johanna.socialapp.dto.comment.CommentDTO;
import se.jensen.johanna.socialapp.dto.comment.CommentRequest;
import se.jensen.johanna.socialapp.dto.comment.CommentResponse;
import se.jensen.johanna.socialapp.dto.ReplyCommentResponse;
=======
import org.mapstruct.*;
import se.jensen.johanna.socialapp.dto.*;
>>>>>>> origin/main
import se.jensen.johanna.socialapp.model.Comment;

@Mapper(componentModel = "spring", imports = {java.time.LocalDateTime.class})
public interface CommentMapper {

    @Mapping(target = "updatedAt", ignore = true)
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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "parent", ignore = true)
    void updateComment(CommentRequest commentRequest, @MappingTarget Comment comment);

    UpdateCommentResponse toUpdateCommentResponse(Comment comment);
}
