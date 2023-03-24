package com.example.sns.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response<T> {
    private String resultcode;
    private T result;

    public static Response<Void> error(String errorcode){
        return new Response<>(errorcode, null );
    }

    public static Response<Void> success(){
        return new Response<Void>("SUCCESS", null );
    }

    public static <T> Response<T> success(T result){
        return new Response<>("SUCCESS", result );
    }

    public String toStream() {

        if(result == null){
            return "{"+
                    "\"resultCode\":" +"\"" +resultcode + "\","+
                    "\"result\":"  + null  + "}";
        }

        return "{"+
                "\"resultCode\":" +"\"" +resultcode + "\","+
                "\"result\":"+"\""  + result+ "\""  + "}";
    }
}
