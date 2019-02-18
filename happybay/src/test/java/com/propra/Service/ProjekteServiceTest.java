package propra2.person.Service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import propra2.person.Model.Projekt;
import propra2.person.Repository.ProjektRepository;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;



@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class ProjekteServiceTest {

    @InjectMocks
    ProjekteService projekteService;

    @Mock
    ProjektRepository projektRepository;

    private static Long[] oneId = {5L};
    private static Long[] moreIds = {1L, 2L, 3L};
    private int port = 8080;
    private String fakeServer = "http://localhost:" + port;
    private String body = "{" + "id: 1," + "projektId:2" + ","+  "  event: edit," + "}";
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(port);
    @Before
    public void setupForServer() {
        stubFor(get(urlPathMatching("/projekt/api/events"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)
                )
        );

    }
    @Test
    public void getProjekteTest1Projekt() {
        List<Projekt> actual = projekteService.getProjekte(oneId);

        List<Projekt> expected = new ArrayList<>();
        expected.add(projektRepository.findAllById(oneId[0]));
        assertEquals(actual, expected);
    }

    @Test
    public void getProjekteTest3Projekte() {
        List<Projekt> actual = projekteService.getProjekte(moreIds);

        List<Projekt> expected = new ArrayList<>();
        for (Long id : moreIds) {
            Projekt projekt = projektRepository.findAllById(id);
            expected.add(projekt);
        }
        assertEquals(actual, expected);
    }
    @Test
    public void contextLoads() {
        RestTemplate restTemplate = new RestTemplate();
        String resourceURL = fakeServer;
        ResponseEntity<String> response = restTemplate
                .getForEntity(resourceURL + "/projekt/api/events", String.class);
        assertNotNull(response);
        assertTrue("Status code not equals to 200",response.getStatusCode().equals(HttpStatus.OK));
        assertTrue("Contains fail", response.getBody().contains("projektId:2"));

    }



}
