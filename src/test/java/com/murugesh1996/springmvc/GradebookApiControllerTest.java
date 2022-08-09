package com.murugesh1996.springmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.murugesh1996.springmvc.models.CollegeStudent;
import com.murugesh1996.springmvc.models.GradebookCollegeStudent;
import com.murugesh1996.springmvc.repository.HistoryGradeDAO;
import com.murugesh1996.springmvc.repository.MathGradeDAO;
import com.murugesh1996.springmvc.repository.ScienceGradeDAO;
import com.murugesh1996.springmvc.repository.StudentDAO;
import com.murugesh1996.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource("/application-test.properties")
@Transactional
public class GradebookApiControllerTest {

    private static MockHttpServletRequest request;

    @PersistenceContext
    private EntityManager entityManager;

    @Mock
    StudentAndGradeService studentAndGradeService;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CollegeStudent collegeStudent;

    @Autowired
    StudentDAO studentDAO;

    @Autowired
    StudentAndGradeService studentService;

    @Autowired
    MathGradeDAO mathGradeDAO;

    @Autowired
    ScienceGradeDAO scienceGradeDAO;

    @Autowired
    HistoryGradeDAO historyGradeDAO;

    @Value("${sql.script.create.student}")
    private String sqlAddStudennt;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudennt;

    @Value("${sql.script.create.math.grade}")
    private String sqlCreateMathGrade;

    @Value("${sql.script.create.science.grade}")
    private String sqlCreateScienceGrade;

    @Value("${sql.script.create.history.grade}")
    private String sqlCreateHistoryGrade;

    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;

    public static final MediaType APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON;

    @BeforeAll
    public static void setUp() {
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "Murugesh");
        request.setParameter("lastname", "Palanisamy");
        request.setParameter("emailAddress", "Murugesh2022@gmail.com");
    }

    @BeforeEach
    public void setUpDatabase(){
        jdbc.execute(sqlAddStudennt);
        jdbc.execute(sqlCreateMathGrade);
        jdbc.execute(sqlCreateScienceGrade);
        jdbc.execute(sqlCreateHistoryGrade);
    }

    @AfterEach
    public void setUpAfterTransaction(){

        jdbc.execute(sqlDeleteStudennt);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }

    @Test
    public void getStudentHttpRequest() throws Exception{

        CollegeStudent studentOne = new CollegeStudent("Murugesh", "Palanisamy",
                "Murugesh2022@gmail.com");
        entityManager.persist(studentOne);
        entityManager.flush();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void createStudentHttpRequest() throws Exception {

        CollegeStudent studentOne = new CollegeStudent("Murugesh", "Palanisamy",
                "Murugesh2022@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentOne)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(2)));
        CollegeStudent collegeStudent = studentDAO.findByEmailAddress("Murugesh2022@gmail.com");
        assertNotNull(collegeStudent, "Student should be found");
    }

    @Test
    public void deleteStudentHttpRequest() throws Exception {
        assertTrue(studentDAO.findById(1).isPresent(), "Student is present");
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/student/{id}", 1))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void deleteStudentHttpRequestErrorPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/student/{id}", 0))
                        .andExpect(status().is4xxClientError())
                        .andExpect(jsonPath("$.status", is(404)))
                        .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void studentInformationHttpRequest() throws Exception {
        assertTrue(studentDAO.findById(1).isPresent());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/studentInformation/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstname", is("Murugesh")))
                .andExpect(jsonPath("$.lastname", is("Palanisamy")))
                .andExpect(jsonPath("$.emailAddress", is("Murugesh1996@gmail.com")));
    }

    @Test
    public void studentInformationHttpRequestDoesNotExists() throws Exception {
        assertFalse(studentDAO.findById(0).isPresent());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/studentInformation/{id}", 0))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void createValidGradeHttpRequest() throws Exception {
        assertTrue(studentDAO.findById(1).isPresent());
        GradebookCollegeStudent student = studentService.studentInformation(1);
        assertEquals(1, student.getStudentGrades().getMathGradeResults().size());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "85.00")
                        .param("gradeType", "math")
                        .param("studentId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstname", is("Murugesh")))
                .andExpect(jsonPath("$.lastname", is("Palanisamy")))
                .andExpect(jsonPath("$.emailAddress", is("Murugesh1996@gmail.com")))
                .andExpect(jsonPath("$.studentGrades.mathGradeResults", hasSize(2)));
    }

    @Test
    public void createValidGradeHttpRequestStudentDoesNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "85.00")
                        .param("gradeType", "math")
                        .param("studentId", "0"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void createANonValidGradeHttpRequestGradeTypeDoesNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "85.00")
                        .param("gradeType", "social")
                        .param("studentId", "1"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void deleteAValidGradeHttpRequest() throws Exception{
        assertTrue(mathGradeDAO.findById(1).isPresent());
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/grades/{id}/{gradeType}", 1, "math"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstname", is("Murugesh")))
                .andExpect(jsonPath("$.lastname", is("Palanisamy")))
                .andExpect(jsonPath("$.emailAddress", is("Murugesh1996@gmail.com")))
                .andExpect(jsonPath("$.studentGrades.mathGradeResults", hasSize(0)));
    }

    @Test
    public void deleteAValidGradeHttpRequestGradeIdDoesNotExists() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/grades/{id}/{gradeType}", 2, "history"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    public void deleteAValidGradeHttpRequestGradeTypeDoesNotExists() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/grades/{id}/{gradeType}", 1, "social"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }
}
