package com.example.board.post;


import com.example.board.post.dto.PostReqDto;
import com.example.board.post.dto.PostResDto;
import com.example.board.post.dto.PostResListDto;
import com.example.board.post.dto.PostReqUpdateDto;
import com.example.board.post.entity.PostEntity;
import com.example.board.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    //Create
    @Transactional
    public PostResDto createPost(PostReqDto reqDto){
        PostEntity post = PostEntity.builder()
                .title(reqDto.getTitle())
                .content(reqDto.getContent())
                .posted_date(reqDto.getPosted_date())
                .build();

        return postMapper.toResponse(postRepository.save(post));
    }

    //Read - by One
    public PostResDto findOnePost(Long id){
        PostEntity post = postRepository.findById(id)
                .orElseThrow(IllegalStateException::new); //exception handle

        return postMapper.toResponse(post);
    }

    //Read - by List
    public PostResListDto findAllPost(){
        List<PostEntity> postList = postRepository.findAll();

        return postMapper.toListResponse(postList);
    }

    // Update - with Time Change
    @Transactional
    public PostResDto updatePost(PostReqUpdateDto request, Long id){
        id = request.getId();

        PostEntity post = postRepository.findById(request.getId()) // find post
                .orElseThrow(IllegalStateException::new); // - exception handle

        post.updatePost(request.getTitle(), request.getContent(), request.getPosted_date()); // update

        return postMapper.toResponse(postRepository.save(post));
    }

    // Delete - soft delete
    @Transactional
    public PostResDto deletePost(Long id){
        PostEntity post = postRepository.findById(id) // find post
                .orElseThrow(IllegalStateException::new); // - exception handle

        post.delete(); // soft delete

        return postMapper.toResponse(post);
    }

}
