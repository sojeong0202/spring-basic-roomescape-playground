package roomescape.member;

import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberDao memberDao;
    private final TokenProvider tokenProvider;

    public MemberService(MemberDao memberDao, TokenProvider tokenProvider) {
        this.memberDao = memberDao;
        this.tokenProvider = tokenProvider;
    }

    public String getAccessTokenByLoginInfo(LoginRequest loginRequest) {
        return tokenProvider.makeAccessToken(getMemberByLoginInfo(loginRequest));
    }

    public MemberResponse createMember(MemberRequest memberRequest) {
        Member member = memberDao.save(
                new Member(
                        memberRequest.getName(),
                        memberRequest.getEmail(),
                        memberRequest.getPassword(),
                        "USER"));
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }

    public MemberNameResponse getName(String accessToken) {
        return new MemberNameResponse(tokenProvider.getMemberInfoByToken(accessToken));
    }

    private Member getMemberByLoginInfo(LoginRequest loginRequest) {
        return memberDao.findByEmailAndPassword(loginRequest.getEmail(), loginRequest.getPassword());
    }
}
