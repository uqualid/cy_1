package com.example.board.user;

import com.example.board.user.dto.UserReqDto;
import com.example.board.user.dto.UserResDto;
import com.example.board.user.entity.UserEntity;
import com.example.board.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserResDto registerUser(UserReqDto reqDto) {
        UserEntity userEntity = UserEntity.builder()
                .userId(reqDto.getUserId())
                .password(passwordEncoder.encode(reqDto.getPassword()))
                .email(reqDto.getEmail())
                .build();

        return userMapper.toResponse(userRepository.save(userEntity));
    }

    public UserResDto authenticateUser(String userId, String password) {
        UserEntity user = userRepository.findByUserId(userId);
        if (user != null) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                return userMapper.toResponse(user);
            } else {
                System.out.println("Password does not match.");
            }
        } else {
            System.out.println("User not found.");
        }
        return null;
    }

//    public String logoutUser() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null) {
//            // 세션 무효화 및 로그아웃 로직 처리
//            SecurityContextHolder.clearContext();
//            return "Logout successful.";
//        }
//        return "User is not authenticated.";
//    }
}
