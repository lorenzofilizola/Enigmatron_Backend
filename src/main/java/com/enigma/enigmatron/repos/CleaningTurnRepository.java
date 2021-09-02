package com.enigma.enigmatron.repos;

import com.enigma.enigmatron.entities.CleaningTurn;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface CleaningTurnRepository extends CrudRepository<CleaningTurn, Integer> {

    CleaningTurn findCleaningTurnById(Integer id);
    List<CleaningTurn> findAll();
    CleaningTurn findByDate(Date date);
}
