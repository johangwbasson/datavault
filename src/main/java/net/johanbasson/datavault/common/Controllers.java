package net.johanbasson.datavault.common;

import net.johanbasson.datavault.system.result.Result;
import org.springframework.http.ResponseEntity;

public class Controllers {

    public static <T> ResponseEntity<?> ok(Result<T, ApiError> result) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue());
        } else {
            return ResponseEntity.badRequest().body(result.getError().toErrorMessage());
        }
    }
}
