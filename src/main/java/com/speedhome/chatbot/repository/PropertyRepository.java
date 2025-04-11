package com.speedhome.chatbot.repository;

import com.speedhome.chatbot.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByUserId(Long userId);
}