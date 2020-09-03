package com.buddhi.banking.repository;

import com.buddhi.banking.models.Txn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TxnRepository extends JpaRepository<Txn, Long> {
    Txn findByUserIdAndTypeAndStatus(Long userId, String type, String status);

    List<Txn> findByTypeAndRefIdAndStatus(String type, Long refId, String status);

    Txn findByUserIdAndTypeAndRefIdAndStatus(Long userId, String type, Long refId, String status);

    @Query(value = "SELECT SUM(CASE WHEN t.type='D' THEN t.amount*-1 ELSE t.amount END) AS v FROM bank.txn t WHERE t.user_id = :userId AND t.type in ('D','C')", nativeQuery = true)
    Double getBalance(@Param("userId") Long userId);

}
