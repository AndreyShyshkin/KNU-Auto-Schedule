package ua.kiev.univ.schedule;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import ua.kiev.univ.schedule.service.core.DataInitializationService;
import ua.kiev.univ.schedule.service.core.DatabaseService;

import java.util.TimeZone;

@SpringBootApplication
public class ScheduleApplication implements CommandLineRunner {

    private final DataInitializationService dataInitializationService;
    private final Environment environment;

    public ScheduleApplication(DataInitializationService dataInitializationService, Environment environment) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kyiv"));
        this.dataInitializationService = dataInitializationService;
        this.environment = environment;
    }

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kyiv"));
        SpringApplication.run(ScheduleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String dbUrl = environment.getProperty("spring.datasource.url");
        String dbUser = environment.getProperty("spring.datasource.username");
        String dbPass = environment.getProperty("spring.datasource.password");

        // Set credentials for legacy JDBC code
        DatabaseService.setCredentials(dbUrl, dbUser, dbPass);
        
        dataInitializationService.initializeData();
    }
}
