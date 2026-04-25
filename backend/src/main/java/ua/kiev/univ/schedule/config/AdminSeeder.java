package ua.kiev.univ.schedule.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.kiev.univ.schedule.model.user.Role;
import ua.kiev.univ.schedule.model.user.User;
import ua.kiev.univ.schedule.repository.UserRepository;

@Configuration
public class AdminSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner seedAdmin() {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .build();
                userRepository.save(admin);
                System.out.println("Default admin user created: admin / admin123");
            }
        };
    }
}
