package com.murugesh1996.springmvc.repository;

import com.murugesh1996.springmvc.models.HistoryGrade;
import com.murugesh1996.springmvc.models.ScienceGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryGradeDAO extends JpaRepository<HistoryGrade, Integer> {
    public Iterable<HistoryGrade> findGradeByStudentId(int id);

    public void deleteByStudentId(int id);
}
