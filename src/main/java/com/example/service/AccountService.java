package com.example.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.exception.AccountNotFoundException;
import com.example.exception.DuplicateUserException;
import com.example.exception.LoginErrorException;
import com.example.exception.MinimumPasswordLengthException;
import com.example.repository.AccountRepository;

@Service
public class AccountService {
  public final AccountRepository accountRepository;

  @Autowired
  public AccountService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  /**
   * Get Account By Id
   * 
   * @param account_id
   * @return account
   * @throws AccountNotFoundExcpetion
   */
  public Account getAccountById(Integer account_id) throws AccountNotFoundException {
    Optional<Account> optionalAccount = accountRepository.findById(account_id);

    if (optionalAccount.isPresent()) {
      return optionalAccount.get();
    } else {
      throw new AccountNotFoundException();
    }
  }

  /**
   * Registration Service
   * fail on duplicate account
   * fail on password length < 4 char
   * 
   * @param account
   * @return succes -> account
   * @throws DuplicateUserException
   */
  public Account registerAccount(Account account) throws DuplicateUserException, MinimumPasswordLengthException {
    Optional<Account> optionalAccount = accountRepository.getAccountByUsername(account.getUsername());
    // need to check if account exists and password length is greater than 4
    if (optionalAccount.isPresent() || account.getPassword().length() < 4) {
      throw new DuplicateUserException();
    } 

    if (account.getPassword().length() < 4) {
      throw new MinimumPasswordLengthException();
    }

    return accountRepository.save(account);
  }

  /**
   * Login Service
   * 
   * @param username
   * @param password
   * @return success -> account
   * @throws LoginErrorException
   */
  public Account loginAccount(String username, String password) throws LoginErrorException {
    // get account 
    Optional<Account> optionalAccount = accountRepository.getAccountByUsername(username);

    // compare accounts
    if (optionalAccount.isPresent() && password.equals(optionalAccount.get().getPassword())) {
      return optionalAccount.get();
    } else {
      throw new LoginErrorException();
    }
  }
}

