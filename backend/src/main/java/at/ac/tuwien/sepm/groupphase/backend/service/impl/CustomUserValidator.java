package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class CustomUserValidator {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UserRepository userRepository;

    @Autowired
    public CustomUserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateForCreate(ApplicationUser user) throws ValidationException, ConflictException {
        LOG.debug("Validate new ApplicationUser {}", user);
        List<String> validationErrors = new ArrayList<>();
        List<String> conflictErrors = new ArrayList<>();
        if (user == null) {
            validationErrors.add("given User is null");
        } else {
            ApplicationUser retrievedUser = userRepository.findApplicationUserByEmail(user.getEmail());
            if (retrievedUser != null) {
                conflictErrors.add("User email already exists. Contact admin if you need to reset your password.");
            }
            // Validate firstName
            if (user.getFirstName().trim().isEmpty()) {
                validationErrors.add("firstName of given user is empty");
            }
            // Validate lastName
            if (user.getLastName().trim().isEmpty()) {
                validationErrors.add("lastName of given user is empty");
            }
            // Validate email
            if (user.getEmail().trim().isEmpty()) {
                validationErrors.add("Email of given user is empty");
            }
            String regexEmail = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"; // source: https://emailregex.com/
            Pattern pattern = Pattern.compile(regexEmail);
            if (!pattern.matcher(user.getEmail()).matches()) {
                validationErrors.add("Email is set but not well formatted");
            }
            // Validate password
            if (user.getPassword().trim().isEmpty()) {
                validationErrors.add("Password of given user is empty");
            }
            if (user.getPassword().trim().length() < 8) {
                validationErrors.add("Password must be at least 8 characters long");
            }

            if (!validationErrors.isEmpty()) {
                throw new ValidationException("Validation of user for creating a new user failed", validationErrors);
            }
            if (!conflictErrors.isEmpty()) {
                throw new ConflictException("Validation of meeting for updating failed – conflict with system state.", conflictErrors);
            }
        }
    }
}
