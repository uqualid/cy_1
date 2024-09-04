package com.example.board.post.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime posted_date;
    private Boolean isDeleted;
}
