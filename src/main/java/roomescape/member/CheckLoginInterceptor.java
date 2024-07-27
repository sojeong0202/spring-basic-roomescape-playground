package roomescape.member;

import static roomescape.Parser.extractTokenFromCookie;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class CheckLoginInterceptor implements HandlerInterceptor {

    private final MemberService memberService;

    public CheckLoginInterceptor(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Member member = memberService.getLoginMemberInfoByToken(extractTokenFromCookie(request.getCookies()));

        if (member == null || !member.getRole().equals("ADMIN")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return false;
        }

        return true;
    }
}
