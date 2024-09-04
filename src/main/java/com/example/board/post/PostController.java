package com.example.board.post;

import com.example.board.post.dto.PostReqDto;
import com.example.board.post.dto.PostResDto;
import com.example.board.post.dto.PostResListDto;
import com.example.board.post.dto.PostReqUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post Controller", description = "게시판 API")
@RequiredArgsConstructor
@RestController
@RequestMapping
public class PostController {
    @Autowired
    private final PostService postService;

    @PostMapping("/post") //Create
    @Operation(summary = "Create Post", description = "게시글 삽입")
    public ResponseEntity<PostResDto> createPost(@RequestBody PostReqDto request){
        PostResDto response = postService.createPost(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/post/{id}") // Read - one by Id
    @Operation(summary = "Read One Post by ID", description = "게시글 단건 조회")
    public ResponseEntity<PostResDto> findOnePost(@PathVariable Long id){
        return ResponseEntity.ok(postService.findOnePost(id));
    }

    @GetMapping("/post") // Read - List of All
    @Operation(summary = "Read List of Post", description = "게시글 목록 조회")
    public ResponseEntity<PostResListDto> findAllPost(){
        return ResponseEntity.ok(postService.findAllPost());
    }

    @PutMapping("/post/{id}") // Update
    @Operation(summary = "Update Post", description = "게시글 수정")
    public ResponseEntity<PostResDto> updatePost(@RequestBody PostReqUpdateDto request, @PathVariable Long id){
        return ResponseEntity.ok(postService.updatePost(request, id));
    }

    @DeleteMapping("/post/{id}") // Delete - soft
    @Operation(summary = "Delete Post by ID", description = "게시글 삭제(Soft Delete)")
    public ResponseEntity<PostResDto> deletePost(@PathVariable Long id){
        return ResponseEntity.ok(postService.deletePost(id));
    }
}
