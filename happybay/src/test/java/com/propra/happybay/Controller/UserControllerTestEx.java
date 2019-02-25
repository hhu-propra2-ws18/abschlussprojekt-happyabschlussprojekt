package com.propra.happybay.Controller;//package com.propra.happybay.Controller;
//
//import com.propra.happybay.Model.Geraet;
//import com.propra.happybay.Model.Person;
//import com.propra.happybay.Repository.*;
//import com.propra.happybay.Service.ProPayService;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.TestContext;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.servlet.view.InternalResourceViewResolver;
//
//import java.sql.Date;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.hamcrest.Matchers.*;
//import static org.hamcrest.core.AllOf.allOf;
//import static org.hamcrest.core.IsCollectionContaining.hasItem;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class SecuredControllerSpringBootIntegrationTest {
//
//    @Autowired
//    private WebApplicationContext context;
//
//    private MockMvc mvc;
//
//    @Before
//    public void setup() {
//        mvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity())
//                .build();
//    }
//    @WithMockUser(value = "test")
//    @Test
//    public void givenAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
//        mvc.perform(get("/user/profile").contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
////    private Person besitzer = new Person();
////    private Person mieter = new Person();
////    private Geraet verfuerbaresGeraet = new Geraet();
////    private Geraet nichtVerfuerbaresGeraet = new Geraet();
////    private List<Geraet> geraetList = new ArrayList<>();
////
////    private MockMvc mockMvc;
////    @Mock
////    PersonRepository personRepository;
////    @Mock
////    GeraetRepository geraetRepository;
////    @Autowired
////    public PasswordEncoder encoder;
////    @Autowired
////    private TransferRequestRepository transferRequestRepository;
////    @Autowired
////    private ProPayService proPayService;
////    @Autowired
////    private AccountRepository accountRepository;
////    @Autowired
////    private NotificationRepository notificationRepository;
////    @Autowired
////    private GeraetMitReservationIDRepository geraetMitReservationIDRepository;
////    @Before
////    public void setUp() {
////        //Besitzer
////        besitzer.setVorname("David");
////        besitzer.setNachname("Cao");
////        besitzer.setKontakt("ancao100@gmail.com");
////        besitzer.setAdresse("test 13 10203 Dusseldorf");
////        besitzer.setId(2L);
////        besitzer.setUsername("ancao100");
////        //besitzer.setPassword(encoder.encode("123456789"));
////        //besitzer.setPasswordConfirm((encoder.encode("123456789")));
////        besitzer.setRole("ROLE_USER");
////        when(personRepository.findById(2L)).thenReturn(java.util.Optional.ofNullable(besitzer));
////        when(personRepository.save(Mockito.isA(Person.class))).thenReturn(besitzer);
////        //Mieter
////        mieter.setVorname("David");
////        mieter.setNachname("Cao");
////        mieter.setKontakt("ancao100@gmail.com");
////        mieter.setAdresse("test 13 10203 Dusseldorf");
////        mieter.setId(3L);
////        mieter.setUsername("anhphu195");
////        //mieter.setPassword(encoder.encode("123456789"));
////        mieter.setRole("ROLE_USER");
////        when(personRepository.findById(2L)).thenReturn(java.util.Optional.ofNullable(mieter));
////        when(personRepository.save(Mockito.isA(Person.class))).thenReturn(mieter);
////
////
////
////        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
////        viewResolver.setPrefix("/WEB-INF/jsp/view/");
////        viewResolver.setSuffix(".jsp");
////        //verfuerbaresGeraet
////        verfuerbaresGeraet.setId(4L);
////        verfuerbaresGeraet.setBesitzer(besitzer.getUsername());
////        verfuerbaresGeraet.setBeschreibung("blablabla");
////        verfuerbaresGeraet.setTitel("Titel Muster");
////        verfuerbaresGeraet.setVerfuegbar(true);
////        verfuerbaresGeraet.setMieter(null);
////        verfuerbaresGeraet.setZeitraum(3);
////        verfuerbaresGeraet.setKosten(3);
////        verfuerbaresGeraet.setKaution(10);
////        verfuerbaresGeraet.setAbholort("Duesseldorf");
////        verfuerbaresGeraet.setMietezeitpunkt(new Date(2019,3,2));
////
////        when(geraetRepository.findAll()).thenReturn(Arrays.asList(verfuerbaresGeraet));
////        geraetList.add(verfuerbaresGeraet);
////        // build mockmvc
////        this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController(geraetRepository, personRepository,encoder,transferRequestRepository,proPayService,accountRepository,notificationRepository,geraetMitReservationIDRepository))
////                .setViewResolvers(viewResolver)
////                .build();
////    }
////    @After
////    public void clear(){
////        personRepository.deleteAll();
////        geraetRepository.deleteAll();
////    }
//
////    @Test
//////    public void profileTest() throws Exception{
//////        mockMvc.perform(get("/user/profile"))
//////                .andExpect(status().isOk())
//////                .andDo(print())
//////                .andExpect(view().name("user/profile"));
//////        verify(personRepository,times(1)).findByUsername(anyString());
//////    }
////    @Test
////    public void AddPersonPageTEST() throws Exception {
////        mockMvc.perform(get("/addPerson"))
////                .andExpect(status().isOk())
////                .andDo(print())
////                .andExpect(view().name("addPerson"))
////                .andExpect(model().attribute("projekte", hasItem(
////                        allOf(
////                                hasProperty("name", is("projekt4")),
////                                hasProperty("beschreibung", is("description")),
////                                hasProperty("startTime", is(Date.valueOf("2018-10-30"))),
////                                hasProperty("last", is(10))
////                        )
////                )));
////        verify(projektRepository, times(1)).findAll();
////        verifyNoMoreInteractions(projektRepository);
////    }
////
////    @Test
////    public void AddToDatabaseTEST_fullInfo() throws Exception {
////        Long[] vergangeneProjekte =new Long[]{1L};
////        vergangeneProjekt.add(firstProjekt);
////        when(projekteService.getProjekte(any())).thenReturn((vergangeneProjekt));
////
////        mockMvc.perform(post("/add")
////
////                .flashAttr("person", firstPerson)
////        )
////                .andDo(print())
////                .andExpect(status().isOk())
////                .andExpect(model().attribute("person",
////                        allOf(
////                                hasProperty("projekteId", is(vergangeneProjekte)),
////                                hasProperty("skills", is(new String[]{"Java", "Python"})),
////                                hasProperty("kontakt", is("tung@gmail.com")),
////                                hasProperty("jahreslohn", is("10000")),
////                                hasProperty("nachname", is("Stark")),
////                                hasProperty("vorname", is("Tom"))
////                        )
////                ))
////                .andExpect(model().attribute("projekte",hasItem(
////                        allOf(
////                                hasProperty("name", is("projekt4")),
////                                hasProperty("beschreibung", is("description")),
////                                hasProperty("startTime", is(Date.valueOf("2018-10-30"))),
////                                hasProperty("last", is(10)),
////                                hasProperty("personId", is(new Integer[]{1}))
////                        )
////                )))
////        ;
////        verify(personRepository, times(1)).save(any());
////        verify(projekteService, times(1)).getProjekte(any());
////        verify(personEventService, times(1)).createEvent(any());
////
////    }
////
////    @Test
////    public void AddToDatabaseTEST_no_Projekt_no_skills() throws Exception {
////        vergangeneProjekt.add(firstProjekt);
////        mockMvc.perform(post("/add")
////
////                .flashAttr("person", firstPerson)
////
////        )
////                .andDo(print())
////                .andExpect(status().isOk())
////                .andExpect(model().attribute("person",
////                        allOf(
////                                hasProperty("kontakt", is("tung@gmail.com")),
////                                hasProperty("jahreslohn", is("10000")),
////                                hasProperty("nachname", is("Stark")),
////                                hasProperty("vorname", is("Tom"))
////                        )
////                ))
////                .andExpect(model().size(2))
////
////        ;
////        verify(personRepository, times(1)).save(any());
////        verify(personEventService, times(1)).createEvent(any());
////
////    }
////    @Test
////    public void AddToDatabaseTEST_no_skills_have_Projekt() throws Exception {
////        Long[] vergangeneProjekte =new Long[]{1L};
////        vergangeneProjekt.add(firstProjekt);
////        when(projekteService.getProjekte(any())).thenReturn((vergangeneProjekt));
////        firstPerson.setSkills(new String[]{"keine"});
////        mockMvc.perform(post("/add")
////
////                .flashAttr("person",firstPerson)
////        )
////                .andDo(print())
////                .andExpect(status().isOk())
////                .andExpect(model().attribute("person",
////                        allOf(
////                                hasProperty("projekteId", is(vergangeneProjekte)),
////                                hasProperty("skills", is(new String[]{"keine"})),
////                                hasProperty("kontakt", is("tung@gmail.com")),
////                                hasProperty("jahreslohn", is("10000")),
////                                hasProperty("nachname", is("Stark")),
////                                hasProperty("vorname", is("Tom"))
////                        )
////                ))
////                .andExpect(model().attribute("projekte",hasItem(
////                        allOf(
////                                hasProperty("name", is("projekt4")),
////                                hasProperty("beschreibung", is("description")),
////                                hasProperty("startTime", is(Date.valueOf("2018-10-30"))),
////                                hasProperty("last", is(10)),
////                                hasProperty("personId", is(new Integer[]{1}))
////                        )
////                )))
////        ;
////        verify(personRepository, times(1)).save(any());
////        verify(projekteService, times(1)).getProjekte(any());
////        verify(personEventService, times(1)).createEvent(any());
////
////    }
////    @Test
////    public void AddToDatabaseTEST_no_Projekt() throws Exception {
////        vergangeneProjekt.add(firstProjekt);
////        firstPerson.setProjekteId(null);
////        firstPerson.setSkills(new String[]{"keine"});
////        mockMvc.perform(post("/add")
////
////                .flashAttr("person",firstPerson)
////        )
////                .andDo(print())
////                .andExpect(status().isOk())
////                .andExpect(model().attribute("person",
////                        allOf(
////                                hasProperty("projekteId", nullValue()),
////                                hasProperty("skills", is(new String[]{"keine"})),
////                                hasProperty("kontakt", is("tung@gmail.com")),
////                                hasProperty("jahreslohn", is("10000")),
////                                hasProperty("nachname", is("Stark")),
////                                hasProperty("vorname", is("Tom"))
////                        )
////                ))
////
////        ;
////        verify(personRepository, times(1)).save(any());
////        verify(projekteService, times(0)).getProjekte(any());
////        verify(personEventService, times(1)).createEvent(any());
////
////    }
////    @Test
////    public void editTEST_PersonnichtVorhanden() throws Exception {
////        mockMvc.perform(get("/edit/{id}", 2L))
////                .andDo(print())
////                .andExpect(status().isOk())
////        ;
////
////        verify(personRepository, times(1)).findById(anyLong());
////        verify(projektRepository, times(1)).findAll();
////        verifyZeroInteractions(personRepository);
////    }
////
////    @Test
////    public void editTEST_PersonVorhanden() throws Exception {
////
////        when(personRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(firstPerson));
////
////        mockMvc.perform(get("/edit/{id}", 1L))
////                .andDo(print())
////                .andExpect(status().isOk())
////                .andExpect(view().name("edit"))
////                .andExpect(request().attribute("projekte", hasItem(
////                        allOf(
////                                hasProperty("name", is("projekt4")),
////                                hasProperty("beschreibung", is("description")),
////                                hasProperty("startTime", is(Date.valueOf("2018-10-30"))),
////                                hasProperty("last", is(10))
////                        )
////                )))
////        ;
////        verify(personRepository, times(1)).findById(1L);
////        verifyZeroInteractions(personRepository);
////    }
////    @Test
////    public void saveChanges_TEST() throws Exception {
////        mockMvc.perform(post("/saveChanges/{id}", 2L)
////                .flashAttr("person", firstPerson)
////        )
////                .andExpect(status().isOk())
////                .andExpect(view().name("confirmationEdit"))
////                .andDo(print())
////
////        ;
////        verify(personRepository, times(1)).save(any());
////        verify(personEventService, times(1)).editEvent(any());
////        verify(projekteService, times(1)).getProjekte(any());
////
////    }
////    @Test
////    public void deletePersonTest() throws Exception{
////        mockMvc.perform(post("/delete/{id}",1L))
////                .andExpect(status().isOk())
////                .andExpect(view().name("confirmationDelete"));
////        verify(personRepository, times(1)).deleteById(anyLong());
////        verify(personEventService, times(1)).deleteEvent(any());
////
////    }
////
////
////
//}