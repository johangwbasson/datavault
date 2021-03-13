package net.johanbasson.datavault.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CreateUserRequest {

    private String email;
    private String password;

}
