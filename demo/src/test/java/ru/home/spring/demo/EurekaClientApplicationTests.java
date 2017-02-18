package ru.home.spring.demo;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * Created by dzaets on 02.02.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoClientApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EurekaClientApplicationTests {

    static ConfigurableApplicationContext eurekaServer;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeClass
    public static void startEureka() {
        eurekaServer = SpringApplication.run(EurekaServer.class,
                "--server.port=8761",
                "--eureka.instance.leaseRenewalIntervalInSeconds=1");
    }

    @AfterClass
    public static void closeEureka() {
        eurekaServer.close();
    }

    @Test
    public void shouldRegisterClientInEurekaServer() throws InterruptedException {
        // registration has to take place...
        Thread.sleep(3000);

        ResponseEntity<String> response = this.testRestTemplate.getForEntity("http://localhost:" + this.port + "/service-instances/demo%20app", String.class);

        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).contains("demo%20app");
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableEurekaServer
    static class EurekaServer {
    }
}