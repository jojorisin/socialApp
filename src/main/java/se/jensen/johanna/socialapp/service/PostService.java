package se.jensen.johanna.socialapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.jensen.johanna.socialapp.dto.PostDTO;
import se.jensen.johanna.socialapp.dto.PostRequest;
import se.jensen.johanna.socialapp.dto.PostResponse;
import se.jensen.johanna.socialapp.dto.admin.AdminUpdatePostRequest;
import se.jensen.johanna.socialapp.dto.admin.AdminUpdatePostResponse;
import se.jensen.johanna.socialapp.exception.ForbiddenException;
import se.jensen.johanna.socialapp.exception.NotFoundException;
import se.jensen.johanna.socialapp.mapper.PostMapper;
import se.jensen.johanna.socialapp.model.Post;
import se.jensen.johanna.socialapp.model.User;
import se.jensen.johanna.socialapp.repository.PostRepository;
import se.jensen.johanna.socialapp.repository.UserRepository;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserRepository userRepository;


    public List<PostDTO> findAllPosts() {
        return postRepository.findAll().stream()
                .map(postMapper::toDTO).toList();

    }

    public PostDTO findPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(NotFoundException::new);

        return postMapper.toDTO(post);
    }

    public PostResponse addPost(PostRequest postRequest, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        Post post = postMapper.toPost(postRequest);
        post.setUser(user);
        postRepository.save(post);
        return postMapper.toPostResponse(post);

    }

    public PostResponse updatePost(PostRequest postRequest, Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(NotFoundException::new);
        if (!post.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("You are not authorized to edit this post");
        }
        postMapper.updatePost(postRequest, post);
        postRepository.save(post);


        return postMapper.toPostResponse(post);

    }

    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(NotFoundException::new);
        if (!post.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("You are not authorized to delete this post");
        }
        postRepository.delete(post);

    }

    public AdminUpdatePostResponse updatePostAdmin(
            AdminUpdatePostRequest adminRequest, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(NotFoundException::new);

        postMapper.updatePostAdmin(adminRequest, post);
        postRepository.save(post);

        return postMapper.toAdminUpdateResponse(post);
    }

    public void deletePostAdmin(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(NotFoundException::new);

        postRepository.delete(post);
    }
}
