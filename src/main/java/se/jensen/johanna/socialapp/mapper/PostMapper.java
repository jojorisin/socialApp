package se.jensen.johanna.socialapp.mapper;

import org.mapstruct.*;
import se.jensen.johanna.socialapp.dto.PostDTO;
import se.jensen.johanna.socialapp.dto.PostRequest;
import se.jensen.johanna.socialapp.dto.PostResponse;
import se.jensen.johanna.socialapp.dto.admin.AdminUpdatePostRequest;
import se.jensen.johanna.socialapp.dto.admin.AdminUpdatePostResponse;
import se.jensen.johanna.socialapp.model.Post;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface PostMapper {

    @Mapping(target = "userId", source = "post.user.userId")
    @Mapping(target = "username", source = "post.user.username")
    PostDTO toDTO(Post post);

    Post toPost(PostRequest postRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePost(PostRequest postRequest, @MappingTarget Post post);

    @Mapping(target = "postId", source = "postId")
    PostResponse toPostResponse(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePostAdmin(AdminUpdatePostRequest adminRequest, @MappingTarget Post post);

    @Mapping(target = "userId", source = "post.user.userId")
    AdminUpdatePostResponse toAdminUpdateResponse(Post post);
}

