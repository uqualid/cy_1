package com.example.board.auth;

import com.example.board.auth.entity.UserAuthEntity;
import com.example.board.auth.repository.UserAuthRepository;
import com.example.board.security.JwtTokenProvider;
import com.example.board.user.dto.LoginReqDto;
import com.example.board.user.dto.LoginResDto;
import com.example.board.user.entity.UserEntity;
import com.example.board.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
        // 아이디 존재 x시 exception handling
        if(!passwordEncoder.matches(loginReqDto.getPassword(), userEntity.getPassword()))
            throw new RuntimeException("check Password");

        StringBuilder expire = new StringBuilder();
        String accessToken = jwtTokenProvider.createToken(String.valueOf(userEntity.getUserId()), null, expire);
        StringBuilder refreshExpire = new StringBuilder();
        String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(userEntity.getUserId()), null, expire);

        userAuthRepository.save(UserAuthEntity.builder()
                .ownNum(userEntity.getOwnNum())
                .userId(userEntity.getUserId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .logout(false)
                .build());

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

    }

    @Transactional
    public LoginResDto refreshToken(String id, String accessToken, String refreshToken) {
        if(jwtTokenProvider.validateToken(refreshToken)){

            if (jwtTokenProvider.getUserPk(refreshToken).equals(id)) {
                UserAuthEntity auth = userAuthRepository.findByRefreshToken(refreshToken).orElseThrow(IllegalArgumentException::new);
                UserEntity userEntity = userRepository.findByUserId(auth.getUserId());

                if (userEntity.getUserId().equals(id) && auth.getAccessToken().equals(accessToken)){
                    StringBuilder expire = new StringBuilder();
                    String newAccessToken = jwtTokenProvider.createToken(String.valueOf(userEntity.getUserId()), null, expire);

                    StringBuilder refreshExpire = new StringBuilder();
                    String newRefreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(userEntity.getUserId()), null, refreshExpire);

                    userAuthRepository.save(UserAuthEntity.builder()
                                    .userId(userEntity.getUserId())
                                    .accessToken(newAccessToken)
                                    .refreshToken(newRefreshToken)
                                    .logout(false)
                            .build());

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
