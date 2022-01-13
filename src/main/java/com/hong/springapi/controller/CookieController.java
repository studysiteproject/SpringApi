package com.hong.springapi.controller;

import com.hong.springapi.repository.ApplicationlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class CookieController {

    private final ApplicationlistRepository applicationlistRepository;

    @GetMapping("/cookie")
    public ResponseEntity<Object> getCookie(HttpServletRequest request){
        Cookie[] getCookie = request.getCookies();
        Map<String,String> map = new HashMap<>();
        if(getCookie != null){
            for(int i=0; i<getCookie.length; i++){
                Cookie c = getCookie[i];
                String name = c.getName(); // 쿠키 이름 가져오기
                System.out.println("name : " + name);
                String value = c.getValue(); // 쿠키 값 가져오기
                System.out.println("value : " + value);
                map.put(name,value);
            }
        }
        return new ResponseEntity<>(map , HttpStatus.OK);
    }

    @PostMapping("/cookie")
    public void addCookie(HttpServletResponse response){
        Cookie myCookie = new Cookie("test1", "value1");
        myCookie.setPath("/");
        response.addCookie(myCookie);
        myCookie = new Cookie("test2", "value2");
        myCookie.setPath("/");
        response.addCookie(myCookie);
    }
}
