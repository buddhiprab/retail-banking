package com.buddhi.banking.services;

import com.buddhi.banking.models.Txn;
import com.buddhi.banking.models.User;
import com.buddhi.banking.repository.TxnRepository;
import com.buddhi.banking.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class BankingService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TxnRepository txnRepository;
    @Autowired
    PrintConsoleMessageService printConsoleMessageService;

    @Transactional(rollbackOn = RuntimeException.class)
    public void login(String name) {
        //Todo input parameter validations and throw validation exceptions
        User user = userRepository.findByName(name);
        Double balance = 0.0;
        if(user!=null){
            balance = getBalance(user.getId());
        } else {
            User u = new User();
            u.setName(name);
            user = userRepository.save(u);
        }
        printConsoleMessageService.loginGreeting(name);
        List<Txn> owingFromTxns = getOwingFromTxns(user.getId());
        if(owingFromTxns != null){
            owingFromTxns.forEach(owingFromTxn -> userRepository.findById(owingFromTxn.getUserId()).ifPresent(owingFromUser -> printConsoleMessageService.owingFromUser(owingFromUser.getName(), owingFromTxn.getAmount())));
        }
        printConsoleMessageService.balance(balance);
        Txn oweTxn = getOweTxn(user.getId());
        if(oweTxn != null) {
            userRepository.findById(oweTxn.getRefId()).ifPresent(oweToUser -> printConsoleMessageService.oweToUser(oweToUser.getName(), oweTxn.getAmount()));
        }
    }

    @Transactional(rollbackOn = RuntimeException.class)
    public void topUp(String name, String amountVal) {
        //Todo input parameter validations and throw validation exceptions
        Double amount = Double.valueOf(amountVal);
        User user = userRepository.findByName(name);
        //create credit txn
        createTxn(user.getId(), "C", amount, null, null);
        //check owe txn
        Txn oweTxn = getOweTxn(user.getId());
        if(oweTxn==null) {
            printConsoleMessageService.balance(getBalance(user.getId()));
        } else { // handle owe
            if(oweTxn.getAmount().compareTo(amount)>=0) { // owe >= amount
                Txn debitTxn = createTxn(user.getId(), "D", amount, null, null);
                setTxnId(debitTxn);
                createTxn(oweTxn.getRefId(), "C", amount, debitTxn.getId(), null);
                userRepository.findById(oweTxn.getRefId()).ifPresent(toUser -> printConsoleMessageService.transfer(toUser.getName(), amount));
            } else {
                Txn debitTxn = createTxn(user.getId(), "D", oweTxn.getAmount(), null, null);
                setTxnId(debitTxn);
                createTxn(oweTxn.getRefId(), "C", oweTxn.getAmount(), debitTxn.getId(), null);
                userRepository.findById(oweTxn.getRefId()).ifPresent(toUser -> printConsoleMessageService.transfer(toUser.getName(), oweTxn.getAmount()));
            }
            printConsoleMessageService.balance(getBalance(user.getId()));
            //settle owe txn
            oweTxn.setStatus("Y");
            txnRepository.save(oweTxn);
            if(oweTxn.getAmount().compareTo(amount)>0) { // owe > amount
                Double oweMoreAmount = oweTxn.getAmount()-amount;
                Txn oweTxnForDifferenceAmount = createTxn(user.getId(), "O", oweMoreAmount, null, oweTxn.getRefId());
                userRepository.findById(oweTxnForDifferenceAmount.getRefId()).ifPresent(oweMoreToUser -> printConsoleMessageService.oweToUser(oweMoreToUser.getName(), oweMoreAmount));
            }
        }
    }

    @Transactional(rollbackOn = RuntimeException.class)
    public void pay(String name, String amountVal, String toUserName) {
        //Todo input parameter validations and throw validation exceptions
        Double amount = Double.valueOf(amountVal);
        User user = userRepository.findByName(name);
        User toUser = userRepository.findByName(toUserName);
        Double balance = getBalance(user.getId());
        if(balance.compareTo(0.0)>0) { // balance > 0
            Txn owingFromTxn = getOwingFromTxn(toUser.getId(), user.getId());
            if(owingFromTxn==null){
                if(balance.compareTo(amount)>=0){
                    Txn debitTxn = createTxn(user.getId(), "D", amount, null, null);
                    setTxnId(debitTxn);
                    createTxn(toUser.getId(), "C", amount, debitTxn.getId(), null);
                    printConsoleMessageService.transfer(toUser.getName(), amount);
                    printConsoleMessageService.balance(getBalance(user.getId()));
                } else {
                    Txn debitTxn = createTxn(user.getId(), "D", balance, null, null);
                    setTxnId(debitTxn);
                    createTxn(toUser.getId(), "C", balance, debitTxn.getId(), null);
                    printConsoleMessageService.transfer(toUser.getName(), balance);
                    printConsoleMessageService.balance(getBalance(user.getId()));
                    Double oweAmount = amount-balance;
                    createTxn(user.getId(), "O", oweAmount, null, toUser.getId());
                    printConsoleMessageService.oweToUser(toUser.getName(), oweAmount);
                }
            } else {
                //settle owe txn
                owingFromTxn.setStatus("Y");
                txnRepository.save(owingFromTxn);
                if(owingFromTxn.getAmount().compareTo(amount)>0) {
                    Double oweMoreAmount = owingFromTxn.getAmount()-amount;
                    createTxn(toUser.getId(), "O", oweMoreAmount, null, user.getId());
                    printConsoleMessageService.owingFromUser(toUser.getName(), oweMoreAmount);
                } else {
                    createTxn(toUser.getId(), "C", amount-owingFromTxn.getAmount(), null, null);
                }
                printConsoleMessageService.balance(getBalance(user.getId()));
            }
        } else {
            printConsoleMessageService.balance(getBalance(user.getId()));
        }
    }

    private Double getBalance(Long userId) {
        Double balance = txnRepository.getBalance(userId);
        return balance==null ? 0.0 : balance;
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

    private Txn getOweTxn(Long userId) {
        return txnRepository.findByUserIdAndTypeAndStatus(userId, "O", null);
    }

    private List<Txn> getOwingFromTxns(Long refId) {
        return txnRepository.findByTypeAndRefIdAndStatus("O", refId, null);
    }

    private Txn getOwingFromTxn(Long userId, Long refId) {
        return txnRepository.findByUserIdAndTypeAndRefIdAndStatus(userId, "O", refId, null);
    }
}
