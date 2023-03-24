package com.example.sns.controller;

import com.example.sns.controller.request.UserJoinRequest;
import com.example.sns.controller.request.UserLoginRequest;
import com.example.sns.controller.response.Response;
import com.example.sns.controller.response.UserJoinResponse;
import com.example.sns.controller.response.UserLoginResponse;
import com.example.sns.model.User;
import com.example.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request){
        // POST로 request(형식 UserJoinRequest로 설정) 받아서 userService.join을 해준다
        User user = userService.join(request.getUserName(), request.getPassword());

        // Controller에서 Service로부터 user로 받아온 다음 Response 형식으로 넘겨준다
        return Response.success(UserJoinResponse.fromUser(user));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request){

        String token = userService.login(request.getUserName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));

    }

}
