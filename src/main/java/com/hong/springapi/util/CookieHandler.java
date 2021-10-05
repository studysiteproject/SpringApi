package com.hong.springapi.util;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
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

    // 수정필요
    static public Boolean checkValidation() {
        try {
            String uri = "https://api.catchstudys.com/auth/verify_user";
            URL url = new URL(uri);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line = br.readLine();
            System.out.println(line);
            // json형식에서 state 값을 추출해 success이면 true 리턴
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
