package com.example.board.post.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostReqDto {
    private String title;
    private String content;
    private LocalDateTime posted_date;
}
