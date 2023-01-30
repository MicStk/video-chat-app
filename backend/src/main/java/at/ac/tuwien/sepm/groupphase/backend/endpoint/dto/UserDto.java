package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.Role;

/**
 * DTO to bundle the parameters used for a user dto.
 */
public class UserDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private Role role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public static final class UserDtoBuilder {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private Role role;

        private UserDtoBuilder() {
        }

        public static UserDto.UserDtoBuilder aUserDto() {
            return new UserDto.UserDtoBuilder();
        }

        public UserDto.UserDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public UserDto.UserDtoBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserDto.UserDtoBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserDto.UserDtoBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public UserDto.UserDtoBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserDto.UserDtoBuilder withRole(Role role) {
            this.role = role;
            return this;
        }

        public UserDto build() {
            UserDto userDto = new UserDto();
            userDto.setId(id);
            userDto.setFirstName(firstName);
            userDto.setLastName(lastName);
            userDto.setEmail(email);
            userDto.setPassword(password);
            userDto.setRole(role);
            return userDto;
        }
    }
}