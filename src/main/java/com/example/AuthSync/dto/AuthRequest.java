package com.example.AuthSync.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequest {

    @NotBlank(message = "email required")
    @Email(message = "please enter valid email")
    private String email;

    @Size(max =8,message = "password should be less then 8 characters")
    @NotBlank(message = "password required")
    private String password;
}
