package com.propra.happybay.Service;

import com.propra.happybay.Model.Account;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.GeraetMitReservationID;
import com.propra.happybay.Repository.AccountRepository;
import com.propra.happybay.Repository.GeraetMitReservationIDRepository;
import com.propra.happybay.Repository.GeraetRepository;
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
    @Autowired
    GeraetRepository geraetRepository;
    @Autowired
    GeraetMitReservationIDRepository geraetMitReservationIDRepository;

    public void saveAccount(String username) {
        Account account = getEntity(username, Account.class);
        accountRepository.save(account);
    }

    public void updateAccount(String username) {
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

    public void erhoeheAmount(String username, int amount) throws IOException {
        URL url = new URL("http://localhost:8888/account/" + username);
        makeQuery(amount, "amount", url);
        saveAccount(username);
    }

    public void ueberweisen(String username, String besizer, int amount) throws IOException {
        URL url = new URL("http://localhost:8888/account/"  + username + "/transfer/" + besizer);
        makeQuery(amount, "amount", url);
        saveAccount(username);
    }

    public int erzeugeReservation(String mieter, String besitzer, int amount) throws IOException {
        URL url = new URL("http://localhost:8888/reservation/reserve/"  + mieter + "/" + besitzer);
        Reader reader = makeQuery(amount, "amount", url);

        String response = ((BufferedReader) reader).readLine();
        String[] lines = response.split(":");
        String[] lines2 = lines[1].split(",");
        return Integer.parseInt(lines2[0]);
    }

    public void releaseReservation(String mieter, Long geraetId) throws IOException {
        Geraet geraet = geraetRepository.findById(geraetId).get();
        GeraetMitReservationID geraetMitReservationID = geraetMitReservationIDRepository.findByGeraetID(geraetId);
        Long reservationId = geraetMitReservationID.getReservationID();
        int reservationIdInt = reservationId.intValue();

        URL url = new URL("http://localhost:8888/reservation/release/"  + mieter);
        makeQuery(reservationIdInt, "reservationId",url);

        saveAccount(mieter);
        saveAccount(geraet.getBesitzer());
    }

    public void punishReservation(String mieter, Long geraetId) throws IOException {
        Geraet geraet = geraetRepository.findById(geraetId).get();
        System.out.println();
        System.out.println(mieter);
        System.out.println();
        GeraetMitReservationID geraetMitReservationID = geraetMitReservationIDRepository.findByGeraetID(geraetId);
        Long reservationId = geraetMitReservationID.getReservationID();
        int reservationIdInt = reservationId.intValue();

        URL url = new URL("http://localhost:8888/reservation/punish/"  + mieter);
        makeQuery(reservationIdInt, "reservationId", url);
        saveAccount(mieter);
        saveAccount(geraet.getBesitzer());
    }

    private Reader makeQuery(int amount, String amountString, URL url) throws IOException {
        String query = "";
        query = query + URLEncoder.encode(amountString, "UTF-8");
        query = query + "=";
        query = query + URLEncoder.encode("" + amount, "UTF-8");

        byte[] queryBytes = query.getBytes("UTF-8");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", String.valueOf(queryBytes.length));
        connection.setDoOutput(true);
        connection.getOutputStream().write(queryBytes);

        Reader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        return reader;
    }
}
