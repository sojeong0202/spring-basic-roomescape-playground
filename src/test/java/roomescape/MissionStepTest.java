package roomescape;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;
import roomescape.reservation.ReservationResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MissionStepTest {

    @Test
    @DisplayName("로그인 요청에 대한 응답 API 테스트")
    void should_getTokenInCookie_when_requestLoginWithEmailAndPassword() {
        assertThat(createToken("admin@email.com", "password")).isNotBlank();
    }

    @Test
    @DisplayName("인증 정보 조회 API 테스트")
    void should_responseMemberInfo_when_requestLoginCheckWithCookie() {
        String token = createToken("admin@email.com", "password");

        ExtractableResponse<Response> checkResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200)
                .extract();

        assertThat(checkResponse.body().jsonPath().getString("name")).isEqualTo("어드민");
    }

    @Test
    @DisplayName("ReservationReqeust에 name값이 없을 경우 로그인 정보를 활용하여 Member 조회 테스트")
    void should_createReservationWithLoginInfo_when_hasNotNameArgumentInReservationRequest() {
        String token = createToken("admin@email.com", "password");  // 일단계에서 토큰을 추출하는 로직을 메서드로 따로 만들어서 활용하세요.

        Map<String, Object> params = new HashMap<>();
        params.put("date", "2024-03-01");
        params.put("theme", "1");
        params.put("time", "1");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .post("/reservations")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.as(ReservationResponse.class).getName()).isEqualTo("어드민");
    }

    @Test
    @DisplayName("ReservationRequest에 name값이 있을 경우 name으로 Member 조회 테스트")
    void should_createReservationWithName_when_hasNameArgumentInReservationRequest() {
        String token = createToken("admin@email.com", "password");  // 일단계에서 토큰을 추출하는 로직을 메서드로 따로 만들어서 활용하세요.

        Map<String, Object> params = new HashMap<>();
        params.put("date", "2024-03-01");
        params.put("theme", "1");
        params.put("time", "1");
        params.put("name", "브라운");

        ExtractableResponse<Response> adminResponse = RestAssured.given().log().all()
                .body(params)
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .post("/reservations")
                .then().log().all()
                .extract();

        assertThat(adminResponse.statusCode()).isEqualTo(201);
        assertThat(adminResponse.as(ReservationResponse.class).getName()).isEqualTo("브라운");
    }

    @Test
    @DisplayName("admin 권한 없는 사람이 어드민 페이지에 진입할 경우 401 응답 코드 테스트")
    void should_responseStatusCode401_when_hasNotAdminRoleRequestAdminPage() {
        String brownToken = createToken("brown@email.com", "password");

        RestAssured.given().log().all()
                .cookie("token", brownToken)
                .get("/admin")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("admin 권한 있는 사람이 어드민 페이지에 진입할 경우 200 응답 코드 테스트")
    void should_responseStatusCode200_when_hasAdminRoleRequestAdminPage() {
        String adminToken = createToken("admin@email.com", "password");

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    private String createToken(String email, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract();

        return  response.headers().get("Set-Cookie").getValue().split(";")[0].split("=")[1];
    }
}
