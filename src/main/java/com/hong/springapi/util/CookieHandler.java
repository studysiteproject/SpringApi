package com.hong.springapi.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class CookieHandler {

    static public Map<String,String> getCookie(HttpServletRequest request){
        Cookie[] getCookie = request.getCookies();
        Map<String,String> m = new HashMap<>();
        if(getCookie != null){
            for (Cookie c : getCookie) {
                String name = c.getName(); // 쿠키 이름 가져오기
                String value = c.getValue(); // 쿠키 값 가져오기
                m.put(name, value);
            }
        }
        return m;
    }
}
