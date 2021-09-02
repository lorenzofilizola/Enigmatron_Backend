package com.enigma.enigmatron.repos;

import com.enigma.enigmatron.entities.CleaningGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CleaningGroupRepository extends CrudRepository<CleaningGroup, Integer> {

    CleaningGroup findCleaningGroupById(Integer id);
    List<CleaningGroup> findAll();
}
