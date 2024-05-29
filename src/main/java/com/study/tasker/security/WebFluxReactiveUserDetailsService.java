package com.study.tasker.security;

import com.study.tasker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WebFluxReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final UserService userService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono
                .fromCallable(() -> userService.findByName(username))
                .flatMap(Mono::just).flatMap(userMono -> userMono)
                .map(WebAppUserDetails::new);
    }
}
