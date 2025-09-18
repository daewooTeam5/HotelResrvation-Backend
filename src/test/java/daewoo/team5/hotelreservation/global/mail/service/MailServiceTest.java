package daewoo.team5.hotelreservation.global.mail.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MailServiceTest {
    @Autowired
    private MailService mailService;

    @Test
    public void sendMailOtp(){
        mailService.sendOtpCode("seungho020510@gmail.com","123456");
    }

}