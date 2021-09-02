package com.enigma.enigmatron.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "opening_turn")
public class OpeningTurn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;

    @OneToOne
    @JsonProperty("opening")
    private Opening opening;

    public Opening getOpening() {
        return opening;
    }

    public void setOpening(Opening opening) {
        this.opening = opening;
    }

    @ManyToMany
    @JoinTable(name="assignment",
            joinColumns = @JoinColumn(name="opening_turn_id"),
            inverseJoinColumns = @JoinColumn(name="volunteer_id"))
    @JsonProperty("volunteers")
    private List<Volunteer> volunteers = new ArrayList<Volunteer>();

    public void addVolunteers(List<Volunteer> volunteers) {
        this.volunteers.addAll(volunteers);
    }

}
