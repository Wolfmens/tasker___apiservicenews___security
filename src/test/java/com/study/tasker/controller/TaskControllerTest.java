package com.study.tasker.controller;

import com.study.tasker.TestBase;
import com.study.tasker.entity.Task;
import com.study.tasker.entity.TaskStatus;
import com.study.tasker.model.TaskModel;
import com.study.tasker.model.TaskModelAfterUpdate;
import com.study.tasker.model.UserModel;
import com.study.tasker.web.model.TaskRequest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskControllerTest extends TestBase {

    @Test
    public void whenFindAllTasks_ThenReturnTasksModelListWithAllEntities() {
        var expectedListTaskModels = List.of(
                new TaskModel(taskIds.get(1),
                        "Task 1",
                        "Description 1",
                        Instant.parse("2024-04-14T16:32:57.588Z"),
                        Instant.parse("2024-04-14T16:32:57.588Z"),
                        "DONE",
                        new UserModel(userIds.get(1),"Name 1", "Mail 1"),
                        new UserModel(userIds.get(2),"Name 2", "Mail 2"),
                        Set.of(new UserModel(userIds.get(3),"Name 3", "Mail 3"))),
                new TaskModel(taskIds.get(2),
                        "Task 2",
                        "Description 2",
                        Instant.parse("2024-04-14T16:32:57.588Z"),
                        Instant.parse("2024-04-14T16:32:57.588Z"),
                        "TODO",
                        new UserModel(userIds.get(2),"Name 2", "Mail 2"),
                        new UserModel(userIds.get(3),"Name 3", "Mail 3"),
                        Set.of(new UserModel(userIds.get(1),"Name 1", "Mail 1"))),
                new TaskModel(taskIds.get(3),
                        "Task 3",
                        "Description 3",
                        Instant.parse("2024-04-14T16:32:57.588Z"),
                        Instant.parse("2024-04-14T16:32:57.588Z"),
                        "TODO",
                        new UserModel(userIds.get(1),"Name 1", "Mail 1"),
                        new UserModel(userIds.get(2),"Name 2", "Mail 2"),
                        Set.of(new UserModel(userIds.get(3),"Name 3", "Mail 3")))
        );

        testClient
                .get()
                .uri("/api/v1/task")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TaskModel.class)
                .hasSize(3)
                .contains(expectedListTaskModels.toArray(TaskModel[]::new));
    }

    @Test
    public void whenFindTaskById_ThenReturnTaskModelWithAllEntities() {
        var expectedTaskModel = new TaskModel(taskIds.get(1),
                "Task 1",
                "Description 1",
                Instant.parse("2024-04-14T16:32:57.588Z"),
                Instant.parse("2024-04-14T16:32:57.588Z"),
                "DONE",
                new UserModel(userIds.get(1),"Name 1", "Mail 1"),
                new UserModel(userIds.get(2),"Name 2", "Mail 2"),
                Set.of(new UserModel(userIds.get(3),"Name 3", "Mail 3")));

        testClient
                .get()
                .uri("/api/v1/task/{id}", expectedTaskModel.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskModel.class)
                .isEqualTo(expectedTaskModel);
    }

    @Test
    public void whenCreateTask_ThenReturnNewTaskModel() {
        StepVerifier
                .create(taskRepository.count())
                .expectNext(3L)
                .verifyComplete();

        TaskRequest request = new TaskRequest(
                "Task 4",
                "Description 4",
                "TODO",
                userIds.get(1),
                userIds.get(2),
                Set.of(userIds.get(3)));

        testClient
                .post()
                .uri("/api/v1/task")
                .body(Mono.just(request), TaskRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TaskModelAfterUpdate.class)
                .value(response -> {
                    assertNotNull(response.getId());
                });

        StepVerifier
                .create(taskRepository.count())
                .expectNext(4L)
                .verifyComplete();
    }

    @Test
    public void whenUpdateTask_ThenReturnUpdatedTaskModel() {
        TaskRequest request = new TaskRequest();
        request.setName("Task 444");

        testClient
                .put()
                .uri("/api/v1/task/{id}", taskIds.get(3))
                .body(Mono.just(request), TaskRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskModelAfterUpdate.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(response.getName(), request.getName());
                });

        Task probeNotExist= new Task(taskIds.get(3),
                "Task 3",
                "Description 3",
                Instant.parse("2024-04-14T16:32:57.588Z"),
                Instant.parse("2024-04-14T16:32:57.588Z"),
                TaskStatus.TODO,
                userIds.get(1),
                userIds.get(2),
                Set.of(userIds.get(3)),
                null,
                null,
                Collections.emptySet());

        Task probeExist = new Task(taskIds.get(3),
                "Task 444",
                "Description 3",
                Instant.parse("2024-04-14T16:32:57.588Z"),
                Instant.parse("2024-04-14T16:32:57.588Z"),
                TaskStatus.TODO,
                userIds.get(1),
                userIds.get(2),
                Set.of(userIds.get(3)),
                null,
                null,
                Collections.emptySet());

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnorePaths("id", "updatedAt");

        Example<Task> exampleNotExist = Example.of(probeNotExist, matcher);
        Example<Task> exampleExist = Example.of(probeExist, matcher);

        StepVerifier
                .create(taskRepository.findOne(exampleNotExist))
                .expectNextCount(0L)
                .verifyComplete();

        StepVerifier
                .create(taskRepository.findOne(exampleExist))
                .expectNextCount(1L)
                .verifyComplete();
    }

    @Test
    public void whenAddObserverToTask_ThenReturnUpdatedByFieldTaskModel() {
        String observerId = "1712780799700";

        testClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/task/add-obs/{id}")
                        .queryParam("observerId", observerId)
                        .build(taskIds.get(2)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskModelAfterUpdate.class)
                .value(response -> {
                    assertNotNull(response);
                    assertEquals(response.getObserverIds().size(), 2);
                });
    }

    @Test
    public void whenDeleteTaskById_ThenDeleteTaskFromBD() {
        testClient
                .delete()
                .uri("/api/v1/task/{id}", taskIds.get(1))
                .exchange()
                .expectStatus()
                .isNoContent();

        StepVerifier
                .create(taskRepository.count())
                .expectNext(2L)
                .verifyComplete();
    }


}
