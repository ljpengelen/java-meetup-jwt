package nl.kabisa.meetup.sessionbased.sessions.api;

public class StatusResponse {
    private SessionStatus status;

    protected StatusResponse() {
    }

    public StatusResponse(SessionStatus status) {
        this.status = status;
    }

    public SessionStatus getStatus() {
        return status;
    }

    protected void setStatus(SessionStatus status) {
        this.status = status;
    }
}
