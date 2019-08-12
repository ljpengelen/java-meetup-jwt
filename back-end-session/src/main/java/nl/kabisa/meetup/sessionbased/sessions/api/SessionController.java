package nl.kabisa.meetup.sessionbased.sessions.api;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import nl.kabisa.meetup.sessionbased.accounts.repository.Account;
import nl.kabisa.meetup.sessionbased.accounts.repository.AccountRepository;

@RequestMapping("/session")
@RestController
public class SessionController {

    private StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();

    @Autowired
    AccountRepository accountRepository;

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody LoginResponse login(@RequestBody @Valid LoginRequest request, HttpSession session) {
        Account account = accountRepository.findByUsername(request.getUsername());
        if (account == null) {
            return new LoginResponse(LoginStatus.INVALID_CREDENTIALS);
        }

        if (!encryptor.checkPassword(request.getPassword(), account.getPassword())) {
            return new LoginResponse(LoginStatus.INVALID_CREDENTIALS);
        }

        session.setAttribute("accountId", account.getId());

        return new LoginResponse(LoginStatus.LOGGED_IN);
    }

    @GetMapping
    public StatusResponse getStatus(HttpSession session) {
        if (session.getAttribute("accountId") == null) {
            return new StatusResponse(SessionStatus.LOGGED_OUT);
        }

        return new StatusResponse(SessionStatus.LOGGED_IN);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public @ResponseStatus(HttpStatus.NO_CONTENT) void logout(HttpSession session) {
        session.removeAttribute("accountId");
    }
}
