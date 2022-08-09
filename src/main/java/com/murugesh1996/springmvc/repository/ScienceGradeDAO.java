package com.murugesh1996.springmvc.repository;

import com.murugesh1996.springmvc.models.MathGrade;
import com.murugesh1996.springmvc.models.ScienceGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScienceGradeDAO extends JpaRepository<ScienceGrade, Integer> {
    public Iterable<ScienceGrade> findGradeByStudentId(int id);

    public void deleteByStudentId(int id);
}
