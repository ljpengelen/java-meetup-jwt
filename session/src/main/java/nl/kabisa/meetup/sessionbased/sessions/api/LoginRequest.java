package nl.kabisa.meetup.sessionbased.sessions.api;

import org.hibernate.validator.constraints.NotBlank;

public class LoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    protected LoginRequest() {}

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    protected void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    protected void setPassword(String password) {
        this.password = password;
    }
}
