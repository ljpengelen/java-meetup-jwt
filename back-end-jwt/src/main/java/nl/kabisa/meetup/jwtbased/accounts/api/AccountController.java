package nl.kabisa.meetup.jwtbased.accounts.api;

import static nl.kabisa.meetup.jwtbased.TokenNames.REFRESH_TOKEN_NAME;

import javax.validation.Valid;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Jwts;
import nl.kabisa.meetup.jwtbased.accounts.repository.Account;
import nl.kabisa.meetup.jwtbased.accounts.repository.AccountRepository;
import nl.kabisa.meetup.jwtbased.interceptors.authentication.RequireValidToken;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Value("${jwt.secret_key}")
    private String encodedKey;

    private StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();

    @Autowired
    AccountRepository accountRepository;

    @RequestMapping(method = RequestMethod.POST)
    public SanitizedAccountDto createAccount(@RequestBody @Valid AccountDto request) throws UsernameInUseException {
        Account existingAccount = accountRepository.findByUsername(request.getUsername());
        if (existingAccount != null) {
            throw new UsernameInUseException(request.getUsername());
        }

        Account requestedAccount = new Account(request.getUsername(), encryptor.encryptPassword(request.getPassword()));
        Account createdAccount = accountRepository.save(requestedAccount);

        return new SanitizedAccountDto(createdAccount.getId(), createdAccount.getUsername());
    }

    @RequireValidToken
    @RequestMapping()
    public SanitizedAccountDto getAccount(@CookieValue(value = REFRESH_TOKEN_NAME, defaultValue = "") String jwt) {
        Account account = getAccountForLoggedInUser(jwt);
        return new SanitizedAccountDto(account.getId(), account.getUsername());
    }

    @RequireValidToken
    @RequestMapping(method = RequestMethod.PUT)
    public SanitizedAccountDto updateAccount(
            @RequestBody @Valid AccountDto request, @CookieValue(value = REFRESH_TOKEN_NAME, defaultValue = "") String jwt) throws UsernameInUseException {
        Account existingAccount = accountRepository.findByUsername(request.getUsername());
        Account accountForLoggedInUser = getAccountForLoggedInUser(jwt);
        if (existingAccount != null && existingAccount.getId() != accountForLoggedInUser.getId()) {
            throw new UsernameInUseException(request.getUsername());
        }

        accountForLoggedInUser.setUsername(request.getUsername());
        accountForLoggedInUser.setPassword(encryptor.encryptPassword(request.getPassword()));

        Account updatedAccount = accountRepository.save(accountForLoggedInUser);

        return new SanitizedAccountDto(updatedAccount.getId(), updatedAccount.getUsername());
    }

    @RequireValidToken
    @RequestMapping(method = RequestMethod.DELETE)
    public @ResponseStatus(HttpStatus.NO_CONTENT) void deleteAccount(@CookieValue(value = REFRESH_TOKEN_NAME, defaultValue = "") String jwt) {
        Account account = getAccountForLoggedInUser(jwt);
        accountRepository.delete(account);
    }

    private Account getAccountForLoggedInUser(String jwt) {
        Long accountId = Long.parseLong(Jwts.parser().setSigningKey(encodedKey).parseClaimsJws(jwt).getBody().getSubject());
        if (accountId == null) {
            throw new RuntimeException("Unable to find account identifier of logged in user");
        }

        Account account = accountRepository.findOne(accountId);
        if (account == null) {
            throw new RuntimeException("Unable to find account of logged in user");
        }

        return account;
    }
}
