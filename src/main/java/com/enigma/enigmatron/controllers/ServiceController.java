package com.enigma.enigmatron.controllers;

import com.enigma.enigmatron.entities.*;
import com.enigma.enigmatron.repos.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ServiceController {

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private CleaningGroupRepository cleaningGroupRepository;

    @Autowired
    private CleaningTurnRepository cleaningTurnRepository;

    @Autowired
    private OpeningRepository openingRepository;

    @Autowired
    private OpeningTurnRepository openingTurnRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;


    @PostMapping("/registerVolunteer")
    public String registerVolunteer(@RequestParam Integer id, @RequestParam String firstName) {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(id);
        volunteer.setFirstName(firstName);
        volunteerRepository.save(volunteer);
        return "Added new volunteer to repo!";
    }

    @PostMapping("/newCleaningGroup")
    public String newCleaningGroup() {
        CleaningGroup group = new CleaningGroup();
        cleaningGroupRepository.save(group);
        return "Instantiated a new cleaning group!";
    }

    @PostMapping("/assignVolunteerToCleaningGroup")
    public String assignVolunteerToCleaningGroup(@RequestParam Integer volunteerId, @RequestParam Integer groupId) {
        Volunteer volunteer = volunteerRepository.findVolunteerById(volunteerId);
        CleaningGroup group = cleaningGroupRepository.findCleaningGroupById(groupId);
        group.addVolunteer(volunteer);
        cleaningGroupRepository.save(group);
        return "Added " + volunteer.getFirstName() + " to cleaning group #" + groupId;
    }

    @PostMapping("/newCleaningTurn")
    public String newCleaningTurn(@RequestParam("date")
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
        CleaningTurn cleaningTurn = new CleaningTurn();
        cleaningTurn.setDate(date);
        cleaningTurnRepository.save(cleaningTurn);
        return "Registered a new cleaning turn for " + date;
    }

    @PostMapping("/assignCleaningGrouptoCleaningTurn")
    public String assignCleaningGroupToCleaningTurn(@RequestParam Integer groupId, @RequestParam Integer turnId) {
        CleaningGroup group = cleaningGroupRepository.findCleaningGroupById(groupId);
        CleaningTurn turn = cleaningTurnRepository.findCleaningTurnById(turnId);
        group.addCleaningTurn(turn);
        cleaningGroupRepository.save(group);
        return "Assigned turn of " + turn.getDate() + " to group #" + groupId;
    }

    @GetMapping("/cleaningCalendar")
    public String getCleaningCalendar() throws ParseException {
        List<CleaningTurn> turns = getFutureCleaningTurns();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(turns);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @GetMapping("/openingCalendar")
    public String getOpeningCalendar() throws ParseException {
        List<OpeningTurn> turns = getFutureOpeningTurns();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(turns);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @GetMapping("/volunteers")
    public String getVolunteers() {
        List<Volunteer> volunteers = volunteerRepository.findAll();
        System.out.println(volunteers);
        List<String> names = new ArrayList<String>();
        for (Volunteer v : volunteers)
            names.add(v.getFirstName());
        String json = new Gson().toJson(names);
        return json;
    }

    @GetMapping("/abstained")
    public String getAbstainedVolunteers() throws JsonProcessingException {
        List<Volunteer> volunteers = volunteerRepository.findAll();
        List<Volunteer> abstained = volunteers.stream()
                .filter(v -> v.getAvailabilities().isEmpty())
                .collect(Collectors.toList());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(abstained);
    }

    @GetMapping("/groups")
    public String getGroups() {
        List<CleaningGroup> groups = cleaningGroupRepository.findAll();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(groups);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/cleaningTurnsToday")
    public String getCleaningTurnsToday() {
        CleaningTurn turn = cleaningTurnRepository.findByDate(new Date());
        if (turn == null) return null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(turn);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/openingTurnsToday")
    public String getOpeningTurnsToday() {
        Opening opening = openingRepository.findOpeningByDate(new Date());
        if (opening == null) return null;
        OpeningTurn turn = opening.getOpeningTurn();
        if (turn == null) return null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(turn);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/updateCleaningTurns")
    public String updateCleaningTurns() {
        List<CleaningTurn> cleaningTurns = new ArrayList<CleaningTurn>();
        try {
            cleaningTurns = getFutureCleaningTurns();
        } catch (ParseException e) {
            e.printStackTrace();
            return "Error updating turns";
        }
        if (cleaningTurns.isEmpty()) {
            Calendar c = Calendar.getInstance();
            int diff = 1 - c.get(Calendar.DAY_OF_WEEK);
            if (diff <= 0)
                diff += 7;
            c.add(Calendar.DAY_OF_MONTH, diff);
            List<CleaningGroup> groups = cleaningGroupRepository.findAll();
            for (CleaningGroup g : groups) {
                CleaningTurn turn = new CleaningTurn();
                Date nextSunday = new Date(c.getTimeInMillis());
                turn.setDate(nextSunday);
                turn.setCleaningGroup(g);
                cleaningTurnRepository.save(turn);
                c.add(Calendar.DAY_OF_MONTH, 7);
            }
            return "Cleaning turns updated";
        }

        return "Calendar is not yet empty";
    }

    @PostMapping("/availability")
    public String addAvailability(@RequestParam Integer userId, @RequestParam Integer day,
                                  @RequestParam boolean available) {
        Volunteer volunteer = volunteerRepository.findVolunteerById(userId);
        Calendar c = Calendar.getInstance();
        int diff = 6 + day - c.get(Calendar.DAY_OF_WEEK);
        if (diff <= 0)
            diff += 7;
        c.add(Calendar.DAY_OF_MONTH, diff);
        Date date = new Date(c.getTimeInMillis());
        Opening opening = openingRepository.findOpeningByDate(date);
        if (opening == null) {
            opening = new Opening();
            opening.setDate(date);
        }
        Availability availability = new Availability();
        availability.setOpening(opening);
        availability.setAvailable(available);
        volunteer.addAvailability(availability);
        volunteerRepository.save(volunteer);
        //System.out.println(opening.getVolunteers());
        return "Registered availability.";
    }

    @GetMapping("/availability")
    public String getAvailabilities() throws JsonProcessingException {
        Calendar c = Calendar.getInstance();
        int diff = 6 - c.get(Calendar.DAY_OF_WEEK);
        if (diff <= 0)
            diff += 7;
        c.add(Calendar.DAY_OF_MONTH, diff);
        Date friday = new Date(c.getTimeInMillis());
        Opening fridayOpening = openingRepository.findOpeningByDate(friday);
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date saturday = new Date(c.getTimeInMillis());
        Opening saturdayOpening = openingRepository.findOpeningByDate(saturday);
        List<Opening> result = new ArrayList<Opening>();
        result.add(fridayOpening);
        result.add(saturdayOpening);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(result);
    }

    @PostMapping("/resetAvailability")
    public String resetAvailability(@RequestParam Integer userId) {
        Volunteer volunteer = volunteerRepository.findVolunteerById(userId);
        volunteer.resetAvailability();
        volunteerRepository.save(volunteer);
        return "Reset availabilities";
    }

    @PostMapping("/opening_turns")
    public String updateOpeningTurns() {
        Calendar c = Calendar.getInstance();
        int diff = 6 - c.get(Calendar.DAY_OF_WEEK);
        if (diff <= 0)
            diff += 7;
        c.add(Calendar.DAY_OF_MONTH, diff);
        Date friday = new Date(c.getTimeInMillis());
        Opening fridayOpening = openingRepository.findOpeningByDate(friday);
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date saturday = new Date(c.getTimeInMillis());
        Opening saturdayOpening = openingRepository.findOpeningByDate(saturday);

        if (fridayOpening.getAvailabilities().stream()
                .filter(a -> a.isAvailable()).count() < 3 &&
                saturdayOpening.getAvailabilities().stream()
                        .filter(a -> a.isAvailable()).count() < 3) {
            return "Not enough volunteers.";
        }

        List<Availability> availabilities = new ArrayList<Availability>();
        availabilities.addAll(fridayOpening.getAvailabilities());
        availabilities.addAll(saturdayOpening.getAvailabilities());
        List<Volunteer> availableFriday = new ArrayList<Volunteer>();
        List<Volunteer> availableSaturday = new ArrayList<Volunteer>();


        for (Availability a : fridayOpening.getAvailabilities()) {
            if (a.isAvailable())
                availableFriday.add(a.getVolunteer());
        }

        for (Availability a : saturdayOpening.getAvailabilities()) {
            if (a.isAvailable())
                availableSaturday.add(a.getVolunteer());
        }

        List<Volunteer> assignedFriday = new ArrayList<Volunteer>();
        List<Volunteer> assignedSaturday = new ArrayList<Volunteer>();

        List<Volunteer> availableBothDays = availableFriday.stream()
                .distinct()
                .filter(availableSaturday::contains)
                .collect(Collectors.toList());

        assignedFriday = availableFriday.stream()
                .filter(v -> !availableBothDays.contains(v))
                .collect(Collectors.toList());

        assignedSaturday = availableSaturday.stream()
                .filter(v -> !availableBothDays.contains(v))
                .collect(Collectors.toList());

        for (Volunteer v : availableBothDays) {
            if (assignedFriday.size() < 3) {
                assignedFriday.add(v);
            }
            else if (assignedSaturday.size() < 3) {
                assignedSaturday.add(v);
            }
            else {
                if (assignedFriday.size() > assignedSaturday.size()) {
                    assignedSaturday.add(v);
                }
                else {
                    assignedFriday.add(v);
                }
            }
        }

        if (assignedFriday.size() < 3 || assignedSaturday.size() < 3) {
            for (Volunteer v : availableBothDays) {
                if (assignedFriday.size() < 3 && !assignedFriday.contains(v)) {
                    assignedFriday.add(v);
                }
                else if (assignedSaturday.size() < 3 && !assignedSaturday.contains(v)) {
                    assignedSaturday.add(v);
                }
            }
        }
        OpeningTurn fridayTurn = new OpeningTurn();
        fridayTurn.setOpening(fridayOpening);
        fridayTurn.addVolunteers(assignedFriday);
        OpeningTurn saturdayTurn = new OpeningTurn();
        saturdayTurn.setOpening(saturdayOpening);
        saturdayTurn.addVolunteers(assignedSaturday);
        openingTurnRepository.save(fridayTurn);
        openingTurnRepository.save(saturdayTurn);

        availabilityRepository.deleteAll();

        return "Updated turns";
    }

    private List<CleaningTurn> getFutureCleaningTurns() throws ParseException {
        List<CleaningTurn> turns = cleaningTurnRepository.findAll();
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd");
        Date today = formatter.parse(formatter.format(new Date()));
        return turns.stream().filter(turn -> !turn.getDate().before(today)).collect(Collectors.toList());
    }

    private List<OpeningTurn> getFutureOpeningTurns() throws ParseException {
        List<OpeningTurn> turns = openingTurnRepository.findAll();
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd");
        Date today = formatter.parse(formatter.format(new Date()));
        return turns.stream().filter(turn -> !turn.getOpening().getDate().before(today)).collect(Collectors.toList());
    }
}
