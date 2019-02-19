package com.propra.happybay.Service;

import com.propra.happybay.Model.Account;
import com.propra.happybay.Model.Reservation;
import com.propra.happybay.Repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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


    public void erhoeheAmount(String username, double amount) throws IOException {
        URL url = new URL("http://localhost:8888/account/" + username);
        erzeugeAmount(amount, url);

    }
    public void ueberweisen(String username, String besizer, double amount) throws IOException {
        URL url = new URL("http://localhost:8888/account/"  + username + "/transfer/" + besizer);
        erzeugeAmount(amount, url);

    }
    public void erzeugeReservation(String username, String besitzer, double amount) throws IOException {
        URL url = new URL("http://localhost:8888/reservation/reserve/"  + username + "/" + besitzer);
        erzeugeAmount(amount, url);

    }

    public void releaseReservation(String username, double amount) throws IOException {
        URL url = new URL("http://localhost:8888/reservation/release/"  + username);
        erzeugeAmount(amount, url);

    }
    public void punishReservation(String username, double amount) throws IOException {
        URL url = new URL("http://localhost:8888/reservation/punish/"  + username);
        erzeugeAmount(amount, url);

    }

    private void erzeugeAmount(double amount, URL url) throws IOException {
        String query = "";
        query = query + URLEncoder.encode("amount", "UTF-8");
        query = query + "=";
        query = query + URLEncoder.encode("" + amount, "UTF-8");

        byte[] queryBytes = query.toString().getBytes("UTF-8");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", String.valueOf(queryBytes.length));
        connection.setDoOutput(true);
        connection.getOutputStream().write(queryBytes);

        Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
    }

}
