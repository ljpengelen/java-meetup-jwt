package nl.kabisa.meetup.sessionbased.accounts.api;

import nl.kabisa.meetup.sessionbased.accounts.repository.Account;
import nl.kabisa.meetup.sessionbased.accounts.repository.AccountRepository;
import nl.kabisa.meetup.sessionbased.interceptors.authentication.RequireSession;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController {

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

    @RequireSession
    @RequestMapping()
    public SanitizedAccountDto getAccount(HttpSession session) {
        Account account = getAccountForLoggedInUser(session);
        return new SanitizedAccountDto(account.getId(), account.getUsername());
    }

    @RequireSession
    @RequestMapping(method = RequestMethod.PUT)
    public SanitizedAccountDto updateAccount(@RequestBody @Valid AccountDto request, HttpSession session) throws UsernameInUseException {
        Account existingAccount = accountRepository.findByUsername(request.getUsername());
        Account accountForLoggedInUser = getAccountForLoggedInUser(session);
        if (existingAccount != null && existingAccount.getId() != accountForLoggedInUser.getId()) {
            throw new UsernameInUseException(request.getUsername());
        }

        accountForLoggedInUser.setUsername(request.getUsername());
        accountForLoggedInUser.setPassword(encryptor.encryptPassword(request.getPassword()));

        Account updatedAccount = accountRepository.save(accountForLoggedInUser);

        return new SanitizedAccountDto(updatedAccount.getId(), updatedAccount.getUsername());
    }

    @RequireSession
    @RequestMapping(method = RequestMethod.DELETE)
    public @ResponseStatus(HttpStatus.NO_CONTENT) void deleteAccount(HttpSession session) {
        Account account = getAccountForLoggedInUser(session);
        accountRepository.delete(account);
    }

    private Account getAccountForLoggedInUser(HttpSession session) {
        Long accountId = (Long) session.getAttribute("accountId");
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
