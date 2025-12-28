package ua.kiev.univ.schedule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kiev.univ.schedule.entity.*;
import ua.kiev.univ.schedule.repository.AppointmentRepository;
import ua.kiev.univ.schedule.repository.AuditoriumRepository;
import ua.kiev.univ.schedule.repository.LessonRepository;

import java.util.*;

@Service
public class ScheduleService {

    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AuditoriumRepository auditoriumRepository;

    private static final int DAYS_IN_WEEK = 5;
    private static final int SLOTS_PER_DAY = 4;

    @Transactional
    public void generateSchedule() {
        // 1. Clear existing schedule
        appointmentRepository.deleteAll();

        // 2. Load all lessons and auditoriums
        List<Lesson> lessons = lessonRepository.findAll();
        List<Auditorium> auditoriums = auditoriumRepository.findAll();

        // 3. Simple greedy algorithm
        // We iterate through all time slots (Day + Pair)
        // For each slot, we try to fit as many lessons as possible
        
        // Map to track busy resources per slot
        // Key: SlotIndex (0..19), Value: Set of occupied IDs (TeacherID, GroupID)
        Map<Integer, Set<Long>> teacherBusy = new HashMap<>();
        Map<Integer, Set<Long>> groupBusy = new HashMap<>();
        Map<Integer, Set<Long>> auditoriumBusy = new HashMap<>();

        for (int slot = 0; slot < DAYS_IN_WEEK * SLOTS_PER_DAY; slot++) {
            teacherBusy.put(slot, new HashSet<>());
            groupBusy.put(slot, new HashSet<>());
            auditoriumBusy.put(slot, new HashSet<>());
        }
        
        // Sort lessons by complexity (e.g., number of groups/teachers) - Heuristic
        lessons.sort((l1, l2) -> Integer.compare(
            l2.getGroups().size() + l2.getTeachers().size(), 
            l1.getGroups().size() + l1.getTeachers().size()
        ));

        for (Lesson lesson : lessons) {
            // How many appointments needed? (e.g. 4 hours = 2 pairs)
            int appointmentsNeeded = (lesson.getDurationHours() + 1) / 2; 

            for (int i = 0; i < appointmentsNeeded; i++) {
                boolean scheduled = false;

                // Try to find a slot
                for (int slot = 0; slot < DAYS_IN_WEEK * SLOTS_PER_DAY; slot++) {
                    if (canSchedule(lesson, slot, teacherBusy, groupBusy)) {
                        
                        // Try to find an auditorium
                        Auditorium freeAuditorium = findFreeAuditorium(auditoriums, slot, auditoriumBusy, lesson);
                        
                        if (freeAuditorium != null) {
                            // Book it
                            saveAppointment(lesson, freeAuditorium, slot);
                            
                            // Mark resources as busy
                            markBusy(lesson, freeAuditorium, slot, teacherBusy, groupBusy, auditoriumBusy);
                            
                            scheduled = true;
                            break; // Move to next appointment of this lesson
                        }
                    }
                }
                
                if (!scheduled) {
                    System.out.println("CRITICAL: Could not schedule lesson " + lesson.getId());
                }
            }
        }
    }

    private boolean canSchedule(Lesson lesson, int slot, Map<Integer, Set<Long>> teacherBusy, Map<Integer, Set<Long>> groupBusy) {
        for (Teacher t : lesson.getTeachers()) {
            if (teacherBusy.get(slot).contains(t.getId())) return false;
        }
        for (Group g : lesson.getGroups()) {
            if (groupBusy.get(slot).contains(g.getId())) return false;
        }
        return true;
    }

    private Auditorium findFreeAuditorium(List<Auditorium> auditoriums, int slot, Map<Integer, Set<Long>> auditoriumBusy, Lesson lesson) {
        for (Auditorium a : auditoriums) {
            if (!auditoriumBusy.get(slot).contains(a.getId())) {
                // Check capacity (optional, simple check for now)
                // if (a.getCapacity() >= calculateSize(lesson)) ...
                return a;
            }
        }
        return null;
    }

    private void markBusy(Lesson lesson, Auditorium a, int slot, 
                          Map<Integer, Set<Long>> teacherBusy, 
                          Map<Integer, Set<Long>> groupBusy, 
                          Map<Integer, Set<Long>> auditoriumBusy) {
        for (Teacher t : lesson.getTeachers()) teacherBusy.get(slot).add(t.getId());
        for (Group g : lesson.getGroups()) groupBusy.get(slot).add(g.getId());
        auditoriumBusy.get(slot).add(a.getId());
    }

    private void saveAppointment(Lesson lesson, Auditorium auditorium, int slot) {
        Appointment appt = new Appointment();
        appt.setLesson(lesson);
        appt.setAuditorium(auditorium);
        appt.setDayOfWeek((slot / SLOTS_PER_DAY) + 1); // 1..5
        appt.setTimeSlot((slot % SLOTS_PER_DAY) + 1);  // 1..4
        appointmentRepository.save(appt);
    }
}
