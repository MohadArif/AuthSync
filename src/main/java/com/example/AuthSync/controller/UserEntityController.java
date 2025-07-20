package com.example.AuthSync.controller;

import com.example.AuthSync.dto.UserEntityRequest;
import com.example.AuthSync.dto.UserEntityResponse;
import com.example.AuthSync.service.MailService;
import com.example.AuthSync.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class UserEntityController {

    private final UserService userService;
    private final MailService mailService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserEntityRequest userEntityRequest){
        UserEntityResponse userEntityResponse = userService.createUser(userEntityRequest);
        // email functionality
        mailService.welcomeMail(userEntityResponse.getEmail(), userEntityResponse.getName());
        log.info("welcome mail has been sent to {}",userEntityResponse.getEmail());
        return new ResponseEntity<>(userEntityResponse, HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@CurrentSecurityContext(expression = "authentication?.name")String email){
        UserEntityResponse userProfile = userService.getUserProfile(email);
        return new ResponseEntity<>(userProfile,HttpStatus.OK);
    }

}
