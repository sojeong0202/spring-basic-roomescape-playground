package roomescape.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    public MemberController(MemberService memberService, TokenProvider tokenProvider) {
        this.memberService = memberService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/members")
    public ResponseEntity createMemberRoleUser(@RequestBody MemberRequest memberRequest) {
        MemberResponse member = memberService.createMemberRoleUser(memberRequest);

        return ResponseEntity.created(URI.create("/members/" + member.getId())).body(member);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Cookie cookie = new Cookie("token", memberService.getAccessTokenByLoginInfo(loginRequest));
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/login/check")
    public ResponseEntity getMemberInfoByCookie(HttpServletRequest request) {

        return ResponseEntity.ok()
                .body(memberService.getName(
                        tokenProvider.extractTokenFromCookie(request.getCookies())
                ));
    }

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}
