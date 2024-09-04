package com.example.board.user;

import com.example.board.user.dto.UserResDto;
import com.example.board.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResDto toResponse(UserEntity user){
        return UserResDto.builder()
                .userId(user.getUserId())
                .password(user.getPassword())
                .email(user.getEmail())
                .build();
    }
}
