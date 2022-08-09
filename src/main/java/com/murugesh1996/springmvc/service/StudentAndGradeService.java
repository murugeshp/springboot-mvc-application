package com.murugesh1996.springmvc.service;

import com.murugesh1996.springmvc.models.*;
import com.murugesh1996.springmvc.repository.HistoryGradeDAO;
import com.murugesh1996.springmvc.repository.MathGradeDAO;
import com.murugesh1996.springmvc.repository.ScienceGradeDAO;
import com.murugesh1996.springmvc.repository.StudentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    StudentDAO studentDAO;

    @Autowired
    @Qualifier("mathGrades")
    MathGrade mathGrade;

    @Autowired
    @Qualifier("scienceGrades")
    ScienceGrade scienceGrade;

    @Autowired
    @Qualifier("historyGrades")
    HistoryGrade historyGrade;

    @Autowired
    MathGradeDAO mathGradeDAO;

    @Autowired
    ScienceGradeDAO scienceGradeDAO;

    @Autowired
    HistoryGradeDAO historyGradeDAO;

    @Autowired
    StudentGrades studentGrades;

    public void createStudent(int id, String firstName, String lastName, String email){
        CollegeStudent collegeStudent = new CollegeStudent( firstName,  lastName,  email);
        collegeStudent.setId(id);
        studentDAO.save(collegeStudent);
    }

    public boolean checkIfStudentIsPresent(int id) {
        Optional<CollegeStudent> student = studentDAO.findById(id);
        if(student.isPresent())
            return true;
        return false;
    }

    public boolean checkIfGradeIsPresent(int id, String gradeType) {

        if(gradeType.equals("math")){
            if(mathGradeDAO.findById(id).isPresent())
                return true;
        }else if(gradeType.equals("science")){
            if(scienceGradeDAO.findById(id).isPresent())
                return true;
        }else if(gradeType.equals("history")){
            if(historyGradeDAO.findById(id).isPresent())
                return true;
        }

        return false;
    }

    public void deleteStudent(int id) {
        if (checkIfStudentIsPresent(id))
            studentDAO.deleteById(id);
        if(checkIfGradeIsPresentByStudentId(id, "math")){
            mathGradeDAO.deleteByStudentId(id);
        }
        if(checkIfGradeIsPresent(id, "science")){
            scienceGradeDAO.deleteByStudentId(id);
        }
        if(checkIfGradeIsPresent(id, "history")){
            historyGradeDAO.deleteByStudentId(id);
        }
    }

    private boolean checkIfGradeIsPresentByStudentId(int id, String gradeType) {
        if(gradeType.equals("math")){
            if(mathGradeDAO.findGradeByStudentId(id).iterator().hasNext())
                return true;
        }else if(gradeType.equals("science")){
            if(scienceGradeDAO.findGradeByStudentId(id).iterator().hasNext())
                return true;
        }else if(gradeType.equals("history")){
            if(historyGradeDAO.findGradeByStudentId(id).iterator().hasNext())
                return true;
        }
        return false;
    }

    public Iterable<CollegeStudent> getGradebook() {
        return studentDAO.findAll();
    }

    public Gradebook getGradebookObject () {

        Iterable<CollegeStudent> collegeStudents = studentDAO.findAll();

        Iterable<MathGrade> mathGrades = mathGradeDAO.findAll();

        Iterable<ScienceGrade> scienceGrades = scienceGradeDAO.findAll();

        Iterable<HistoryGrade> historyGrades = historyGradeDAO.findAll();

        Gradebook gradebook = new Gradebook();

        for (CollegeStudent collegeStudent : collegeStudents) {
            List<Grade> mathGradesPerStudent = new ArrayList<>();
            List<Grade> scienceGradesPerStudent = new ArrayList<>();
            List<Grade> historyGradesPerStudent = new ArrayList<>();

            for (MathGrade grade : mathGrades) {
                if (grade.getStudentId() == collegeStudent.getId()) {
                    mathGradesPerStudent.add(grade);
                }
            }
            for (ScienceGrade grade : scienceGrades) {
                if (grade.getStudentId() == collegeStudent.getId()) {
                    scienceGradesPerStudent.add(grade);
                }
            }

            for (HistoryGrade grade : historyGrades) {
                if (grade.getStudentId() == collegeStudent.getId()) {
                    historyGradesPerStudent.add(grade);
                }
            }

            studentGrades.setMathGradeResults(mathGradesPerStudent);
            studentGrades.setScienceGradeResults(scienceGradesPerStudent);
            studentGrades.setHistoryGradeResults(historyGradesPerStudent);

            GradebookCollegeStudent gradebookCollegeStudent = new GradebookCollegeStudent(collegeStudent.getId(), collegeStudent.getFirstname(), collegeStudent.getLastname(),
                    collegeStudent.getEmailAddress(), studentGrades);

            gradebook.getStudents().add(gradebookCollegeStudent);
        }

        return gradebook;
    }

    public boolean createGrade(double grade, int id, String gradeType) {
        if(!checkIfStudentIsPresent(id))
            return false;
        if(grade>=0 && grade<=100)
            if(gradeType.equals("math")){
                mathGrade.setId(0);
                mathGrade.setGrade(grade);
                mathGrade.setStudentId(id);
                mathGradeDAO.save(mathGrade);
                return true;
            }else if(gradeType.equals("science")){
                scienceGrade.setId(0);
                scienceGrade.setGrade(grade);
                scienceGrade.setStudentId(id);
                scienceGradeDAO.save(scienceGrade);
                return true;
            }else if(gradeType.equals("history")){
                historyGrade.setId(0);
                historyGrade.setGrade(grade);
                historyGrade.setStudentId(id);
                historyGradeDAO.save(historyGrade);
                return true;
            }
        return false;
    }

    public int deleteGrade(int id, String gradeType) {
        int studentId = -1;

        if (gradeType.equals("math")) {
            Optional<MathGrade> grade = mathGradeDAO.findById(id);
            if (!grade.isPresent()) {
                return studentId;
            }
            studentId = grade.get().getStudentId();
            mathGradeDAO.deleteById(id);
        }
        if (gradeType.equals("science")) {
            Optional<ScienceGrade> grade = scienceGradeDAO.findById(id);
            if (!grade.isPresent()) {
                return studentId;
            }
            studentId = grade.get().getStudentId();
            scienceGradeDAO.deleteById(id);
        }
        if (gradeType.equals("history")) {
            Optional<HistoryGrade> grade = historyGradeDAO.findById(id);
            if (!grade.isPresent()) {
                return studentId;
            }
            studentId = grade.get().getStudentId();
            historyGradeDAO.deleteById(id);
        }

        return studentId;
    }

    public GradebookCollegeStudent studentInformation(int id) {
        CollegeStudent  collegeStudent= new CollegeStudent();
        List<Grade> mathGradeList = new ArrayList<>();
        List<Grade> scienceGradeList = new ArrayList<>();
        List<Grade> historyGradeList = new ArrayList<>();
        StudentGrades studentGrades = new StudentGrades();

        if(checkIfStudentIsPresent(id)){
            collegeStudent = studentDAO.findById(id).get();
        }else {
            return null;
        }
        if(checkIfGradeIsPresentByStudentId(id,"math")){
            mathGradeDAO.findGradeByStudentId(id).forEach(mathGradeList::add);
        }
        if(checkIfGradeIsPresentByStudentId(id,"science")){
            scienceGradeDAO.findGradeByStudentId(id).forEach(scienceGradeList::add);
        }
        if(checkIfGradeIsPresentByStudentId(id,"history")){
            historyGradeDAO.findGradeByStudentId(id).forEach(historyGradeList::add);
        }

        studentGrades.setMathGradeResults(mathGradeList);
        studentGrades.setScienceGradeResults(scienceGradeList);
        studentGrades.setHistoryGradeResults(historyGradeList);

        GradebookCollegeStudent gradebookCollegeStudent = new GradebookCollegeStudent(
                collegeStudent.getId(),
                collegeStudent.getFirstname(),
                collegeStudent.getLastname(),
                collegeStudent.getEmailAddress(),
                studentGrades);

        return gradebookCollegeStudent;
    }

    public void configureStudentInformationModel(int studentId, Model m) {
        GradebookCollegeStudent studentEntity = studentInformation(studentId);
        m.addAttribute("student", studentEntity);
        if(studentEntity.getStudentGrades().getMathGradeResults().size() > 0){
            m.addAttribute("mathAverage", studentEntity.getStudentGrades().findGradePointAverage(
                    studentEntity.getStudentGrades().getMathGradeResults()
            ));
        }else{
            m.addAttribute("mathAverage","N/A");
        }
        if(studentEntity.getStudentGrades().getScienceGradeResults().size() > 0){
            m.addAttribute("scienceAverage", studentEntity.getStudentGrades().findGradePointAverage(
                    studentEntity.getStudentGrades().getScienceGradeResults()
            ));
        }else{
            m.addAttribute("scienceAverage","N/A");
        }
        if(studentEntity.getStudentGrades().getHistoryGradeResults().size() > 0){
            m.addAttribute("historyAverage", studentEntity.getStudentGrades().findGradePointAverage(
                    studentEntity.getStudentGrades().getHistoryGradeResults()
            ));
        }else{
            m.addAttribute("historyAverage","N/A");
        }
    }
}
