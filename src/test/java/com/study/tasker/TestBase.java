package com.study.tasker;

import com.study.tasker.entity.Task;
import com.study.tasker.entity.TaskStatus;
import com.study.tasker.entity.User;
import com.study.tasker.repository.TaskRepository;
import com.study.tasker.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.*;

@SpringBootTest
@AutoConfigureWebTestClient
@Testcontainers
@ActiveProfiles("test")
public class TestBase {

    @Value("#{${app.test.user.userIds}}")
    protected Map<Integer, String> userIds = new HashMap<>();

    @Value("#{${app.test.user.taskIds}}")
    protected Map<Integer, String> taskIds = new HashMap<>();

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected TaskRepository taskRepository;

    @Autowired
    protected WebTestClient testClient;

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:6.0.8"))
            .withReuse(true);

    @DynamicPropertySource
    static void registry(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    public void setDates() {
        List<User> userDates = List.of(
                new User(userIds.get(1),"Name 1", "Mail 1"),
                new User(userIds.get(2),"Name 2", "Mail 2"),
                new User(userIds.get(3),"Name 3", "Mail 3")
        );

        List<Task> taskDates = List.of(
                new Task(taskIds.get(1),
                        "Task 1",
                        "Description 1",
                        Instant.parse("2024-04-14T16:32:57.588Z"),
                        Instant.parse("2024-04-14T16:32:57.588Z"),
                        TaskStatus.DONE,
                        userIds.get(1),
                        userIds.get(2),
                        Set.of(userIds.get(3)),
                        null,
                        null,
                        Collections.emptySet()),
                new Task(taskIds.get(2),
                        "Task 2",
                        "Description 2",
                        Instant.parse("2024-04-14T16:32:57.588Z"),
                        Instant.parse("2024-04-14T16:32:57.588Z"),
                        TaskStatus.TODO,
                        userIds.get(2),
                        userIds.get(3),
                        Set.of(userIds.get(1)),
                        null,
                        null,
                        Collections.emptySet()),
                new Task(taskIds.get(3),
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
                        Collections.emptySet())
        );

        userRepository.saveAll(userDates).collectList().block();
        taskRepository.saveAll(taskDates).collectList().block();
    }

    @AfterEach
    public void afterTest() {
        userRepository.deleteAll().block();
        taskRepository.deleteAll().block();
    }



}
