package nl.kabisa.meetup.sessionbased.accounts.api;

import org.hibernate.validator.constraints.NotBlank;

public class AccountDto {

    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    protected AccountDto() {}

    public AccountDto(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public AccountDto(String username, String password) {
        this(null, username, password);
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

    public String getPassword() {
        return password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }
}
