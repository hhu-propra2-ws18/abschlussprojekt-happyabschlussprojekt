package com.propra.happybay.Service.AdminServices;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.*;
import com.propra.happybay.ReturnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    PersonRepository personRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    GeraetRepository geraetRepository;
    @Autowired
    private TransferRequestRepository transferRequestRepository;
    @Autowired
    public PasswordEncoder encoder;

    public static int numberOfConflicts = 0;
    public static int numberOfPersons = 0;
    public static int numberOfNotifications = 0;


    @Override
    public List<PersonMitAccount> returnAllPersonsWithAccounts() {
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

    @Override
    public InformationForMenuBadges returnInformationForMenuBadges() {
        updateInformationForMenuBadges();
        InformationForMenuBadges informationForMenuBadges = new InformationForMenuBadges();
        informationForMenuBadges.setNumberOfConflicts(numberOfConflicts);
        informationForMenuBadges.setNumberOfPersons(numberOfPersons);
        informationForMenuBadges.setNumberOfNotifications(numberOfNotifications);
        return informationForMenuBadges;
    }

    public void updateInformationForMenuBadges() {
        List<PersonMitAccount> personenMitAccounts = returnAllPersonsWithAccounts();
        numberOfPersons = personenMitAccounts.size();
        List<Geraet> geraeteMitKonflikten = geraetRepository.findAllByReturnStatus(ReturnStatus.KAPUTT);
        numberOfConflicts = geraeteMitKonflikten.size();
        List<TransferRequest> transferRequests = transferRequestRepository.findAll();
        numberOfNotifications = transferRequests.size();
    }
    @Override
    public boolean isAdminHasDefaultPassword() {
        Person admin = personRepository.findByUsername("admin").get();
        if (encoder.matches("admin", admin.getPassword())) {
            return true;
        }
        return false;
    }

    @Override
    public void changeAdminPassword(String newPassword) {
        Person admin = personRepository.findByUsername("admin").get();
        admin.setPassword(encoder.encode(newPassword));
        personRepository.save(admin);
    }

    //public void setGeraetToNew(Long geraetId, boolean setDefault) {
    //    Geraet geraet = geraetRepository.findById(geraetId).get();
    //    if (setDefault) {
    //        geraet.setReturnStatus(ReturnStatus.DEFAULT);
    //    }
    //    geraet.setVerfuegbar(true);
    //    geraet.setMieter(null);
    //    geraetRepository.save(geraet);
    //    proPayService.saveAccount(geraet.getBesitzer());
    //    proPayService.saveAccount(geraet.getMieter());
    //}
    //
    //public String isInitPassword() {
    //    Person admin = personRepository.findByUsername("admin").get();
    //    if (encoder.matches("admin", admin.getPassword())) {
    //        return "admin/changePassword";
    //    }
    //    return "admin/allUsers";
    //}
    //
    //public Account getAccountByUsername(String username) {
    //    return accountRepository.findByAccount(username).get();
    //}
    //
    //public List<Geraet> getGeraeteMitKonflikten() {
    //    return geraetRepository.findAllByReturnStatus(ReturnStatus.KAPUTT);
    //}
    //
    //public GeraetMitReservationID getGeraeteMitReservationID(Long GeraetId) {
    //    return geraetMitReservationIDRepository.findByGeraetID(GeraetId);
    //}
    //
    //public void saveTransfer(int amount, String account) {
    //    TransferRequest transferRequest = new TransferRequest();
    //    transferRequest.setAmount(amount);
    //    transferRequest.setUsername(account);
    //    transferRequestRepository.save(transferRequest);
    //}
    //
    //public void setAdminNewPassword(String password) {
    //    Person admin = personRepository.findByUsername("admin").get();
    //    admin.setPassword(encoder.encode(password));
    //    personRepository.save(admin);
    //}
    //
    //public List<TransferRequest> getAllTransferRequest() {
    //    return transferRequestRepository.findAll();
    //}
    //
    //public void setAnzahlKonflikte() {
    //    numberOfConflicts = getGeraeteMitKonflikten().size();
    //}
    //
    //private void setAnzahlPersonen() {
    //    numberOfPersons = createPersonAccont().size();
    //}
    //
    //public void setAnzahlNotifications() {
    //    numberOfNotifications = getAllTransferRequest().size();
    //}


}
