package com.enigma.enigmatron.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "volunteer")
public class Volunteer {
    @Id
    @JsonProperty("id")
    private Integer id;

    public void setId(Integer id)
    {
        this.id = id;
    }

    String firstName;

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }


    @ManyToOne
    @JoinColumn(name = "cleaning_group_id")
    @JsonIgnore
    private CleaningGroup cleaningGroup;

    public CleaningGroup getCleaningGroup() {
        return cleaningGroup;
    }

    public void setCleaningGroup(CleaningGroup cleaningGroup) {
        this.cleaningGroup = cleaningGroup;
    }

    @OneToMany(mappedBy = "volunteer", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JsonIgnore
    private List<Availability> availabilities = new ArrayList<Availability>();

    public List<Availability> getAvailabilities() {
        return availabilities;
    }

    public void addAvailability(Availability availability) {
        this.availabilities.add(availability);
        availability.setVolunteer(this);
    }

    public void resetAvailability() {
        this.availabilities.clear();
    }

    @ManyToMany
    @JoinTable(name="assignment",
    joinColumns = @JoinColumn(name="volunteer_id"),
    inverseJoinColumns = @JoinColumn(name="opening_turn_id"))
    @JsonIgnore
    private List<OpeningTurn> openingTurns;
}
