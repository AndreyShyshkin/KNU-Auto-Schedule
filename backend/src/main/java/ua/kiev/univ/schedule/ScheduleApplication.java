package ua.kiev.univ.schedule;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import ua.kiev.univ.schedule.view.Frame;

import javax.swing.SwingUtilities;

@SpringBootApplication
public class ScheduleApplication implements CommandLineRunner {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ScheduleApplication.class)
                .headless(false)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            Frame frame = new Frame();
            frame.setVisible(true);
        });
    }
}