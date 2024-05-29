package com.study.tasker.service.impl;

import com.study.tasker.entity.Task;
import com.study.tasker.entity.User;
import com.study.tasker.repository.TaskRepository;
import com.study.tasker.service.TaskService;
import com.study.tasker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

    private final UserService userService;

    @Override
    public Flux<Task> findAll() {
        return repository.findAll().flatMap(this::addFieldsToTask);
    }

    @Override
    public Mono<Task> findById(String id) {
        return repository.findById(id).flatMap(this::addFieldsToTask);
    }

    @Override
    public Mono<Task> create(Task task) {
        task.setId(String.valueOf(System.currentTimeMillis()));
        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());

        return repository.save(task);
    }


    @Override
    public Mono<Task> updateById(String id, Task task) {
        return repository.findById(id)
                .flatMap(taskFromRepository -> {
                    if (StringUtils.hasText(task.getName())) {
                        taskFromRepository.setName(task.getName());
                    }

                    if (StringUtils.hasText(task.getDescription())) {
                        taskFromRepository.setDescription(task.getDescription());
                    }

                    if (task.getStatus() != null && StringUtils.hasText(task.getStatus().name())) {
                        taskFromRepository.setStatus(task.getStatus());
                    }

                    if (StringUtils.hasText(task.getAuthorId())) {
                        taskFromRepository.setAuthorId(task.getAuthorId());
                    }

                    if (StringUtils.hasText(task.getAssigneeId())) {
                        taskFromRepository.setAssigneeId(task.getAssigneeId());
                    }

                    if (task.getObserverIds() != null && !task.getObserverIds().isEmpty()) {
                        taskFromRepository.setObserverIds(task.getObserverIds());
                    }

                    taskFromRepository.setUpdatedAt(Instant.now());

                    return repository.save(taskFromRepository);
                });
    }

    @Override
    public Mono<Task> addObservers(String id, String observerId) {
        return repository.findById(id)
                .flatMap(taskFromRepository -> {
                    taskFromRepository.getObserverIds().add(observerId);

                    return repository.save(taskFromRepository);
                });
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

    private Mono<Task> addFieldsToTask(Task task) {
        Mono<User> monoAuthor = userService.findByID(task.getAuthorId());
        Mono<User> monAssignee = userService.findByID(task.getAssigneeId());
        Mono<Set<User>> monosObservers = Flux
                .fromIterable(task.getObserverIds())
                .flatMap(userService::findByID)
                .collectList()
                .map(HashSet::new);

        return Mono.zip(monoAuthor, monAssignee, monosObservers)
                .map(data -> {
                    task.setAuthor(data.getT1());
                    task.setAssignee(data.getT2());
                    task.setObservers(data.getT3());

                    return task;
                });
    }

    @Override
    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }
}
