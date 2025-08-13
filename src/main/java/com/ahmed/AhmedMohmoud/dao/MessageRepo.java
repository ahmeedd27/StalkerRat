package com.ahmed.AhmedMohmoud.dao;

import com.ahmed.AhmedMohmoud.entities.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepo extends JpaRepository<Message , Integer> {


    @Query("select m from Message m where m.sender.id=:id")
    Page<Message> getPaginatedSentMessages(Pageable p, Integer id);
    @Query("select m from Message m where m.receiver.id=:id")
    Page<Message> getPaginatedReceivedMessages(Pageable p, Integer id);

    @Modifying
    @Transactional
    @Query("delete from Message m where m.id=:msgId and m.receiver.id=:userId")
    int deleteSpecificMessageReceivedToSpecificUser(Integer msgId, Integer userId);


    @Query("select m from Message m where m.id=:msgId and m.receiver.id=:userId")
    Optional<Message> makeMessageFavorite(Integer msgId, Integer userId);
}
