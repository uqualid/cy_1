package com.example.board.security;

import com.example.board.auth.CustomUserDetailService;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    @Value("spring.jwt.secret")
    private String secretKey;

    private final CustomUserDetailService customUserDetailService;

//    public JwtTokenProvider(
//            @Value("${jwt.secret}") String secret,
//            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
//        this.secretKey = secret;
//        this.accessTokenValidityInMs = tokenValidityInSeconds * 60 * 24; // 60,000ms : 1m(0.001d), 60000 * 60 * 12 = 12h
//        this.refreshTokenValidityInMs = tokenValidityInSeconds * 60 * 24 * 2; // 60,000ms : 1m(0.001d), 60000 * 60 * 24 = 1d
//    }


    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String userPk, List<String> roles, StringBuilder expire) {
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", roles);
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + 1000L * 60 * 60 * 24);
        expire.append(expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // created date
                .setExpiration(expireTime) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();
    }

    public String createRefreshToken(String userPk, List<String> roles, StringBuilder expire) {
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", roles);
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + 1000L * 60 * 60 * 24 * 30);
        expire.append(expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // created date
                .setExpiration(expireTime) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();
    }

    public String createTokenByExpire(String userPk, List<String> roles, Long expire) {
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", roles);
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + expire);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // created date
                .setExpiration(expireTime) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        return req.getHeader("x-api-token");
    }

    public boolean validateToken(String token) {
        try {
            Date now = new Date();
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            Date expire = claims.getBody().getExpiration();
            if (expire.before(now)) {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                log.error("token expire : expire({})/now({})", dateFormat.format(expire), dateFormat.format(now));
            }
            return !expire.before(now);
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다. : {}({})", token, e.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다. : {}({})", token, e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다. : {}({})", token, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다. : {}({})", token, e.getMessage());
        } catch (Exception e) {
            log.error("invalid token : {}({})", token, e.getMessage());
        }
        return false;
    }
}
