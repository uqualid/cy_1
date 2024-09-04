package com.example.board.post.dto;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostReqUpdateDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime posted_date;
}
