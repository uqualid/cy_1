package com.example.board.post.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE record SET is_deleted = true WHERE record_id =  ?")
@Where(clause = "is_deleted = false")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "post_title", nullable = false)
    private String title;

    @Column(name = "post_content", nullable = false)
    private String content;

    private LocalDateTime posted_date;

    private boolean isDeleted = Boolean.FALSE; // soft delete(default - FALSE)

    public void delete() {
        this.isDeleted = true;
    }

    public void updatePost(String title, String content, LocalDateTime posted_date) {
        this.title = title;
        this.content = content;
        this.posted_date = posted_date;
    }
}
