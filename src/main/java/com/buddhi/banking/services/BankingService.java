package com.buddhi.banking.services;

import com.buddhi.banking.models.Txn;
import com.buddhi.banking.models.User;
import com.buddhi.banking.repository.TxnRepository;
import com.buddhi.banking.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class BankingService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TxnRepository txnRepository;
    @Autowired
    PrintConsoleMessageService printConsoleMessageService;

    public void login(String name) {
        User user = userRepository.findByName(name);
        Double balance = 0.0;
        if(user!=null){
            balance = getBalance(user.getId());
        } else {
            User u = new User();
            u.setName(name);
            userRepository.save(u);
        }
        printConsoleMessageService.loginGreeting(name);
        printConsoleMessageService.balance(balance);
    }

    public void topUp(String name, String amount) {
        User user = userRepository.findByName(name);
        credit(user, new Double(amount));
        printConsoleMessageService.balance(getBalance(user.getId()));
    }

    public void pay(String name, String amountVal, String toUserName) {
        Double amount = Double.valueOf(amountVal);
        User user = userRepository.findByName(name);
        User toUser = userRepository.findByName(toUserName);
        Double balance = getBalance(user.getId());
        if(balance.compareTo(0.0)>0) { // balance > 0
            if(balance.compareTo(amount)>=0) {
                createTxn(user.getId(),"D", amount,null,null);
                credit(toUser, amount);
            } else {
                createTxn(user.getId(), "D", balance, null, null);
                credit(toUser, balance);
                createTxn(user.getId(), "O", amount-balance, null, toUser.getId());
            }
            printConsoleMessageService.pay(amountVal, toUserName);
        }
        printConsoleMessageService.balance(getBalance(user.getId()));
        Txn oweTxn = getOweTxn(user);
        if(oweTxn != null) {
            userRepository.findById(oweTxn.getRefId()).ifPresent(oweToUser -> printConsoleMessageService.oweToUser(oweToUser.getName(), String.valueOf(oweTxn.getAmount())));
        }
    }

    private Double getBalance(Long userId) {
        Double balance = txnRepository.getBalance(userId);
        return balance==null ? 0.0 : balance;
    }

    private void credit(User user, Double amount) {
        //check owe txn
        Txn oweTxn = txnRepository.findByUserIdAndTypeAndStatus(user.getId(), "O", null);
        if(oweTxn==null) { //create credit txn
            createTxn(user.getId(), "C", amount, null, null);
        } else { // handle owe
            if(oweTxn.getAmount().compareTo(amount)>=0) { // owe >= amount
                createTxn(user.getId(), "C", amount, null, null);
                Txn debitTxn = createTxn(user.getId(), "D", amount, null, null);
                setTxnId(debitTxn);
                createTxn(oweTxn.getRefId(), "C", amount, debitTxn.getId(), null);
                userRepository.findById(oweTxn.getRefId()).ifPresent(oweToUser -> printConsoleMessageService.oweTransfer(oweToUser.getName(), amount));
            } else {
                createTxn(user.getId(), "C", amount, null, null);
                Txn debitTxn = createTxn(user.getId(), "D", oweTxn.getAmount(), null, null);
                setTxnId(debitTxn);
                createTxn(oweTxn.getRefId(), "C", oweTxn.getAmount(), debitTxn.getId(), null);
            }
            //settle owe txn
            oweTxn.setStatus("Y");
            txnRepository.save(oweTxn);
            if(oweTxn.getAmount().compareTo(amount)>0) { // owe > amount
                Double oweMoreAmount = oweTxn.getAmount()-amount;
                Txn oweTxnForDifferenceAmount = createTxn(user.getId(), "O", oweMoreAmount, null, oweTxn.getRefId());
                userRepository.findById(oweTxnForDifferenceAmount.getRefId()).ifPresent(oweMoreToUser -> printConsoleMessageService.oweMoreToUser(oweMoreToUser.getName(), oweMoreAmount));
            }
        }
    }

    private Txn createTxn(Long userId, String type, Double amount, Long txnId, Long refId) {
        Txn v = new Txn();
        v.setUserId(userId);
        v.setType(type);
        v.setAmount(amount);
        if(txnId != null){
            v.setTxnId(txnId);
        }
        if(refId != null){
            v.setRefId(refId);
        }
        return txnRepository.save(v);
    }

    private void setTxnId(Txn txn) {
        txn.setTxnId(txn.getId());
        txnRepository.save(txn);
    }

    private Txn getOweTxn(User user) {
        return txnRepository.findByUserIdAndTypeAndStatus(user.getId(), "O", null);
    }
}
