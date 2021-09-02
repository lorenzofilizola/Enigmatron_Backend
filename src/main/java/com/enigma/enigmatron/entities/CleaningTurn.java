package com.enigma.enigmatron.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cleaning_turn")
public class CleaningTurn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "cleaning_group_id")
    private CleaningGroup cleaningGroup;


    public CleaningGroup getCleaningGroup() {
        return this.cleaningGroup;
    }

    public void setCleaningGroup(CleaningGroup cleaningGroup) {
        this.cleaningGroup = cleaningGroup;
    }
}
