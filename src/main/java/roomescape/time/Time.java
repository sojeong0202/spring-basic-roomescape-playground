package roomescape.time;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20, name = "time_value")
    private String value;

    private Boolean deleted = false;


    public Time(String value) {
        this.value = value;
    }

    public Time() {
    }

    public Long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public Boolean getDeleted() {
        return deleted;
    }
}
