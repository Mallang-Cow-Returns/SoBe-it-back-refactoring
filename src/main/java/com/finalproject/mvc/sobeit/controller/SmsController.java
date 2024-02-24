package com.finalproject.mvc.sobeit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.finalproject.mvc.sobeit.dto.MessageDTO;
import com.finalproject.mvc.sobeit.dto.SmsAuthRequestDTO;
import com.finalproject.mvc.sobeit.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;

/**
 * 회원가입 시 SMS 인증할 때 사용되는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;
    private final HttpSession session;
    private final Map<String, String> smsMap;

    @PostMapping("/sms/smsAuthRequest")
    public Boolean phoneAuth(@RequestBody String tel) throws JSONException, UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {

//        try { // 이미 가입된 전화번호가 있으면
//            if(memberService.memberTelCount(tel) > 0)
//                return false;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        MessageDTO messageDTO = new MessageDTO();
        Random rand = new Random();
        String numStr = "";
        for (int i = 0; i < 6; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            numStr += ran;
        }

        messageDTO.setContent("Sobe-it 인증번호는 [" + numStr + "] 입니다.");
        messageDTO.setTo(tel);
        smsService.sendSms(messageDTO);
//        session.setAttribute("rand", numStr);
        tel = "0" + tel;
        smsMap.put(tel,numStr);
        System.out.println(smsMap);

        return true;
    }

    @PostMapping("/sms/smsAuthOk")
    public Boolean phoneAuthOk(@RequestBody SmsAuthRequestDTO data) {
        String code = data.getCode();
        String phone = data.getPhone();

//        code = code.substring(1, 7); // 프론트에서 오는 값 양옆의 큰따옴표 제거
        System.out.println("code: " + code);
        System.out.println("phone: " + phone);

        System.out.println(smsMap);
        if (code.equals(smsMap.get(phone))){
            smsMap.remove(phone);
            return true;  // Auth successful
        }

        return false;  // Auth failed
    }
}
