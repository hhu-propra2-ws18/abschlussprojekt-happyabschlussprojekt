//package com.propra.happybay.Service;//package com.propra.happybay.Service;
//
//import com.propra.happybay.Model.Account;
//import com.propra.happybay.Model.Reservation;
//import com.propra.happybay.Repository.AccountRepository;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import static com.github.tomakehurst.wiremock.client.WireMock.*;
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.times;
//
//@RunWith(SpringRunner.class)
//@AutoConfigureWireMock(port = 8888)
//public class ProPayServiceTest {
//
//    @Mock
//    AccountRepository accountRepository;
//
//    @InjectMocks
//    ProPayService proPayService;
//
//    @Test
//    public void testSaveAccount() {
//        proPayService.saveAccount("Anton");
//       Mockito.verify(accountRepository,times(1)).save(any());
//    }
//
//    @Test
//    public void testUpdateAccount(){
//
//        proPayService.updateAccount("Anton");
//        Mockito.verify(accountRepository,times(1)).save(any());
//
//    }
//    @Test
//    public void testGetAccountInfoWithCorrectUrl() {
//        ProPayService propayservice = new ProPayService();
//
//        stubFor(get(urlEqualTo("/account/Anton"))
//                .willReturn(aResponse().withStatus(200)));
//
//
//        propayservice.getEntity("Anton", Account.class);
//
//        verify(getRequestedFor(urlEqualTo("/account/Anton")));
//    }
//
//
//    @Test
//    public void testGetAccountInfoWithAmountAndReservation() {
//        ProPayService propayservice = new ProPayService();
//
//        stubFor(get(urlEqualTo("/account/Anton"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody("{\"account\":\"Anton\",\"amount\":1000.0,\"reservations\":[{\"id\":1,\"" +
//                                "amount\":55.0},{\"id\":3,\"amount\":10.0},{\"id\":4,\"amount\":600.0}]}")));
//
//        Account anton = propayservice.getEntity("Anton",Account.class);
//
//        assertEquals("Anton", anton.getAccount());
//        assertEquals(1000.0, anton.getAmount(), 0.001);
//        assertEquals(3, anton.getReservations().size());
//
//        for (Reservation reservation : anton.getReservations()) {
//            if (reservation.getId() == 1) {
//                assertEquals(55.0, reservation.getAmount(), 0.001);
//            } else if (reservation.getId() == 3) {
//                assertEquals(10.0, reservation.getAmount(), 0.001);
//            } else if (reservation.getId() == 4) {
//                assertEquals(600.0, reservation.getAmount(), 0.001);
//            } else {
//                assertEquals(true, false);
//            }
//        }
//    }
//
//    @Test
//
//    public void testErhoeheAmount_CorrectUrl() {
//        ProPayService propayservice = new ProPayService();
//
//        stubFor(post(urlEqualTo("/account/Anton"))
//                .willReturn(aResponse().withStatus(200)));
//        try{
//            propayservice.erhoeheAmount("Anton", 100);
//        }catch(Exception e){
//
//
//        }
//        verify(postRequestedFor(urlEqualTo("/account/Anton")));
//    }
//
//
//    @Test
//    public void testTransferFunds_CorrectUrl() {
//        ProPayService proPayInterface = new ProPayService();
//
//        stubFor(post(urlEqualTo("/account/Anton/transfer/Tony"))
//                .willReturn(aResponse().withStatus(200)));
//
//        try{
//            proPayInterface.ueberweisen("Anton", "Tony", 100);
//        }catch(Exception e){
//
//
//        }
//        verify(postRequestedFor(urlEqualTo("/account/Anton/transfer/Tony")));
//    }
//
//
//    @Test
//    public void testCreateReservation_CorrectUrl() {
//        ProPayService proPayInterface = new ProPayService();
//
//        stubFor(post(urlEqualTo("/reservation/reserve/Anton/Tony"))
//                .willReturn(aResponse().withStatus(200)));
//        try{
//             proPayInterface.erzeugeReservation("Anton", "Tony", 100);
//        }catch(Exception e){
//
//        }
//
//        verify(postRequestedFor(urlEqualTo("/reservation/reserve/Anton/Tony")));
//    }
//
//    @Test
//    public void testCreateReservationID() {
//        ProPayService proPayInterface = new ProPayService();
//
//        stubFor(post(urlEqualTo("/reservation/reserve/Anton/Tony"))
//                .willReturn(aResponse().withStatus(200)));
//        try{
//            int id = proPayInterface.erzeugeReservation("Anton", "Tony", 100);
//            assertEquals(0,id);
//
//        }catch(Exception e){
//
//        }
//
//        verify(postRequestedFor(urlEqualTo("/reservation/reserve/Anton/Tony")));
//
//    }
//
//
//    @Test
//    public void testReleaseReservation_CorrectUrl() {
//        ProPayService proPayInterface = new ProPayService();
//
//        stubFor(post(urlEqualTo("/reservation/release/Anton"))
//                .willReturn(aResponse().withStatus(200)));
//
//
//        try{
//            proPayInterface.releaseReservation("Anton", 1l);
//        }catch(Exception e){
//
//        }
//
//        verify(postRequestedFor(urlEqualTo("/reservation/release/Anton")));
//    }
//
//
//    @Test
//    public void testPunishReservation_CorrectUrl() {
//        ProPayService proPayInterface = new ProPayService();
//
//        stubFor(post(urlEqualTo("/reservation/punish/Anton"))
//                .willReturn(aResponse().withStatus(200)));
//        try{
//            proPayInterface.punishReservation("Anton",1l);
//        }catch(Exception e){
//
//        }
//
//        verify(postRequestedFor(urlEqualTo("/reservation/punish/Anton")));
//    }
//
//
//}