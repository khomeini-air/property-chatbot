package com.speedhome.chatbot.repository;

import com.speedhome.chatbot.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a FROM Appointment a WHERE a.dateTime BETWEEN :start AND :end AND a.confirmed = true AND a.reminderSent = false")
    List<Appointment> findUpcomingRemind(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}