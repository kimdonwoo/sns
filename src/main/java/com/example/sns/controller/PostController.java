package com.example.sns.controller;

import com.example.sns.controller.request.PostCreateRequest;
import com.example.sns.controller.request.PostModifyRequest;
import com.example.sns.controller.response.PostResponse;
import com.example.sns.controller.response.Response;
import com.example.sns.model.Post;
import com.example.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request , Authentication authentication){
        //authentication으로 로그인한 유저만 사용 가능
        //userName 받아오는 방법
        postService.create(request.getTitle(),request.getBody(),authentication.getName());

        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modify(@PathVariable Integer postId,@RequestBody PostModifyRequest request , Authentication authentication){
        //userName 받아오는 방법
        Post post = postService.modify(request.getTitle(),request.getBody(),authentication.getName(),postId);

        return Response.success(PostResponse.fromPost(post));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(@PathVariable Integer postId, Authentication authentication){
        postService.delete(authentication.getName(),postId);
        return Response.success();
    }

    //리스트 출력시 반드시 페이징처리 필요
    @GetMapping
    public Response<Page<PostResponse>> list(Pageable pageable, Authentication authentication){
        return Response.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    @GetMapping("/my")
    public Response<Page<PostResponse>> my(Pageable pageable, Authentication authentication){
        return Response.success(postService.my(authentication.getName(),pageable).map(PostResponse::fromPost));
    }

}
