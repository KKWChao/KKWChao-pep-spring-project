package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.AccountNotFoundException;
import com.example.exception.MessageLengthException;
import com.example.exception.MessageNotFoundException;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

@Service
public class MessageService {
    public final MessageRepository messageRepository;
    public final AccountRepository accountRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Get All Messages
     * 
     * @return List<Message>
     */
    public List<Message> getMessages() {
        return messageRepository.findAll();
    }

    /**
     * Get Message By Id
     * 
     * @param message_id
     * @return message
     * @throws MessageNotFoundException
     */
    public Message getMessageById(Integer message_id) throws MessageNotFoundException {
        Optional<Message> optionalMessage = messageRepository.findById(message_id);

        if (!optionalMessage.isPresent()) {
            throw new MessageNotFoundException();
        }

        return optionalMessage.get();
    }

    /**
     * Create New Message
     *      Need to add missing user exception
     * @param message
     * @return created message
     * @throws MessageLengthException
     * @throws AccountNotFoundException
     */
    public Message createMessage(Message message) throws MessageLengthException, AccountNotFoundException  {
        // check if user exists
        Optional<Account> checkAccount = accountRepository.findById(message.getPosted_by());

        // check if user exists
        //      Issue with foreign key reference?
        //      not throwing correct error
        if (!checkAccount.isPresent()) {
            throw new AccountNotFoundException();
        }

        // minimum length and maximum length
        if (message.getMessage_text().length() <= 0 || message.getMessage_text().length() >= 255) {
            throw new MessageLengthException();
        }
  
        return messageRepository.save(message);
    }

    /**
     * Update Message By Id
     * 
     * @param message_id
     * @param message
     * @return message
     * @throws MessageNotFoundException
     * @throws MessageLengthException
     */
    public void updateMessage(Integer message_id, Message message) throws MessageNotFoundException, MessageLengthException {
        Optional<Message> optionalMessage = messageRepository.findById(message_id);

        if (!optionalMessage.isPresent()) {
            throw new MessageNotFoundException();
        }

        // minimum length and maximum length
        if (message.getMessage_text().length() <= 0 || message.getMessage_text().length() >= 255) {
            throw new MessageLengthException();
        }

        messageRepository.save(message);
    }

    /**
     * Delete Message By Id
     * 
     * @param message_id
     * @throws MessageNotFoundException
     */
    public void deleteMessageById(Integer message_id) throws MessageNotFoundException {
        Optional<Message> optionalMessage = messageRepository.findById(message_id);
        if (optionalMessage.isPresent()) {
            messageRepository.deleteById(message_id);
        } else {
            throw new MessageNotFoundException();
        }
    }

    /**
     * Get All Messages By User Id
     * 
     * @param account_id
     * @return List<Messages>
     * @throws AccountNotFoundException
     */
    public List<Message> getMessagesByAccountId(Integer acount_id) throws AccountNotFoundException {
        Optional<Account> optionalAccount = accountRepository.findById(acount_id);
        if (!optionalAccount.isPresent()) {
            throw new AccountNotFoundException();
        }

        Optional<List<Message>> optionalMessages = messageRepository.getMessagesByAccount(acount_id);

        return optionalMessages.get();

        
    }
}
