package com.example.board.post.dto;

import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResListDto {
    private List<PostResDto> postList;
    // return RecordResponseDto by List type
}
