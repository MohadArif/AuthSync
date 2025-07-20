package com.example.AuthSync.service;

import com.example.AuthSync.dto.ResetPasswordRequest;
import com.example.AuthSync.dto.UserEntityRequest;
import com.example.AuthSync.dto.UserEntityResponse;
import com.example.AuthSync.entity.UserEntity;
import com.example.AuthSync.exception.EmailAlreadyExistException;
import com.example.AuthSync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEnoder;
    private final MailService mailService;

    @Override
    public UserEntityResponse createUser(UserEntityRequest userEntityRequest) {
        if(userRepository.existsByEmail(userEntityRequest.getEmail())){
            throw new EmailAlreadyExistException("email id already exist");
        }
        UserEntity userEntity = convertToUserEntity(userEntityRequest);
        userEntity.setPassword(passwordEnoder.encode(userEntityRequest.getPassword()));
        UserEntity savedUser = userRepository.save(userEntity);
        return convertToUserEntityResponse(savedUser);
    }

    @Override
    public UserEntityResponse getUserProfile(String email) {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user not found" + email));
        return convertToUserEntityResponse(userEntity);
    }

    @Override
    public void sendRestOtp(String email) {
        UserEntity existingUser = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("user not found" + email));

        //creating 6 digit otp
        String otp=String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

        //create otp expiration time
        Long otpExpirationTime=System.currentTimeMillis()+(15*60*1000);

        existingUser.setResetOpt(otp);
        existingUser.setResetOtpExpiredAt(otpExpirationTime);
        userRepository.save(existingUser);

        //mail logic
       try{
           mailService.sendOtpToMail(existingUser.getEmail(),otp,existingUser.getName());
       }catch (InternalError ex){
           throw new RuntimeException("unbale to send email"+ex.getMessage());
       }
    }

    @Override
    public void resetPassword(String email, String otp,String newPassword) {
        UserEntity existingUser = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("user not found"+ email));
        if(existingUser.getResetOpt()==null || !existingUser.getResetOpt().equals(otp)){
            throw new RuntimeException("invalid otp");
        }

        if(existingUser.getResetOtpExpiredAt()<System.currentTimeMillis()){
            throw new RuntimeException("otp expired");
        }

        existingUser.setResetOpt(null);
        existingUser.setResetOtpExpiredAt(0L);
        existingUser.setPassword(passwordEnoder.encode(newPassword));
        userRepository.save(existingUser);
        try{
            mailService.restPasswordSucessMail(existingUser.getEmail(),existingUser.getName());
        }catch (InternalError ex){
            throw new RuntimeException("unable to send mail"+ex.getMessage());
        }
    }

    @Override
    public void sendOtp(String email) {

        UserEntity existingUser = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("user not fount " + email));
        if(existingUser.getIsAccountVerified()!=null && existingUser.getIsAccountVerified()){
            return;
        }

        //creating 6 digit otp
        String otp=String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

        //create otp expiration time
        Long otpExpirationTime=System.currentTimeMillis()+(24*60*60*1000);

        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpiredAt(otpExpirationTime);
        userRepository.save(existingUser);

        try{
            mailService.sendOtpToEmailVerification(existingUser.getEmail(),otp,existingUser.getName());
        }catch (InternalError ex){
            throw new RuntimeException("unable to email"+ex.getMessage());
        }
    }

    @Override
    public void verifyOtp(String email, String otp) {
        UserEntity existingUser = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("user not fount " + email));
        if(existingUser.getVerifyOtp()==null || !existingUser.getVerifyOtp().equals(otp)){
            throw new RuntimeException("invalid otp");
        }

        if (existingUser.getVerifyOtpExpiredAt()<System.currentTimeMillis()){
              throw new RuntimeException("otp expired");
        }

        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpiredAt(0L);
        existingUser.setIsAccountVerified(true);
        userRepository.save(existingUser);
    }

    @Override
    public String getloggedInUser(String email) {
        UserEntity existingUser = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("user not found :" + email));
        return existingUser.getUserId();
    }

    private UserEntityResponse convertToUserEntityResponse(UserEntity userEntity) {
        return UserEntityResponse.builder()
                .userId(userEntity.getUserId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .isAccountActive(userEntity.getIsAccountVerified())
                .build();
    }

    private UserEntity convertToUserEntity(UserEntityRequest userEntityRequest) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .name(userEntityRequest.getName())
                .email(userEntityRequest.getEmail())
                .password(userEntityRequest.getPassword())
                .isAccountVerified(false)
                .resetOpt(null)
                .verifyOtp(null)
                .verifyOtpExpiredAt(0L)
                .resetOtpExpiredAt(0L)
                .build();
    }
}
