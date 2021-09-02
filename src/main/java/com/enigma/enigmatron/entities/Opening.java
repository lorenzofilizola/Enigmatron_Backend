package com.enigma.enigmatron.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "opening")
public class Opening {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Temporal(TemporalType.DATE)
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @OneToMany(mappedBy = "opening")
    @JsonProperty("availabilities")
    private List<Availability> availabilities = new ArrayList<Availability>();

    public List<Availability> getAvailabilities() {
        return availabilities;
    }

    public void addAvailability(Availability availability) {
        this.availabilities.add(availability);
    }

    @OneToOne(mappedBy = "opening")
    @JsonIgnore
    private OpeningTurn openingTurn;

    public OpeningTurn getOpeningTurn() {
        return openingTurn;
    }

}
