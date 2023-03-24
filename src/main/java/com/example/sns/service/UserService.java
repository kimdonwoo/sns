package com.example.sns.service;


import com.example.sns.exception.ErrorCode;
import com.example.sns.exception.SnsApplicationException;
import com.example.sns.model.User;
import com.example.sns.model.entity.UserEntity;
import com.example.sns.repository.UserEntityRepository;
import com.example.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    public User loadUserByUserName(String userName){
        return userEntityRepository.findByUserName(userName).map(User::fromEntity).orElseThrow(()->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND,String.format("%s not founded",userName)));
    }


    // join을 하다가 exception이 발생할 경우 rollback 발생
    @Transactional
    public User join(String userName, String password){

        // 회원가입하려는 userName으로 회원가입된 user가 있는지
        // 만약 유저가 있다면 에러를 던져줌
        userEntityRepository.findByUserName(userName).ifPresent(it ->{
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME,String.format("%s is duplicated",userName));
        });

        //문제 없으면 회원가입 진행 => user 등록
        // save vs persist (??)
        // userEntity를 만들어서 save를 함
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));

        // userEntity를 Controller로 보내주는게 아니고 User로 바꿔서 보내줌

        return User.fromEntity(userEntity);
    }


    public String login(String userName, String password){
        // 회원가입 여부 체크
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND,String.format("%s not founded",userName)));

        // 비밀번호 체크
        if(!encoder.matches(password,userEntity.getPassword())){
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD,"");
        }

        // 토큰 생성
        String token = JwtTokenUtils.generateToken(userName, secretKey, expiredTimeMs);

        return token;
    }

}
