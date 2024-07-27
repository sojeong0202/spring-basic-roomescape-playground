package roomescape.member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private MemberDao memberDao;

    @Value("${roomescape.auth.jwt.secret}")
    private String secretKey;

    public MemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public MemberResponse createMember(MemberRequest memberRequest) {
        Member member = memberDao.save(new Member(memberRequest.getName(), memberRequest.getEmail(), memberRequest.getPassword(), "USER"));
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }

    public String getAccessTokenByLoginInfo(LoginRequest loginRequest) {
        return makeAccessToken(getMemberByLoginInfo(loginRequest));
    }

    public MemberNameResponse getMemberInfoByToken(String accessToken) {
        return new MemberNameResponse(getClaims(accessToken).get("name").toString());
    }

    public Member getLoginMemberInfoByToken(String accessToken) {
        Claims claims = getClaims(accessToken);

        return new Member(
                claims.get("id").toString(),
                claims.get("name").toString(),
                claims.get("email").toString(),
                claims.get("role").toString()
        );
    }

    private Member getMemberByLoginInfo(LoginRequest loginRequest) {
        return memberDao.findByEmailAndPassword(loginRequest.getEmail(), loginRequest.getPassword());
    }

    private String makeAccessToken(Member member) {
        return Jwts.builder()
                .setSubject(member.getId().toString())
                .claim("id", member.getId())
                .claim("name", member.getName())
                .claim("email", member.getEmail())
                .claim("role", member.getRole())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    private Claims getClaims(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }
}
