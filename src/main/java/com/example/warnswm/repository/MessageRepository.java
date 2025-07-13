package com.example.warnswm.repository;

import com.example.warnswm.entity.Message;
import com.example.warnswm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByUserOrderBySentAtDesc(User user);
    
    Page<Message> findByUserOrderBySentAtDesc(User user, Pageable pageable);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.user = :user")
    long countByUser(User user);
}
