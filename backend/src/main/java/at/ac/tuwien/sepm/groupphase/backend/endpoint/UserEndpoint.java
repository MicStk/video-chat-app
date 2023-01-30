package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserEndpoint(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Add a new user", security = @SecurityRequirement(name = "apiKey"))
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        LOGGER.trace("POST /api/v1/users body: {}", userDto);
        try {
            return userMapper.applicationUserToUserDto(userService.createUser(userMapper.userDtoToApplicationUser(userDto)));
        } catch (ValidationException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        } catch (ConflictException e) {
            LOGGER.error("Conflict error while updating meeting. " + e.getMessage(), e);
            HttpStatus status = HttpStatus.CONFLICT;
            throw new ResponseStatusException(status, e.getMessage(), e);
        }
    }

    @PermitAll
    @GetMapping(value = "/{email}")
    //@Operation(summary = "Get information about a User", security = @SecurityRequirement(name = "apiKey"))
    public UserDto getUserWithEmail(@PathVariable String email) {
        LOGGER.trace("GET /api/v1/users/{}", email);
        UserDto user = userMapper.applicationUserToUserDto(userService.findApplicationUserByEmail(email));
        user.setPassword("");
        return user;
    }

    @PermitAll
    @GetMapping(value = "/name/{name}")
    //@Operation(summary = "Get information about a User", security = @SecurityRequirement(name = "apiKey"))
    public List<UserDto> getUserWithName(@PathVariable String name) {
        LOGGER.trace("GET /api/v1/users/name/{}", name);
        List<UserDto> users = userMapper.applicationUserToUserDto(userService.findApplicationUserByName(name));
        users.forEach((userDto -> userDto.setPassword(null)));
        return users;
    }

}
