package com.murugesh1996.springmvc.controller;

import com.murugesh1996.springmvc.models.CollegeStudent;
import com.murugesh1996.springmvc.models.Gradebook;
import com.murugesh1996.springmvc.models.GradebookCollegeStudent;
import com.murugesh1996.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradebookController {

    @Autowired
    private Gradebook gradebook;

    @Autowired
	private StudentAndGradeService studentService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getStudents(Model m) {
        Iterable<CollegeStudent> collegeStudents = studentService.getGradebook();
		m.addAttribute("students", collegeStudents);
		return "index";
    }


    @GetMapping("/studentInformation/{id}")
    public String studentInformation(@PathVariable int id, Model m) {
        if(!studentService.checkIfStudentIsPresent(id)){
            return "error";
        }
        GradebookCollegeStudent studentEntity = studentService.studentInformation(id);
        m.addAttribute("student", studentEntity);
        studentService.configureStudentInformationModel(id, m);
        return "studentInformation";
    }

    @PostMapping("/")
    public String createStudent(@ModelAttribute("student") CollegeStudent student, Model m){
        studentService.createStudent(student.getId(), student.getFirstname(), student.getLastname(), student.getEmailAddress());
        Iterable<CollegeStudent> collegeStudents = studentService.getGradebook();
        m.addAttribute("students", collegeStudents);
        return "index";
    }

    @GetMapping("/delete/student/{id}")
    public String deleteStudent(@PathVariable int id, Model m) {
        if(!studentService.checkIfStudentIsPresent(id))
            return "error";
        studentService.deleteStudent(id);
        Iterable<CollegeStudent> collegeStudents = studentService.getGradebook();
        m.addAttribute("students", collegeStudents);
        return "index";
    }

    @PostMapping("/grades")
    public String createGrade(@RequestParam("grade") double grade,
                              @RequestParam("gradeType") String gradeType,
                              @RequestParam("studentId") int studentId,
                              Model m){

        if(!studentService.checkIfStudentIsPresent(studentId)){
            return "error";
        }
        boolean success = studentService.createGrade(grade, studentId, gradeType);
        if(!success){
            return "error";
        }
        studentService.configureStudentInformationModel(studentId, m);
        return "studentInformation";
    }

    @GetMapping("/grades/{id}/{gradeType}")
    public String deleteGrade(@PathVariable("id") int gradeId,
                              @PathVariable("gradeType") String gradeType,
                              Model m){

        int studentId = studentService.deleteGrade(gradeId, gradeType);
        if(studentId==-1)
            return "error";
        studentService.configureStudentInformationModel(studentId, m);

        return "studentInformation";
    }
}
