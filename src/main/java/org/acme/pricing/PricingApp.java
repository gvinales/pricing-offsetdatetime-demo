package org.acme.pricing;

import lombok.extern.slf4j.Slf4j;
import org.acme.pricing.exception.PlatformExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Import(PlatformExceptionHandler.class)
@SpringBootApplication
@Slf4j
public class PricingApp implements ApplicationListener<ApplicationReadyEvent> {


    @Autowired
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(PricingApp.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            int port = applicationContext.getBean(Environment.class).getProperty("server.port", Integer.class, 8080);
            log.debug(String.format("%n%nApplication started on: http://%s:%d%n%n", ip, port));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


}
