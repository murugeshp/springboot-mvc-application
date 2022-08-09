package com.murugesh1996.springmvc.controller;

import com.murugesh1996.springmvc.exceptionhandlingexceptionhandling.StudentOrGradeErrorResponse;
import com.murugesh1996.springmvc.exceptionhandlingexceptionhandling.StudentOrGradeNotFoundException;
import com.murugesh1996.springmvc.models.CollegeStudent;
import com.murugesh1996.springmvc.models.Gradebook;
import com.murugesh1996.springmvc.models.*;
import com.murugesh1996.springmvc.models.GradebookCollegeStudent;
import com.murugesh1996.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GradebookRestApiController {

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private Gradebook gradebook;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<GradebookCollegeStudent> getStudents() {
        gradebook = studentService.getGradebookObject();
        return gradebook.getStudents();
    }


    @GetMapping("/studentInformation/{id}")
    public GradebookCollegeStudent studentInformation(@PathVariable int id) {

        if (!studentService.checkIfStudentIsPresent(id)) {
            throw new StudentOrGradeNotFoundException("Student or Grade was not found");
        }
        GradebookCollegeStudent studentEntity = studentService.studentInformation(id);

        return studentEntity;
    }

    @PostMapping(value = "/")
    public List<GradebookCollegeStudent> createStudent(@RequestBody CollegeStudent student) {

        studentService.createStudent(student.getId(), student.getFirstname(), student.getLastname(), student.getEmailAddress());
        gradebook = studentService.getGradebookObject();
        return gradebook.getStudents();
    }

    @DeleteMapping("/student/{id}")
    public List<GradebookCollegeStudent> deleteStudent(@PathVariable int id) {

        if (!studentService.checkIfStudentIsPresent(id)) {
            throw new StudentOrGradeNotFoundException("Student or Grade was not found");
        }

        studentService.deleteStudent(id);
        gradebook = studentService.getGradebookObject();
        return gradebook.getStudents();
    }

    @PostMapping(value = "/grades")
    public GradebookCollegeStudent createGrade(@RequestParam("grade") double grade,
                                               @RequestParam("gradeType") String gradeType,
                                               @RequestParam("studentId") int studentId) {

        if (!studentService.checkIfStudentIsPresent(studentId)) {
            throw new StudentOrGradeNotFoundException("Student or Grade was not found");
        }

        boolean success = studentService.createGrade(grade, studentId, gradeType);

        if (!success) {
            throw new StudentOrGradeNotFoundException("Student or Grade was not found");
        }

        GradebookCollegeStudent studentEntity = studentService.studentInformation(studentId);

        if (studentEntity == null) {
            throw new StudentOrGradeNotFoundException("Student or Grade was not found");
        }

        return studentEntity;
    }

    @DeleteMapping("/grades/{id}/{gradeType}")
    public GradebookCollegeStudent deleteGrade(@PathVariable int id, @PathVariable String gradeType) {

        int studentId = studentService.deleteGrade(id, gradeType);

        if (studentId == -1) {
            throw new StudentOrGradeNotFoundException("Student or Grade was not found");
        }

        GradebookCollegeStudent studentEntity = studentService.studentInformation(studentId);

        return studentEntity;
    }

    @ExceptionHandler
    public ResponseEntity<StudentOrGradeErrorResponse> handleException(StudentOrGradeNotFoundException exc) {

        StudentOrGradeErrorResponse error = new StudentOrGradeErrorResponse();

        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<StudentOrGradeErrorResponse> handleException(Exception exc) {

        StudentOrGradeErrorResponse error = new StudentOrGradeErrorResponse();

        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
