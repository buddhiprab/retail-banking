package com.buddhi.banking.services;

import org.springframework.stereotype.Service;

@Service
public class PrintConsoleMessageService {
    public void loginGreeting(String name) {
        System.out.println("Hello, " + name + "!");
    }

    public void balance(Double balance) {
        System.out.println("Your balance is " + balance + ".");
    }

    public void pay(String name, String amount) {
        System.out.println("Transferred " + amount + " to " + name + ".");
    }

    public void oweToUser(String oweToUserName, String amount) {
        System.out.println("Owing " + amount + " to " + oweToUserName + ".");
    }

    public void oweTransfer(String toUserName, Double amount) {
        System.out.println("Transferred " + amount + " to " + toUserName + ".");
    }

    public void oweMoreToUser(String toUsername, Double amount) {
        System.out.println("Owing " + amount + " to " + toUsername + ".");
    }
}
