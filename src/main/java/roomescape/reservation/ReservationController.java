package roomescape.reservation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import roomescape.member.AuthUser;
import roomescape.member.LoginMember;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> list() {
        return reservationService.findAll();
    }

    @PostMapping("/reservations")
    public ResponseEntity create(@RequestBody ReservationRequest reservationRequest,
                                 @AuthUser(required = false) LoginMember loginMember) {

        if (reservationRequest.getDate() == null
                || reservationRequest.getTheme() == null
                || reservationRequest.getTime() == null) {
            return ResponseEntity.badRequest().build();
        }

        if (loginMember.getRole() == "ADMIN") {
            reservationRequest = new ReservationRequest(
                    reservationRequest.getName(),
                    reservationRequest.getDate(),
                    reservationRequest.getTheme(),
                    reservationRequest.getTime());
        }

        if (loginMember.getRole() == "USER" || reservationRequest.getName() == null) {
            reservationRequest = new ReservationRequest(
                    loginMember.getName(),
                    reservationRequest.getDate(),
                    reservationRequest.getTheme(),
                    reservationRequest.getTime());
        }
        ReservationResponse reservation = reservationService.save(reservationRequest);

        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId())).body(reservation);
    }

    @GetMapping("/reservations-mine")
    public ResponseEntity findAllByMember(@AuthUser LoginMember loginMember) {
        return ResponseEntity.ok().body(reservationService.findAllByMember(loginMember));
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
