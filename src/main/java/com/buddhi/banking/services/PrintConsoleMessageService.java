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

    public void transfer(String name, Double amount) {
        System.out.println("Transferred " + amount + " to " + name+ ".");
    }

    public void oweToUser(String name, Double amount) {
        System.out.println("Owing " + amount + " to " + name + ".");
    }

    public void owingFromUser(String name, Double amount) {
        System.out.println("Owing " + amount + " from " + name + ".");
    }
}
