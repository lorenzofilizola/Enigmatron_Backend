package com.enigma.enigmatron.repos;

import com.enigma.enigmatron.entities.OpeningTurn;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OpeningTurnRepository extends CrudRepository<OpeningTurn, Integer> {
    List<OpeningTurn> findAll();
}
