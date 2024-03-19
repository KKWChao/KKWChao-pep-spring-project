package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.*;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {
  private final AccountService accountService;
  private final MessageService messageService;

  @Autowired
  public SocialMediaController(AccountService accountService, MessageService messageService) {
    this.accountService = accountService;
    this.messageService = messageService;
  }

  /**
   * Register Account
   * 
   * @RequestBody String username, String password
   * @Requirements account = unique, password length > 4
   * @return success -> account + status(200)
   * @return fail -> duplicate username = 409 | other reason = 400
   */
  @PostMapping("/register")
  public ResponseEntity<?> registerAccount(@RequestBody Account account) {
    try {
      Account registeredAccount = accountService.registerAccount(account);
      return ResponseEntity.status(200).body(registeredAccount);
    } catch (DuplicateUserException duplicateUserException) {
      return ResponseEntity.status(409).body("Registration Failed");
    } catch (MinimumPasswordLengthException minimumPasswordLengthException) {
      return ResponseEntity.status(401).body("Minimum Password Length Not Met");
    } catch (Exception exception) {
      return ResponseEntity.status(400).body("Internal Service Error");
    }
  }

  /**
   * Login Account
   * 
   * @RequestBody String username, String password
   * @Requirements account_username and account_password must match
   * @return success -> account + account_id + status(200)
   * @return fail -> 401
   */
  @PostMapping("/login")
  public ResponseEntity<?> loginAccount(@RequestBody Account account) {
    // login logic
    try {
      System.out.println(account);
      Account loggedInAccount = accountService.loginAccount(account.getUsername(), account.getPassword());
      return ResponseEntity.status(200).body(loggedInAccount);
    } catch (LoginErrorException loginErrorException) {
      return ResponseEntity.status(401).body("Login Error");
    } catch (Exception exception) {
      return ResponseEntity.status(400).body(null);
    }
    
  }

  /**
   * Create New Message
   * 
   * @RequestBody String message_text
   * @Requirements message is not blank, message.length < 256
   * @return success -> new message + message_id + status(200)
   * @return fail -> status(400)
   */
  @PostMapping("/messages")
  public ResponseEntity<?> createMessage(@RequestBody Message message) {
    try {
      Message createdMessage = messageService.createMessage(message);
      return ResponseEntity.status(200).body(createdMessage);
    } catch (MessageLengthException messageLengthException) {
      return ResponseEntity.status(400).body("Message does not comply with length parameters");
    } catch(AccountNotFoundException accountNotFoundException) {
      return ResponseEntity.status(400).body("Account Not Found");
    }
  }

  /**
   * Get All Message
   * 
   * @return List of all Messages + status(200)
   */
  @GetMapping("/messages")
  public @ResponseBody List<Message> getAllMessages() {
    
    return messageService.getMessages();
  }

  /**
   * Get One Message Given Message Id
   * 
   * @PathVariable Integer message_id
   * @return success -> message + status(200)
   * @return fail -> empty message + status(200)
   */
  @GetMapping("/messages/{message_id}")
  public @ResponseBody Message getMessageById(@PathVariable Integer message_id) {
    try {
      return messageService.getMessageById(message_id);
    } catch (MessageNotFoundException messageNotFoundException) {
      return null;
    }
    
  }

  /**
   * Delete a Message Given Message Id
   * 
   * @PathVariable Integer message Id
   * @return success -> # of rows updated(1) + status(200)
   * @return fail -> empty body + status(200)
   */
  @DeleteMapping("/messages/{message_id}")
  public @ResponseBody ResponseEntity<Integer> deleteMessageById(@PathVariable Integer message_id) {
    try {
      messageService.deleteMessageById(message_id);
      return ResponseEntity.status(200).body(1);
    } catch (MessageNotFoundException MessageNotFoundException) {
      return ResponseEntity.status(200).body(null);
    } catch (Exception exception) {
      return ResponseEntity.status(400).body(null);
    }
  }

  /**
   * Update Message Given Message Id (FIX NEED TO INCLUDE MESSAGE BODY)
   * 
   * @PathVariable Integer message Id
   * @RequestBody message
   * @Requirements message_id must exist, message_text not blank + message.length
   *               < 256
   * @return success -> # of rows updated(1)
   * @return fail -> status(400)
   */
  @PatchMapping("/messages/{message_id}")
  public ResponseEntity<Integer> updateMessageById(@PathVariable Integer message_id, @RequestBody Message message) {
    try {
      messageService.updateMessage(message_id, message);
      return ResponseEntity.status(200).body(1);
    } catch (MessageNotFoundException messageNotFoundException) {
      return ResponseEntity.status(400).body(null);
    } catch (MessageLengthException messageLengthException) {
      return ResponseEntity.status(400).body(null);
    } catch (Exception exception) {
      return ResponseEntity.status(400).body(null);
    }
  }

  /**
   * Get All Messages From User Given Account Id
   * 
   * @PathVariable Integer account_id
   * @return success -> List of all messages + status(200)
   * @return fail -> empty list + status(200)
   */
  @GetMapping("/accounts/{account_id}/messages")
  public ResponseEntity<List<Message>> getMessagesByAccount(@PathVariable Integer account_id) throws AccountNotFoundException {
    try {
      List<Message> accountMessages = messageService.getMessagesByAccountId(account_id);
      return ResponseEntity.status(200).body(accountMessages);
    } catch (AccountNotFoundException accountNotFoundException) {
      return ResponseEntity.status(400).body(null);
    } catch (Exception exception) {
      return ResponseEntity.status(400).body(null);
    }
  }

}
