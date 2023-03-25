package com.example.sns.service;

import com.example.sns.exception.ErrorCode;
import com.example.sns.exception.SnsApplicationException;
import com.example.sns.model.Post;
import com.example.sns.model.entity.PostEntity;
import com.example.sns.model.entity.UserEntity;
import com.example.sns.repository.PostEntityRepository;
import com.example.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;

    @Transactional
    public void create(String title, String body, String userName){
        // user find
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        // post save
        // title, body userEntity로 PostEntity 만들어서 DB에 저장
        postEntityRepository.save(PostEntity.of(title, body, userEntity));

    }

    @Transactional
    public Post modify(String title, String body, String userName, Integer postId){
        // user find
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
        // post exist (만약 포스트가 없으면 exception을 던짐)
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

        //post permission
        if(postEntity.getUser() != userEntity){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s",userName,postId));
        }
        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));

    }

    @Transactional
    public void delete(String userName, Integer postId){
        // user find
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
        // post exist (만약 포스트가 없으면 exception을 던짐)
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));
        //post permission
        if(postEntity.getUser() != userEntity){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION,
                    String.format("%s has no permission with %s",userName,postId));
        }

        postEntityRepository.delete(postEntity);
    }

    public Page<Post> list(Pageable pageable){
        //Page Entity로 반환
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(String userName,Pageable pageable){
        // user find
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        //Page Entity로 반환
        return postEntityRepository.findAllByUser(userEntity,pageable).map(Post::fromEntity);
    }

}



