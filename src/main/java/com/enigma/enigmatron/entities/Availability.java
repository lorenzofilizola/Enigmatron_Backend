package com.enigma.enigmatron.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name = "availability")
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonProperty("available")
    private boolean available;

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }



    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    @JsonProperty("volunteer")
    private Volunteer volunteer;

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    @ManyToOne
    @JoinColumn(name = "opening_id")
    @JsonIgnore
    private Opening opening;

    public void setOpening(Opening opening) {
        this.opening = opening;
    }

}
