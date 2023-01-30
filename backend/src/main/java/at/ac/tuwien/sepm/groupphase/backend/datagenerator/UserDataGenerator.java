package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
public class UserDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserDataGenerator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Bean
    private void generateUsers() {
        LOGGER.trace("generating users");
        if (userRepository.findApplicationUserByEmail("admin@email.com") == null) {
            ApplicationUser admin = new ApplicationUser("Max", "Mustermann", "admin@email.com", "password", Role.ADMIN);
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            LOGGER.debug("saving user {}", admin);
            userRepository.save(admin);
        }
        if (userRepository.findApplicationUserByEmail("michael@email.com") == null) {
            ApplicationUser user = new ApplicationUser("Michael", "S", "michael@email.com", "password", Role.EMPLOYEE);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            LOGGER.debug("saving user {}", user);
            userRepository.save(user);
        }
        if (userRepository.findApplicationUserByEmail("florian@email.com") == null) {
            ApplicationUser user = new ApplicationUser("Florian", "K", "florian@email.com", "password", Role.EMPLOYEE);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            LOGGER.debug("saving user {}", user);
            userRepository.save(user);
        }
        if (userRepository.findApplicationUserByEmail("david@email.com") == null) {
            ApplicationUser user = new ApplicationUser("David", "B", "david@email.com", "password", Role.EMPLOYEE);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            LOGGER.debug("saving user {}", user);
            userRepository.save(user);
        }
        if (userRepository.findApplicationUserByEmail("nico@email.com") == null) {
            ApplicationUser user = new ApplicationUser("Nicolas", "S", "nico@email.com", "password", Role.EMPLOYEE);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            LOGGER.debug("saving user {}", user);
            userRepository.save(user);
        }
        if (userRepository.findApplicationUserByEmail("constantin@email.com") == null) {
            ApplicationUser user = new ApplicationUser("Constantin", "H", "constantin@email.com", "password", Role.EMPLOYEE);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            LOGGER.debug("saving user {}", user);
            userRepository.save(user);
        }
        if (userRepository.findApplicationUserByEmail("jakob@email.com") == null) {
            ApplicationUser user = new ApplicationUser("Jakob", "G", "jakob@email.com", "password", Role.EMPLOYEE);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            LOGGER.debug("saving user {}", user);
            userRepository.save(user);
        }
        if (userRepository.findApplicationUserByEmail("alexander@email.com") == null) {
            ApplicationUser user = new ApplicationUser("Alexander", "F", "alexander@email.com", "password", Role.EMPLOYEE);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            LOGGER.debug("saving user {}", user);
            userRepository.save(user);
        }
        if (userRepository.findApplicationUserByEmail("peter@email.com") == null) {
            ApplicationUser user = new ApplicationUser("Peter", "F", "peter@email.com", "password", Role.EMPLOYEE);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            LOGGER.debug("saving user {}", user);
            userRepository.save(user);
        }
    }
}
