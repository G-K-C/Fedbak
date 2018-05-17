package com.questioner.util;

import java.lang.Integer;


public class AuthCode {

    public static String getAuthCode() {
        String authCode="";
        for(int i=0;i<6;i++){
            int s=(int)(Math.random()*36);
            if(s<10) {
                authCode=authCode+Integer.toString(s);
            }
            else{
                authCode=authCode+(char)(s%26+'A');
            }
        }
        return authCode;
    }
}
