package com.edoc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edoc.models.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByCandidatId(Long candidatId);
    List<Notification> findByCommissionId(Long commissionId);
    List<Notification> findBySujetId(Long sujetId);
}
