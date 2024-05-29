package com.study.tasker.web.controller;

import com.study.tasker.mapper.UserMapper;
import com.study.tasker.model.UserModel;
import com.study.tasker.service.UserService;
import com.study.tasker.web.model.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    public Flux<UserModel> findAll() {
        return userService
                .findAll()
                .map(userMapper::userToUserModel);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    public Mono<ResponseEntity<UserModel>> findById(@PathVariable String id) {
        return userService
                .findByID(id)
                .map(userMapper::userToUserModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(
                        ResponseEntity
                                .notFound()
                                .build());
    }

    @PostMapping
    public Mono<ResponseEntity<UserModel>> create(@RequestBody UserRequest request) {
        return userService
                .create(userMapper.userRequestToUser(request))
                .map(userMapper::userToUserModel)
                .map(userModel -> ResponseEntity.status(HttpStatus.CREATED).body(userModel))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    public Mono<ResponseEntity<UserModel>> update(@PathVariable String id, @RequestBody UserRequest request) {
        return userService.updateById(id, userMapper.userRequestToUser(id, request))
                .map(userMapper::userToUserModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id) {
        return userService.deleteById(id).then(Mono.just(ResponseEntity.noContent().build()));
    }

}
