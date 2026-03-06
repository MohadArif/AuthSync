package com.example.AuthSync.service;

import org.hibernate.annotations.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

      @Autowired
      private JavaMailSender javaMailSender;

      @Value("${spring.mail.properties.mail.smtp.from}")
      private String fromMail;

      @Async   // make the Async
      public void welcomeMail(String  toMail,String name){
          SimpleMailMessage message=new SimpleMailMessage();
          message.setFrom(fromMail);
          message.setTo(toMail);
          message.setSubject("welcome message");
          message.setText("Hi "+name+", \n\n🙏 Welcome to AuthSync!\nWe're excited to have you on board ♥ " +
                  "\n\n Your account is now successfully created. " +
                  "You can log in and start exploring your dashboard anytime." +
                  "\n\nBest Regards\nThe AuthSync Team");
          javaMailSender.send(message);
      }

      @Async
    public void sendOtpToMail(String  toMail,String otp,String name){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(fromMail);
        message.setTo(toMail);
        message.setSubject("Reset Password verification Otp");
        message.setText("Hi " + name + ",\n\n" +
                "We received a request to reset your password.\n\n" +
                "Your OTP is: " + otp + "\n" +
                "This OTP is valid for the next 15 minutes.\n\n" +
                "If you didn't request a password reset, please ignore this email.\n\n" +
                "Best Regards,\nThe AuthSync Team");

        javaMailSender.send(message);
    }

    @Async
    public void restPasswordSucessMail(String toMail,String name){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(fromMail);
        message.setTo(toMail);
        message.setSubject("Password reset successfully");
        message.setText("Hi " + name + ",\n\n" +
                "Your password has been successfully reset.\n\n" +
                "If you did not perform this action, please contact our support team immediately.\n\n" +
                "Best Regards,\nThe AuthSync Team");
        javaMailSender.send(message);
    }


    @Async
    public void sendOtpToEmailVerification(String  toMail,String otp,String name){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(fromMail);
        message.setTo(toMail);
        message.setSubject("Account verification Otp");
        message.setText("Hi " + name + ",\n\n" +
                "Welcome to AuthSync!\n\n" +
                "To verify your account, please use the following OTP:\n" +
                otp + "\n" +
                "This OTP is valid for the next 15 minutes.\n\n" +
                "If you did not create this account, please ignore this email.\n\n" +
                "Best Regards,\nThe AuthSync Team");
                javaMailSender.send(message);
    }
}
