package com.elliehannant.checkoutsystem;

import com.elliehannant.checkoutsystem.services.CheckoutSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CheckoutSystemApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CheckoutSystemApplication.class);
    private final CheckoutSystemService checkoutSystemService;

    public CheckoutSystemApplication(CheckoutSystemService checkoutSystemService) {
        this.checkoutSystemService = checkoutSystemService;
    }

    public static void main(String[] args) {
        SpringApplication.run(CheckoutSystemApplication.class, args);
    }

    @Override
    public void run(String... args) {
        LOG.info("*** STARTING CHECKOUT SYSTEM ***");
        checkoutSystemService.runCheckoutSystem();
        LOG.info("*** CHECKOUT SYSTEM FINISHED ***");
    }
}
