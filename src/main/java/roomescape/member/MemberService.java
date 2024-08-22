package roomescape.member;

import static roomescape.member.Authority.USER;

import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public MemberService(MemberRepository memberRepository, TokenProvider tokenProvider) {
        this.memberRepository = memberRepository;
        this.tokenProvider = tokenProvider;
    }

    public String getAccessTokenByLoginInfo(LoginRequest loginRequest) {
        return tokenProvider.makeAccessToken(getMemberByLoginInfo(loginRequest));
    }

    public MemberResponse createMemberRoleUser(MemberRequest memberRequest) {
        Member member = memberRepository.save(
                new Member(
                        memberRequest.getName(),
                        memberRequest.getEmail(),
                        memberRequest.getPassword(),
                        USER.toString()));
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }

    public MemberNameResponse getName(String accessToken) {
        return new MemberNameResponse(tokenProvider.getMemberInfoByToken(accessToken));
    }

    private Member getMemberByLoginInfo(LoginRequest loginRequest) {
        return memberRepository.findByEmailAndPassword(loginRequest.getEmail(), loginRequest.getPassword());
    }
}
