package com.study.tasker.service;

import com.study.tasker.entity.Task;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskService {

    Flux<Task> findAll();

    Mono<Task> findById(String id);

    Mono<Task> create(Task task);

    Mono<Task> updateById(String id, Task task);

    Mono<Task> addObservers(String id, String observerId);

    Mono<Void> deleteById(String id);

    Mono<Void> deleteAll();
}
