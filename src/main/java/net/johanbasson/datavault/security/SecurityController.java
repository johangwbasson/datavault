package net.johanbasson.datavault.security;

import lombok.AllArgsConstructor;
import net.johanbasson.datavault.common.ApiError;
import net.johanbasson.datavault.system.result.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static net.johanbasson.datavault.common.Controllers.ok;

@RestController
@AllArgsConstructor
public class SecurityController {

    private final SecurityService securityService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticateRequest request) {
        Result<JwtToken, ApiError> result = securityService.authenticate(request.getEmail(), request.getPassword());
        return ok(result);
    }
}
