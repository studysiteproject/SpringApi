package com.hong.springapi.util;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SQLdefend {

    static public String checkSQL(String UserInput){
        final Pattern SpecialChars = Pattern.compile("['\"\\-#()@;=*/+]");
        final String regex = "(union|select|from|where)";
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        UserInput = SpecialChars.matcher(UserInput).replaceAll("");
        UserInput = pattern.matcher(UserInput).replaceAll("");
        return UserInput;
    }

    static public List<String> checkSQL(List<String> UserInput){

        List<String> res = new ArrayList<>();

        for(String input : UserInput){
            res.add(checkSQL(input));
        }

        return res;
    }
}
