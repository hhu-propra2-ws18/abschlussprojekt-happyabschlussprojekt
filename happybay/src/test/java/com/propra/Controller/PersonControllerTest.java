package propra2.person.Controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import propra2.person.Model.Person;
import propra2.person.Model.Projekt;
import propra2.person.Repository.EventRepository;
import propra2.person.Repository.PersonRepository;
import propra2.person.Repository.ProjektRepository;
import propra2.person.Service.PersonEventService;
import propra2.person.Service.PersonenMitProjektenService;
import propra2.person.Service.ProjekteService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration

public class PersonControllerTest {

    private Person firstPerson = new Person();
    private Projekt firstProjekt = new Projekt();
    private List<Projekt> vergangeneProjekt = new ArrayList<>();

    private MockMvc mockMvc;
    @Mock
    PersonRepository personRepository;
    @Mock
    ProjektRepository projektRepository;
    @Mock
    EventRepository eventRepository;
    @Mock
    ProjekteService projekteService;
    @Mock
    PersonenMitProjektenService personenMitProjektenService;
    @Mock
    PersonEventService personEventService;
    @Before
    public void setUp() {
        vergangeneProjekt.add(firstProjekt);

        firstPerson.setVorname("Tom");
        firstPerson.setNachname("Stark");
        firstPerson.setJahreslohn("10000");
        firstPerson.setKontakt("tung@gmail.com");
        firstPerson.setSkills(new String[]{"Java", "Python"});
        firstPerson.setId(2L);
        firstPerson.setProjekteId(new Long[]{1L});
        when(personRepository.findById(2L)).thenReturn(java.util.Optional.ofNullable(firstPerson));
        when(personRepository.save(Mockito.isA(Person.class))).thenReturn(firstPerson);

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");

        firstProjekt.setName("projekt4");
        firstProjekt.setBeschreibung("description");
        firstProjekt.setStartTime(Date.valueOf("2018-10-30"));
        firstProjekt.setLast(10);
        firstProjekt.setId(1L);
        firstProjekt.setPersonId(new Integer[]{1});
        when(projektRepository.findAll()).thenReturn(Arrays.asList(firstProjekt));

        List<Projekt> projekts=new ArrayList<>();
        projekts.add(firstProjekt);
        when(projekteService.getProjekte(any())).thenReturn(projekts);
        // build mockmvc
        this.mockMvc = MockMvcBuilders.standaloneSetup(new PersonController(projektRepository, personRepository,eventRepository,projekteService,personenMitProjektenService,personEventService))
                .setViewResolvers(viewResolver)
                .build();
    }
    @After
    public void clear(){
        projektRepository.deleteAll();
        personRepository.deleteAll();
    }
    @Test
    public void MainPAGE_TEST() throws Exception{
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
        verify(projekteService,times(1)).updateProjekte();
        verify(personenMitProjektenService,times(1)).returnPersonenMitProjekten();
    }
    @Test
    public void AddPersonPageTEST() throws Exception {
        mockMvc.perform(get("/addPerson"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(view().name("addPerson"))
                .andExpect(model().attribute("projekte", hasItem(
                        allOf(
                                hasProperty("name", is("projekt4")),
                                hasProperty("beschreibung", is("description")),
                                hasProperty("startTime", is(Date.valueOf("2018-10-30"))),
                                hasProperty("last", is(10))
                        )
                )));
        verify(projektRepository, times(1)).findAll();
        verifyNoMoreInteractions(projektRepository);
    }

    @Test
    public void AddToDatabaseTEST_fullInfo() throws Exception {
        Long[] vergangeneProjekte =new Long[]{1L};
        vergangeneProjekt.add(firstProjekt);
        when(projekteService.getProjekte(any())).thenReturn((vergangeneProjekt));

        mockMvc.perform(post("/add")

                .flashAttr("person", firstPerson)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("person",
                        allOf(
                                hasProperty("projekteId", is(vergangeneProjekte)),
                                hasProperty("skills", is(new String[]{"Java", "Python"})),
                                hasProperty("kontakt", is("tung@gmail.com")),
                                hasProperty("jahreslohn", is("10000")),
                                hasProperty("nachname", is("Stark")),
                                hasProperty("vorname", is("Tom"))
                        )
                ))
                .andExpect(model().attribute("projekte",hasItem(
                        allOf(
                                hasProperty("name", is("projekt4")),
                                hasProperty("beschreibung", is("description")),
                                hasProperty("startTime", is(Date.valueOf("2018-10-30"))),
                                hasProperty("last", is(10)),
                                hasProperty("personId", is(new Integer[]{1}))
                        )
                )))
        ;
        verify(personRepository, times(1)).save(any());
        verify(projekteService, times(1)).getProjekte(any());
        verify(personEventService, times(1)).createEvent(any());

    }

    @Test
    public void AddToDatabaseTEST_no_Projekt_no_skills() throws Exception {
        vergangeneProjekt.add(firstProjekt);
        mockMvc.perform(post("/add")

                .flashAttr("person", firstPerson)

        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("person",
                        allOf(
                                hasProperty("kontakt", is("tung@gmail.com")),
                                hasProperty("jahreslohn", is("10000")),
                                hasProperty("nachname", is("Stark")),
                                hasProperty("vorname", is("Tom"))
                        )
                ))
                .andExpect(model().size(2))

        ;
        verify(personRepository, times(1)).save(any());
        verify(personEventService, times(1)).createEvent(any());

    }
    @Test
    public void AddToDatabaseTEST_no_skills_have_Projekt() throws Exception {
        Long[] vergangeneProjekte =new Long[]{1L};
        vergangeneProjekt.add(firstProjekt);
        when(projekteService.getProjekte(any())).thenReturn((vergangeneProjekt));
        firstPerson.setSkills(new String[]{"keine"});
        mockMvc.perform(post("/add")

                .flashAttr("person",firstPerson)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("person",
                        allOf(
                                hasProperty("projekteId", is(vergangeneProjekte)),
                                hasProperty("skills", is(new String[]{"keine"})),
                                hasProperty("kontakt", is("tung@gmail.com")),
                                hasProperty("jahreslohn", is("10000")),
                                hasProperty("nachname", is("Stark")),
                                hasProperty("vorname", is("Tom"))
                        )
                ))
                .andExpect(model().attribute("projekte",hasItem(
                        allOf(
                                hasProperty("name", is("projekt4")),
                                hasProperty("beschreibung", is("description")),
                                hasProperty("startTime", is(Date.valueOf("2018-10-30"))),
                                hasProperty("last", is(10)),
                                hasProperty("personId", is(new Integer[]{1}))
                        )
                )))
        ;
        verify(personRepository, times(1)).save(any());
        verify(projekteService, times(1)).getProjekte(any());
        verify(personEventService, times(1)).createEvent(any());

    }
    @Test
    public void AddToDatabaseTEST_no_Projekt() throws Exception {
        vergangeneProjekt.add(firstProjekt);
        firstPerson.setProjekteId(null);
        firstPerson.setSkills(new String[]{"keine"});
        mockMvc.perform(post("/add")

                .flashAttr("person",firstPerson)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("person",
                        allOf(
                                hasProperty("projekteId", nullValue()),
                                hasProperty("skills", is(new String[]{"keine"})),
                                hasProperty("kontakt", is("tung@gmail.com")),
                                hasProperty("jahreslohn", is("10000")),
                                hasProperty("nachname", is("Stark")),
                                hasProperty("vorname", is("Tom"))
                        )
                ))

        ;
        verify(personRepository, times(1)).save(any());
        verify(projekteService, times(0)).getProjekte(any());
        verify(personEventService, times(1)).createEvent(any());

    }
    @Test
    public void editTEST_PersonnichtVorhanden() throws Exception {
        mockMvc.perform(get("/edit/{id}", 2L))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        verify(personRepository, times(1)).findById(anyLong());
        verify(projektRepository, times(1)).findAll();
        verifyZeroInteractions(personRepository);
    }

    @Test
    public void editTEST_PersonVorhanden() throws Exception {

        when(personRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(firstPerson));

        mockMvc.perform(get("/edit/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(request().attribute("projekte", hasItem(
                        allOf(
                                hasProperty("name", is("projekt4")),
                                hasProperty("beschreibung", is("description")),
                                hasProperty("startTime", is(Date.valueOf("2018-10-30"))),
                                hasProperty("last", is(10))
                        )
                )))
        ;
        verify(personRepository, times(1)).findById(1L);
        verifyZeroInteractions(personRepository);
    }
    @Test
    public void saveChanges_TEST() throws Exception {
        mockMvc.perform(post("/saveChanges/{id}", 2L)
                .flashAttr("person", firstPerson)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("confirmationEdit"))
                .andDo(print())

        ;
        verify(personRepository, times(1)).save(any());
        verify(personEventService, times(1)).editEvent(any());
        verify(projekteService, times(1)).getProjekte(any());

    }
    @Test
    public void deletePersonTest() throws Exception{
        mockMvc.perform(post("/delete/{id}",1L))
                .andExpect(status().isOk())
                .andExpect(view().name("confirmationDelete"));
        verify(personRepository, times(1)).deleteById(anyLong());
        verify(personEventService, times(1)).deleteEvent(any());

    }



}