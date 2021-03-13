package net.johanbasson.datavault.users;

import lombok.AllArgsConstructor;
import net.johanbasson.datavault.common.ApiError;
import net.johanbasson.datavault.system.CreateResult;
import net.johanbasson.datavault.system.result.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static net.johanbasson.datavault.common.Controllers.ok;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @ResponseBody
    @PostMapping(value = "/users", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> create(@RequestBody CreateUserRequest request) {
        Result<CreateResult, ApiError> result = userService.create(request.getEmail(), request.getPassword(), Role.USER);
        return ok(result);
    }
}
