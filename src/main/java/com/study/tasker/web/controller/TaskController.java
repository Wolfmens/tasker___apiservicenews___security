package com.study.tasker.web.controller;

import com.study.tasker.mapper.TaskMapper;
import com.study.tasker.model.TaskModel;
import com.study.tasker.model.TaskModelAfterUpdate;
import com.study.tasker.security.WebAppUserDetails;
import com.study.tasker.service.TaskService;
import com.study.tasker.web.model.TaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskMapper taskMapper;

    private final TaskService taskService;


    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    public Flux<TaskModel> findAll() {
        return taskService
                .findAll()
                .map(taskMapper::taskToTaskModel);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    public Mono<ResponseEntity<TaskModel>> findById(@PathVariable String id) {
        return taskService
                .findById(id)
                .map(taskMapper::taskToTaskModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public Mono<ResponseEntity<TaskModelAfterUpdate>> create(@RequestBody TaskRequest request,
                                                             @AuthenticationPrincipal WebAppUserDetails userDetails) {
        return taskService
                .create(taskMapper.taskRequestToTask(request, userDetails))
                .map(taskMapper::taskToTaskModelAfterUpdate)
                .map(taskModel -> ResponseEntity.status(HttpStatus.CREATED).body(taskModel))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public Mono<ResponseEntity<TaskModelAfterUpdate>> update(@PathVariable String id,
                                                             @RequestBody TaskRequest request,
                                                             @AuthenticationPrincipal WebAppUserDetails userDetails) {
        return taskService
                .updateById(id, taskMapper.taskRequestToTask(id, request, userDetails))
                .map(taskMapper::taskToTaskModelAfterUpdate)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/add-obs/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'USER')")
    public Mono<ResponseEntity<TaskModelAfterUpdate>> addObserver(@PathVariable String id, @RequestParam String observerId) {
        return taskService
                .addObservers(id, observerId)
                .map(taskMapper::taskToTaskModelAfterUpdate)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id) {
        return taskService
                .deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @DeleteMapping
    public Mono<ResponseEntity<Void>> deleteAll() {
        return taskService
                .deleteAll()
                .then(Mono.just(ResponseEntity.noContent().build()));
    }


}
