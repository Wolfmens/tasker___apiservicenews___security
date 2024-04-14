package com.study.tasker.service.impl;

import com.study.tasker.entity.User;
import com.study.tasker.repository.UserRepository;
import com.study.tasker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public Flux<User> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<User> findByID(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<User> create(User user) {
        user.setId(String.valueOf(System.currentTimeMillis()));

        return repository.save(user);
    }

    @Override
    public Mono<User> updateById(String id, User user) {
        return findByID(id)
                .flatMap(userFromRepository -> {
                    if (StringUtils.hasText(user.getName())) {
                        userFromRepository.setName(user.getName());
                    }
                    if (StringUtils.hasText(user.getEmail())) {
                        userFromRepository.setEmail(user.getEmail());
                    }
                    return repository.save(userFromRepository);
                });
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}
