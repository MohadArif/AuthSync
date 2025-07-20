package com.example.AuthSync.controller;

import com.example.AuthSync.config.CustomUserDetailService;
import com.example.AuthSync.dto.AuthRequest;
import com.example.AuthSync.dto.AuthResponse;
import com.example.AuthSync.dto.ResetPasswordRequest;
import com.example.AuthSync.service.UserService;
import com.example.AuthSync.utility.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Log4j2
@CrossOrigin(origins = "http://localhost:5173" , allowCredentials = "true")
public class LoginController {

    private final CustomUserDetailService customUserDetailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest){
        try {
            log.info("this is login method entry point");
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
            UserDetails userDetails = customUserDetailService.loadUserByUsername(authRequest.getEmail());
            String jwtToken = jwtService.generateToken(userDetails);
            ResponseCookie cookie= ResponseCookie.from("jwtToken",jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("strict")
                    .build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString())
                    .body(new AuthResponse(authRequest.getEmail(),jwtToken));
        }catch(BadCredentialsException ex){
            Map<String,Object> error=new HashMap<>();
            error.put("error",true);
            error.put("message","Email or password is incorrect");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }catch (DisabledException ex){
            Map<String,Object> error=new HashMap<>();
            error.put("error",true);
            error.put("message","Account is disable");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }catch (Exception ex){
            Map<String,Object> error=new HashMap<>();
            error.put("error",true);
            error.put("message","Authentication failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

    }

    @GetMapping("/isAuthenticated")
    public ResponseEntity<?> isAuthenticated(@CurrentSecurityContext(expression = "authentication?.name")String email){
        return new ResponseEntity<>(email!=null,HttpStatus.OK);
    }

    @PostMapping("/send-reset-otp")
    public ResponseEntity<?> sendRestOtp(@RequestParam String email){
        log.info("this is send otp entry point");
        userService.sendRestOtp(email);
        return new ResponseEntity<>("otp sent to register no",HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> restPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest){
        userService.resetPassword(resetPasswordRequest.getEmail(),resetPasswordRequest.getOtp(),resetPasswordRequest.getNewPassword());
        return new ResponseEntity<>("password rest successfully", HttpStatus.ACCEPTED);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@CurrentSecurityContext(expression = "authentication?.name")String email){
        userService.sendOtp(email);
        return new ResponseEntity<>("otp sent to register no",HttpStatus.OK);
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String ,Object> request,
                                         @CurrentSecurityContext(expression = "authentication?.name") String email){
        if(request.get("otp").toString()==null){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"details missing");
        }
        userService.verifyOtp(email,request.get("otp").toString());
        return new ResponseEntity<>("your email verified",HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response){
        ResponseCookie cookie=ResponseCookie.from("jwtToken","")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("strict")
                .build();
        log.info("logout successfully..");
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body("logout successfully..");
    }
}
