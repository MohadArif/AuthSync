package com.example.AuthSync.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntityResponse {

     private String userId;
     private String name;
     private String email;
     private Boolean isAccountActive;
}
