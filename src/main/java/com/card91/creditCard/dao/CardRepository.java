package com.card91.creditCard.dao;

import com.card91.creditCard.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {

    List<Card> findAllByRefId(String refId);

    List<Card> findAllByPhoneNum(String phoneNum);

    List<Card> findAllByAdhaarNum(String adhaarNum);

    @Modifying
    @Query("UPDATE Card c SET c.is_printed = :b Where c.refId = :refId")
    void updateIsPrinted(@Param("refId") String refId,
                         @Param("b") boolean b);
}
