package test.ufanet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "test.ufanet",
        "test.ufanet.common.exception",
        "test.ufanet.common.util"
})
public class SwimPoolApplication {
    public static void main(String[] args) {
        SpringApplication.run(SwimPoolApplication.class, args);
    }
}