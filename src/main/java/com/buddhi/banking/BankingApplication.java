package com.buddhi.banking;

import com.buddhi.banking.services.BankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;

import java.util.Scanner;

@SpringBootApplication
public class BankingApplication implements CommandLineRunner {
	@Autowired
	BankingService bankingService;

	public static void main(String[] args) {
		SpringApplication.run(BankingApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Started Application ...");
		String loggedInUserName = "";
		while (true){
			try{
				Scanner in = new Scanner(System.in);
				String inputText = in.nextLine();
				String[] arr = inputText.split("\\s");
				String cmd = arr[0];
				if ("login".equals(cmd)) {
					String name = arr[1];
					bankingService.login(name);
					loggedInUserName = name;
				} else if ("topup".equals(cmd)) {
					String amount = arr[1];
					bankingService.topUp(loggedInUserName, amount);
				} else if ("pay".equals(cmd)) {
					String toUserName = arr[1];
					String amount = arr[2];
					bankingService.pay(loggedInUserName, amount, toUserName);
				} else {
					System.out.println("command must be either 'login', 'topup' or 'pay'");
				}
			} catch (Exception e) {
				System.out.println("error occurred! " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
