package com.murugesh1996.springmvc.repository;

import com.murugesh1996.springmvc.models.CollegeStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentDAO extends CrudRepository<CollegeStudent, Integer>{

    public CollegeStudent findByEmailAddress(String emailAddress);
}
