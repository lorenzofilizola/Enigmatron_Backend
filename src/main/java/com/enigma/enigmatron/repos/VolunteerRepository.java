package com.enigma.enigmatron.repos;

import com.enigma.enigmatron.entities.Volunteer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VolunteerRepository extends CrudRepository<Volunteer, Integer> {

    Volunteer findVolunteerById(Integer id);
    List<Volunteer> findAll();
}
