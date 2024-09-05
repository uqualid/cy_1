package com.example.board.auth;

import com.example.board.auth.dto.JwtTokenDto;
import com.example.board.security.JwtFilter;
import com.example.board.security.JwtTokenProvider;
import com.example.board.user.dto.LoginReqDto;
import com.example.board.user.dto.LoginResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.xml.ws.Response;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.concurrent.Future;

@Tag(name = "JWT User Controller", description = "Token 발급 로그인/로그아웃")
@RequiredArgsConstructor
//@NoArgsConstructor(force = true)
@RestController
@RequestMapping("/jwtTokenUse")
public class UserAuthController {
    private final UserAuthService userAuthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Operation(summary = "Login with JWT Token", description = "Login(JWT 토큰 발행)")
    @PostMapping(value = "/login")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<JwtTokenDto> loginByUserIdAndPassword(@Parameter(required = true, name = "reqDto", description = "로그인 정보")
                                                             @RequestBody LoginReqDto loginReqDto){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginReqDto.getUserId(), loginReqDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        StringBuilder expire = new StringBuilder();
        String jwt = jwtTokenProvider.createToken(loginReqDto.getUserId(), null, expire);

        HttpHeaders httpheaders = new HttpHeaders();
        httpheaders.add("Authorization", "Bearer " + jwt);

        return new ResponseEntity<>(new JwtTokenDto(jwt), httpheaders, HttpStatus.OK);
    }

    @Operation(summary = "logout with JWT Token", description = "Logout(JWT 토큰)")
    @PostMapping(value = "/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization") String accessToken){
        userAuthService.logout(jwtTokenProvider.getUserPk(accessToken));
        return ResponseEntity.ok("Logout Success");
    }

    @Operation(summary = "refresh JWT Token")
    @PostMapping(value = "/refresh-token/{userId}")
    public ResponseEntity<String> refreshToken(@Parameter(required = true, name = "userId", description = "사용자 아이디")
                                                    @PathVariable String userId,
                                                    @RequestHeader(value = "x-api-token") String accessToken,
                                                    @RequestHeader(value = "x-refresh-token") String refreshToken) {
        return new ResponseEntity(userAuthService.refreshToken(userId, accessToken, refreshToken), HttpStatus.OK);

    }

}
