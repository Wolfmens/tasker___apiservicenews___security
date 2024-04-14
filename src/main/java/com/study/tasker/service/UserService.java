package com.study.tasker.service;

import com.study.tasker.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Flux<User> findAll();

    Mono<User> findByID(String id);

    Mono<User> create(User user);

    Mono<User> updateById(String id, User user);

    Mono<Void> deleteById(String id);

}
