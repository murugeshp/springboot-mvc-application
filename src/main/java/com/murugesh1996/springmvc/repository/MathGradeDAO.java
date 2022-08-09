package com.murugesh1996.springmvc.repository;

import com.murugesh1996.springmvc.models.MathGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MathGradeDAO extends JpaRepository<MathGrade, Integer> {
    public Iterable<MathGrade> findGradeByStudentId(int id);

    public void deleteByStudentId(int id);
}
