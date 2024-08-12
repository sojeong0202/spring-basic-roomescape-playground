package roomescape.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class CheckLoginInterceptor implements HandlerInterceptor {

    private final TokenProvider tokenProvider;

    public CheckLoginInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Member member = tokenProvider.getLoginMemberInfoByToken(
                tokenProvider.extractTokenFromCookie(request.getCookies()));

        if (member == null || !member.getRole().equals("ADMIN")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return false;
        }

        return true;
    }
}
