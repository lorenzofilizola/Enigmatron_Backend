package com.enigma.enigmatron.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CleaningGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Integer id;

    @OneToMany(mappedBy = "cleaningGroup")
    @JsonIgnore
    private List<CleaningTurn> cleaningTurns;

    @OneToMany(mappedBy = "cleaningGroup", cascade = CascadeType.ALL)
    private List<Volunteer> volunteers = new ArrayList<Volunteer>();

    public List<Volunteer> getVolunteers() {
        return volunteers;
    }

    public void addVolunteer(Volunteer volunteer) {
        if (volunteer.getCleaningGroup() != null) {
            throw new RuntimeException("Volunteer is already assigned to a cleaning group");
        }
        this.volunteers.add(volunteer);
        volunteer.setCleaningGroup(this);
    }

    public void addCleaningTurn(CleaningTurn turn) {
        if (turn.getCleaningGroup() != null) {
            throw new RuntimeException("Turn is already assigned to a cleaning group");
        }
        this.cleaningTurns.add(turn);
        turn.setCleaningGroup(this);
    }
}
