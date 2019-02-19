package com.propra.happybay.Service;

import com.propra.happybay.Model.Account;
import com.propra.happybay.Repository.AccountRepository;
import com.propra.happybay.Repository.PersonRepository;
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
    @Autowired
    PersonRepository personRepository;

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
        makeQuery(amount, "amount", url);

    }
    public void ueberweisen(String username, String besizer, double amount) throws IOException {
        URL url = new URL("http://localhost:8888/account/"  + username + "/transfer/" + besizer);
        makeQuery(amount, "amount", url);

    }
    public int erzeugeReservation(String mieter, String besitzer, double amount) throws IOException {
        URL url = new URL("http://localhost:8888/reservation/reserve/"  + mieter + "/" + besitzer);
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
        System.out.println("########################");

        String response = ((BufferedReader) reader).readLine();
        String[] lines = response.split(":");
        String[] lines2 = lines[1].split(",");
        return Integer.parseInt(lines2[0]);

    }

    public void releaseReservation(String username, int reservationId) throws IOException {
        URL url = new URL("http://localhost:8888/reservation/release/"  + username);
        makeQueryPunish(reservationId, "reservationId",url);

    }
    public void punishReservation(String username, int reservationId) throws IOException {
        URL url = new URL("http://localhost:8888/reservation/punish/"  + username);
        makeQueryPunish(reservationId, "reservationId",url);

    }

    private void makeQuery(double amount, String amountString, URL url) throws IOException {
        String query = "";
        query = query + URLEncoder.encode(amountString, "UTF-8");
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
    private void makeQueryPunish(int amount, String amountString, URL url) throws IOException {
        String query = "";
        query = query + URLEncoder.encode(amountString, "UTF-8");
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
