package com.ahmed.AhmedMohmoud.dao;

import com.ahmed.AhmedMohmoud.entities.Message;
import com.ahmed.AhmedMohmoud.entities.User;
import com.ahmed.AhmedMohmoud.helpers.SettingsDto;
import com.ahmed.AhmedMohmoud.helpers.TopThreeQueryHelper;
import com.ahmed.AhmedMohmoud.helpers.TopThreeResponseHelper;
import org.hibernate.query.NativeQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Query("select u from User u join fetch u.sentMessages where u.id=:id")
    User getSentMessages(Integer id);

    @Query("select u from User u join fetch u.receivedMessages where u.id=:id")
    User getReceivedMessages(Integer id);


    @Query("select u from User u where u.name like :name")
    List<User> findUserByName(String name);

    // using the Query with Constructor: Added NEW com.ahmed.AhmedMohmoud.helpers.TopThreeQueryHelper(...)
    // to the @Query. This tells JPA to instantiate TopThreeQueryHelper objects directly using its constructor.
    // Replace com.ahmed.AhmedMohmoud.helpers with your actual package name.
    @Query("""
            select new com.ahmed.AhmedMohmoud.helpers.TopThreeQueryHelper(
            count(m.receiver.id) , m.receiver.id , u.name , u.picUrl , u.bio)
            from Message m
            join m.receiver u
            group by m.receiver.id
            order by count(m.receiver.id) desc
            limit 3
            """)
    List<TopThreeQueryHelper> getTopThreeReceivers();

    @Query("select u from User u where u.email=:email")
    User findByUserEmail(String email);
}
