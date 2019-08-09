package nl.kabisa.meetup.jwtbased.accounts.api;

import org.hibernate.validator.constraints.NotBlank;

public class SanitizedAccountDto {

    private Long id;

    @NotBlank
    private String username;

    protected SanitizedAccountDto() {}

    public SanitizedAccountDto(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    protected void setUsername(String username) {
        this.username = username;
    }
}
