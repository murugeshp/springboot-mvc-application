package com.murugesh1996.springmvc;

import com.murugesh1996.springmvc.models.*;
import com.murugesh1996.springmvc.repository.HistoryGradeDAO;
import com.murugesh1996.springmvc.repository.MathGradeDAO;
import com.murugesh1996.springmvc.repository.ScienceGradeDAO;
import com.murugesh1996.springmvc.repository.StudentDAO;
import com.murugesh1996.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MvcTestingExampleApplication.class)
@ActiveProfiles("test")
public class StudentAndGradeServiceTest {

    @Autowired
    private JdbcTemplate jdbc;

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
    public void createStudentService(){
        studentService.createStudent(1,"Murugesh", "palansiamy", "murugesh1996@gmail.com");
        CollegeStudent collegeStudent = studentDAO.findByEmailAddress("murugesh1996@gmail.com");
        assertEquals("murugesh1996@gmail.com", collegeStudent.getEmailAddress());
    }

    @Test
    public void isStudentNullCheck(){
        assertTrue(studentService.checkIfStudentIsPresent(1));
        assertFalse(studentService.checkIfStudentIsPresent(0));
    }

    @Test
    public void deleteStudentService(){
        assertTrue(studentDAO.findById(1).isPresent(),"Student is present");
        assertTrue(mathGradeDAO.findById(1).isPresent(),"Math grade is deleted");
        assertTrue(scienceGradeDAO.findById(1).isPresent(),"Science grade is deleted");
        assertTrue(historyGradeDAO.findById(1).isPresent(),"History grade is deleted");

        studentService.deleteStudent(1);

        assertFalse(studentDAO.findById(1).isPresent(),"Student is deleted");
        assertFalse(mathGradeDAO.findById(1).isPresent(),"Math is deleted");
        assertFalse(scienceGradeDAO.findById(1).isPresent(),"Science is deleted");
        assertFalse(historyGradeDAO.findById(1).isPresent(),"History is deleted");
    }

    @Sql("/insertData.sql")
    @Test
    public void getStudentGradebook(){
        Iterable<CollegeStudent> collegeStudentIterable = studentService.getGradebook();
        List<CollegeStudent> collegeStudents = new ArrayList<>();
        for(CollegeStudent collegeStudent: collegeStudentIterable){
            collegeStudents.add(collegeStudent);
        }
        assertEquals(3, collegeStudents.size());
    }

    @Test
    public void createGradeService(){
        assertTrue(studentService.createGrade(80.50, 1, "math"));
        assertTrue(studentService.createGrade(90.50, 1, "science"));
        assertTrue(studentService.createGrade(95.00, 1, "history"));
        Iterable<MathGrade> mathGrades = mathGradeDAO.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDAO.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDAO.findGradeByStudentId(1);
        assertTrue(((Collection<MathGrade>) mathGrades).size() == 2, "Student has math grade");
        assertTrue(((Collection<HistoryGrade>) historyGrades).size() == 2, "Student has math grade");
        assertTrue(((Collection<ScienceGrade>) scienceGrades).size() == 2, "Student has math grade");
    }

    @Test
    public void createGradeServiceReturnFalse(){
        assertFalse(studentService.createGrade(110.50, 1, "math"));
        assertFalse(studentService.createGrade(80.50, 2, "math"));
        assertFalse(studentService.createGrade(-80.50, 1, "math"));
        assertFalse(studentService.createGrade(95.00, 1, "social"));
    }

    @Test
    public void deleteGradeService(){
        assertEquals(1, studentService.deleteGrade(1, "math"));
        assertEquals(1, studentService.deleteGrade(1, "science"));
        assertEquals(1, studentService.deleteGrade(1, "history"));
        assertFalse(mathGradeDAO.findById(1).isPresent(), "Student math grade deleted");
        assertFalse(scienceGradeDAO.findById(1).isPresent(), "Student science  grade deleted");
        assertFalse(historyGradeDAO.findById(1).isPresent(), "Student history grade deleted");
    }

    @Test
    public void deleteGradeServiceReturnFalse(){
        assertNotEquals(1, studentService.deleteGrade(5, "math"));
        assertNotEquals(1, studentService.deleteGrade(1, "social"));
    }

    @Test
    public void studentInformation(){
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(1);
        assertNotNull(gradebookCollegeStudent);
        assertEquals(1, gradebookCollegeStudent.getId());
        assertEquals("Murugesh", gradebookCollegeStudent.getFirstname());
        assertEquals("Palanisamy", gradebookCollegeStudent.getLastname());
        assertEquals("Murugesh1996@gmail.com", gradebookCollegeStudent.getEmailAddress());
        assertTrue(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size() == 1);
    }

    @Test
    public void studentInformationServiceReturnNull(){
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(0);
        assertNull(gradebookCollegeStudent);
    }

}
