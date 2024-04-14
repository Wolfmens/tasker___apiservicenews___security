package com.study.tasker.controller;

import com.study.tasker.TestBase;
import com.study.tasker.entity.User;
import com.study.tasker.model.UserModel;
import com.study.tasker.web.model.UserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserControllerTest extends TestBase {

    @Test
    public void whenFindAllUsers_ThenReturnListUserModelsFromBD() {
        var expectUserModelList = List.of(
                new UserModel(userIds.get(1), "Name 1", "Mail 1"),
                new UserModel(userIds.get(2), "Name 2", "Mail 2"),
                new UserModel(userIds.get(3), "Name 3", "Mail 3"));

        testClient
                .get()
                .uri("/api/v1/user")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserModel.class)
                .hasSize(3)
                .contains(expectUserModelList.toArray(UserModel[]::new));
    }

    @Test
    public void whenFindUserById_ThenReturnUserModelFromBD() {
        var expectUser = new UserModel(userIds.get(1), "Name 1", "Mail 1");

        testClient
                .get()
                .uri("/api/v1/user/{id}", expectUser.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserModel.class)
                .isEqualTo(expectUser);
    }

    @Test
    public void whenCreateUser_ThenReturnNewUserModel() {
        StepVerifier
                .create(userRepository.count())
                .expectNext(3L)
                .expectComplete()
                .verify();

        UserRequest newUser = new UserRequest("Name 4", "Mail 4");

        testClient
                .post()
                .uri("/api/v1/user")
                .body(Mono.just(newUser), User.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserModel.class)
                .value(responseModel -> {
                    assertNotNull(responseModel.getId());
                });

        StepVerifier
                .create(userRepository.count())
                .expectNext(4L)
                .expectComplete()
                .verify();

    }

    @Test
    public void whenUpdateUserFromBase_ThenReturnUpdatedUserModelFromBase() {
        var userUpdate = new UserRequest();
        userUpdate.setName("Name 11");

        testClient
                .put()
                .uri("/api/v1/user/{id}", userIds.get(1))
                .body(Mono.just(userUpdate), User.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserModel.class)
                .value(responseBody -> {
                    assertNotNull(responseBody);
                    assertEquals(responseBody.getName(), userUpdate.getName());
                });

        User probe = new User(userIds.get(1), "Name 1", "Mail 1");

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnorePaths("id");

        Example<User> example = Example.of(probe, matcher);

        StepVerifier
                .create(userRepository.findOne(example))
                .expectNextCount(0L)
                .verifyComplete();

        StepVerifier
                .create(userRepository.findOne(Example.of(
                        new User(
                                userIds.get(1),
                                "Name 11",
                                "Mail 1"),
                        matcher)))
                .expectNextCount(1L)
                .verifyComplete();
    }

    @Test
    public void whenDeleteUserById_ThenUserDeletedFromBD() {
        testClient
                .delete()
                .uri("/api/v1/user/{id}", userIds.get(3))
                .exchange()
                .expectStatus().isNoContent();

        StepVerifier
                .create(userRepository.count())
                .expectNext(2L)
                .verifyComplete();
    }

}
