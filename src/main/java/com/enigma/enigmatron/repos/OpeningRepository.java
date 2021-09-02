package com.enigma.enigmatron.repos;

import com.enigma.enigmatron.entities.Opening;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface OpeningRepository extends CrudRepository<Opening, Integer> {
    Opening findOpeningByDate(Date date);
}
