package com.buddhi.banking.repository;

import com.buddhi.banking.models.Balance;
import com.buddhi.banking.models.Txn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Balance getBalanceByUserId(@Param("userId") Long userId);
}
