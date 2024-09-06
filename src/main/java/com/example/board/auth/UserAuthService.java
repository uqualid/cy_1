package com.example.board.auth;

import com.example.board.auth.entity.UserAuthEntity;
import com.example.board.auth.repository.UserAuthRepository;
import com.example.board.security.JwtTokenProvider;
import com.example.board.user.dto.LoginReqDto;
import com.example.board.user.dto.LoginResDto;
import com.example.board.user.entity.UserEntity;
import com.example.board.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Service
public class UserAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserAuthRepository userAuthRepository;

    @Transactional
    public LoginResDto loginByUserIdAndPassword(LoginReqDto loginReqDto){
        UserEntity userEntity = userRepository.findByUserId(loginReqDto.getUserId());
        if(userEntity == null) { // 예외: 사용자가 존재하지 않을 경우
            throw new RuntimeException("User not found with ID: " + loginReqDto.getUserId());
        }

        // 예외: 비밀번호가 일치하지 않는 경우
        if(!passwordEncoder.matches(loginReqDto.getPassword(), userEntity.getPassword())) {
            throw new RuntimeException("Check Password");
        }

        StringBuilder expire = new StringBuilder();
        String accessToken = jwtTokenProvider.createToken(String.valueOf(userEntity.getUserId()), null, expire);
        StringBuilder refreshExpire = new StringBuilder();
        String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(userEntity.getUserId()), null, expire);

        UserAuthEntity userAuthEntity = UserAuthEntity.builder()
                .ownNum(userEntity.getOwnNum())
                .userId(userEntity.getUserId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .logout(false)
                .build();

        UserAuthEntity savedEntity = userAuthRepository.save(userAuthEntity);
        System.out.println("Saved UserAuthEntity: " + savedEntity);

        return LoginResDto.builder()
                .ownNum(userEntity.getOwnNum())
                .userId(userEntity.getUserId())
                .accessToken(accessToken)
                .accessExpireAt(expire.toString())
                .refreshToken(refreshToken)
                .refreshExpireAt(refreshExpire.toString())
                .build();
    }

    @Transactional
    public void logout(String userId){
        UserAuthEntity auth = userAuthRepository
                .findFirstByUserIdAndLogout(userId, false)
                .orElseThrow();

        auth.setLogout();

        log.info("Logout state before save: {}", auth.getLogout());
        userAuthRepository.save(auth);
        log.info("Logout state after save: {}", auth.getLogout());
    }

    @Transactional
    public LoginResDto refreshToken(String id, String accessToken, String refreshToken) {
        if(jwtTokenProvider.validateToken(refreshToken)){

            if (jwtTokenProvider.getUserPk(refreshToken).equals(id)) {
                UserAuthEntity auth = userAuthRepository.findByRefreshToken(refreshToken)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

                UserEntity userEntity = userRepository.findByUserId(auth.getUserId());
                if (userEntity == null || !userEntity.getUserId().equals(id) || !auth.getAccessToken().equals(accessToken)) {
                    throw new IllegalArgumentException("Invalid token or user");
                }

                if (userEntity.getUserId().equals(id) && auth.getAccessToken().equals(accessToken)){
                    StringBuilder expire = new StringBuilder();
                    String newAccessToken = jwtTokenProvider.createToken(String.valueOf(userEntity.getUserId()), null, expire);

                    StringBuilder refreshExpire = new StringBuilder();
                    String newRefreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(userEntity.getUserId()), null, refreshExpire);

                    auth.setAccessToken(newAccessToken);
                    auth.setRefreshToken(newRefreshToken);
                    userAuthRepository.save(auth);

                    return LoginResDto.builder()
                            .ownNum(userEntity.getOwnNum())
                            .userId(userEntity.getUserId())
                            .accessToken(newAccessToken)
                            .refreshToken(newRefreshToken)
                            .accessExpireAt(expire.toString())
                            .refreshExpireAt(expire.toString())
                            .build();
                }
            }
        }
        throw new IllegalArgumentException();

    }

}
