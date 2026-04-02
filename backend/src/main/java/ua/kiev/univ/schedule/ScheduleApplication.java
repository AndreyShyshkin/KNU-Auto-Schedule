package ua.kiev.univ.schedule;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ua.kiev.univ.schedule.service.core.DataInitializationService;

import java.util.TimeZone;

@SpringBootApplication
public class ScheduleApplication implements CommandLineRunner {

    private final DataInitializationService dataInitializationService;

    public ScheduleApplication(DataInitializationService dataInitializationService) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kyiv"));
        this.dataInitializationService = dataInitializationService;
    }

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kyiv"));
        SpringApplication.run(ScheduleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        dataInitializationService.initializeData();
    }
}