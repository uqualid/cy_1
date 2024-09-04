package com.example.board.post;

import com.example.board.post.dto.PostResDto;
import com.example.board.post.dto.PostResListDto;
import com.example.board.post.entity.PostEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostMapper {
    public PostResDto toResponse(PostEntity post){ // from Entity
        return PostResDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .posted_date(post.getPosted_date())
                .isDeleted(post.isDeleted())
                .build();
    }

    public PostResListDto toListResponse(List<PostEntity> postList){
        List<PostResDto> postResDtoList
                = postList.stream().map(this::toResponse).collect(Collectors.toList());
        return PostResListDto.builder().postList(postResDtoList).build();
    }

}
