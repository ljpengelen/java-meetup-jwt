package nl.kabisa.meetup.sessionbased.accounts.repository;

import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByUsername(String username);
}
