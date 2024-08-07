package roomescape.member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenProvider {

    @Value("${roomescape.auth.jwt.secret}")
    private String secretKey;
    @Value("${roomescape.auth.jwt.accessTokenValidTime}")
    private int accessTokenValidTime;
    Date now = new Date();

    public String extractTokenFromCookie(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                return cookie.getValue();
            }
        }

        return "";
    }

    public String makeAccessToken(Member member) {
        return Jwts.builder()
                .setSubject(member.getId().toString())
                .claim("name", member.getName())
                .claim("email", member.getEmail())
                .claim("role", member.getRole())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public String getMemberInfoByToken(String accessToken) {
        return getClaims(accessToken).get("name").toString();
    }

    public Member getLoginMemberInfoByToken(String accessToken) {
        Claims claims = getClaims(accessToken);

        return new Member(
                claims.get("name").toString(),
                claims.get("email").toString(),
                claims.get("role").toString()
        );
    }

    private Claims getClaims(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }
}
