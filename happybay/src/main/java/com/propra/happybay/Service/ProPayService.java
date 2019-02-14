package com.propra.happybay.Service;

import com.propra.happybay.Model.Account;
import com.propra.happybay.Repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ProPayService {

    @Autowired
    AccountRepository accountRepository;

    public void saveAccount(String username) {
        Account account = getEntity(username, Account.class);
        accountRepository.save(account);
    }

    public static <T> T getEntity(final String username, final Class<T> type) {
        final Mono<T> mono = WebClient
                .create()
                .get()
                .uri("http://localhost:8888/account/" + username)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(type);

        return mono.block();
    }
}
