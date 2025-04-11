package com.speedhome.chatbot.service;

import com.speedhome.chatbot.entity.Appointment;

public interface AppointmentService {

    /**
     * Processes and saves appointment from the pre-formatted aiResponse
     */
    Appointment processAppointment(String aiResponse);
}
