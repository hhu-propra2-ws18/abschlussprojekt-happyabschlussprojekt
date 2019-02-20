package com.propra.happybay.Service.AdminServices;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.*;
import com.propra.happybay.ReturnStatus;
import com.propra.happybay.Service.ProPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    PersonRepository personRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    private GeraetMitReservationIDRepository geraetMitReservationIDRepository;
    @Autowired
    GeraetRepository geraetRepository;
    @Autowired
    private TransferRequestRepository transferRequestRepository;
    @Autowired
    public PasswordEncoder encoder;

    public static int anzahlKonflikte = 0;
    public static int anzahlPersonen = 0;
    public static int anzahlNotifications = 0;

    public List<PersonMitAccount> createPersonAccont() {
        List<PersonMitAccount> personenMitAccounts = new ArrayList<>();
        List<Person> personList = personRepository.findAll();
        for (Person person : personList) {
            if (!person.getUsername().equals("admin")) {
                Optional<Account> account = accountRepository.findByAccount(person.getUsername());
                personenMitAccounts.add(new PersonMitAccount(person, account.get()));
            }
        }
        return personenMitAccounts;
    }

    public void addModelCreate(Model model) {
        setAnzahlPersonen();
        model.addAttribute("anzahlPersonen", anzahlPersonen);
        model.addAttribute("anzahlKonflikte", anzahlKonflikte);
        model.addAttribute("anzahlNotifications", anzahlNotifications);
        model.addAttribute("personenMitAccounts", createPersonAccont());
    }

    public String isInitPassword() {
        Person admin = personRepository.findByUsername("admin").get();
        if (encoder.matches("admin", admin.getPassword())) {
            return "admin/changePassword";
        }
        return "admin/allUsers";
    }

    public Account getAccountByUsername(String username) {
        return accountRepository.findByAccount(username).get();
    }

    public List<Geraet> getGeraeteMitKonflikten() {
        return geraetRepository.findAllByReturnStatus(ReturnStatus.KAPUTT);
    }

    public GeraetMitReservationID getGeraeteMitReservationID(Long GeraetId) {
        return geraetMitReservationIDRepository.findByGeraetID(GeraetId);
    }

    public void saveTransfer(int amount, String account) {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(amount);
        transferRequest.setUsername(account);
        transferRequestRepository.save(transferRequest);
    }

    public void setAdminNewPassword(String password) {
        Person admin = personRepository.findByUsername("admin").get();
        admin.setPassword(encoder.encode(password));
        personRepository.save(admin);
    }

    public List<TransferRequest> getAllTransferRequest() {
        return transferRequestRepository.findAll();
    }

    public void setAnzahlKonflikte() {
        anzahlKonflikte = getGeraeteMitKonflikten().size();
    }

    private void setAnzahlPersonen() {
        anzahlPersonen = createPersonAccont().size();
    }

    public void setAnzahlNotifications() {
        anzahlNotifications = getAllTransferRequest().size();
    }


}
