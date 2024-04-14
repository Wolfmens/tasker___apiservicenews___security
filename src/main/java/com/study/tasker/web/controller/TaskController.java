package com.study.tasker.web.controller;

import com.study.tasker.mapper.TaskMapper;
import com.study.tasker.model.TaskModel;
import com.study.tasker.model.TaskModelAfterUpdate;
import com.study.tasker.service.TaskService;
import com.study.tasker.web.model.TaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public Flux<TaskModel> findAll() {
        return taskService
                .findAll()
                .map(taskMapper::taskToTaskModel);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TaskModel>> findById(@PathVariable String id) {
        return taskService
                .findById(id)
                .map(taskMapper::taskToTaskModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<TaskModelAfterUpdate>> create(@RequestBody TaskRequest request) {
        return taskService
                .create(taskMapper.taskRequestToTask(request))
                .map(taskMapper::taskToTaskModelAfterUpdate)
                .map(taskModel -> ResponseEntity.status(HttpStatus.CREATED).body(taskModel))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<TaskModelAfterUpdate>> update(@PathVariable String id, @RequestBody TaskRequest request) {
        return taskService
                .updateById(id, taskMapper.taskRequestToTask(id, request))
                .map(taskMapper::taskToTaskModelAfterUpdate)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/add-obs/{id}")
    public Mono<ResponseEntity<TaskModelAfterUpdate>> addObserver(@PathVariable String id, @RequestParam String observerId) {
        return taskService
                .addObservers(id, observerId)
                .map(taskMapper::taskToTaskModelAfterUpdate)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id) {
        return taskService
                .deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }


}
