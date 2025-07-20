package com.example.AuthSync.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordRequest {

    @NotBlank(message = "email required")
    @Email(message = "please enter valid email")
    private String email;
    @NotBlank(message = "please enter 6 digit Otp")
    private String otp;
    @NotBlank(message = "password required")
    private String newPassword;

}
