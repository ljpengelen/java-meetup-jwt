package nl.kabisa.meetup.sessionbased.sessions.api;

public class LoginResponse {
    private LoginStatus status;

    protected LoginResponse() {}

    public LoginResponse(LoginStatus status) {
        this.status = status;
    }

    public LoginStatus getStatus() {
        return status;
    }
    protected void setStatus(LoginStatus status) {
        this.status = status;
    }
}
