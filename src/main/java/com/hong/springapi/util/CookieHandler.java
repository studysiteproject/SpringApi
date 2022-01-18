package com.hong.springapi.util;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class CookieHandler {

    static public Map<String,String> getCookies(HttpServletRequest request){
        Cookie[] getCookie = request.getCookies();
        Map<String,String> map = new HashMap<>();
        if(getCookie != null){
            for (Cookie c : getCookie) {
                String name = c.getName(); // 쿠키 이름 가져오기
                String value = c.getValue(); // 쿠키 값 가져오기
                map.put(name, value);
            }
        }
        return map;
    }

    static public Long getUser_idFromCookies(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("index".equals(cookie.getName())) {
                    return Long.parseLong(cookie.getValue());
                }
            }
        }
        return null;
    }

    static public Boolean checkValidation(HttpServletRequest request) {
        Map<String,String> map = CookieHandler.getCookies(request);
        if (map.isEmpty()) return false;
        String token = map.get("access_token");
        Long userId = Long.valueOf(map.get("index"));

        try{
            String baseUrl = "http://127.0.0.1:8000/auth/verify_user";
            //String baseUrl = "https://api.catchstudys.com/auth/verify_user";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("cookie", "access_token="+token);
            headers.add("cookie", "index="+userId);
            headers.setContentType(MediaType.valueOf("text/plain;charset=utf-8"));
            headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)" +
                    " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
            HttpEntity<String> entity = new HttpEntity<String>("", headers);
            ResponseEntity<String> res = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, String.class);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
