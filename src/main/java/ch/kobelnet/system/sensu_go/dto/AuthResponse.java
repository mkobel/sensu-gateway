package ch.kobelnet.system.sensu_go.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {

    @JsonProperty("access_token")
    String accessToken;
    @JsonProperty("expires_at")
    long expiresAt;
    @JsonProperty("refresh_token")
    String refreshToken;

}
