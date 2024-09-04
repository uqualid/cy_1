package com.example.board.user;

import com.example.board.user.dto.UserReqDto;
import com.example.board.user.dto.UserReqLoginDto;
import com.example.board.user.dto.UserResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Controller", description = "로그인/ 회원가입 API")
@RequiredArgsConstructor
@RestController
@RequestMapping
public class UserController {
    @Autowired
    private final UserService userService;


    @PostMapping("/user/register")
    @Operation(
            summary = "User Register", description = "회원가입", method = "POST",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            }
    )
    public ResponseEntity<String> registerUser(@RequestBody UserReqDto request) {
        UserResDto response = userService.registerUser(request);
        return ResponseEntity.ok("Registration successful. Redirect to /login.");
    }

    @PostMapping("/user/login")
    @Operation(summary = "User Login", description = "User 로그인", method = "POST")
    public ResponseEntity<String> loginUser(@RequestBody UserReqLoginDto request) {
        UserResDto user = userService.authenticateUser(request.getUserId(), request.getPassword());

        if (user != null) {
            return ResponseEntity.ok("Login successful. Welcome " + user.getUserId() + "!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }

    @PostMapping("/user/logout")
    @Operation(summary = "User Logout", description = "User 로그아웃", method = "POST")
    public ResponseEntity<String> logoutUser() {
        // 로그아웃 로직 처리
        return ResponseEntity.ok("Logout successful");
    }



}
