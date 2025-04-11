package com.speedhome.chatbot.scheduler;

import com.speedhome.chatbot.entity.Appointment;
import com.speedhome.chatbot.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final AppointmentRepository appointmentRepository;

    @Scheduled(fixedRate = 60000) // Runs every minute
    public void sendAppointmentReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plus(1, ChronoUnit.HOURS);

        List<Appointment> upcomingAppointments = appointmentRepository
                .findUpcomingRemind(now, oneHourLater);

        upcomingAppointments.stream()
                .filter(appointment -> !appointment.isReminderSent())
                .forEach(this::sendReminder);
    }

    private void sendReminder(Appointment appointment) {
        // Sent the reminder event to Message Queueu
        log.info("REMINDER EVENT. appointmentTime: {}, address: {}. landlordName: {}, landlordPhone: {}, tenantName: {}, tenantPhone: {}",
                appointment.getDateTime(), appointment.getProperty().getAddress(),
                appointment.getLandlord().getUsername(), appointment.getLandlord().getMobile(),
                appointment.getTenant().getUsername(), appointment.getTenant().getMobile());

        // Update reminder status and save it.
        appointment.setReminderSent(true);
        appointmentRepository.save(appointment);
    }
}