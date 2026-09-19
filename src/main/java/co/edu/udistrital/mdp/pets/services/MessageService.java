package co.edu.udistrital.mdp.pets.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import co.edu.udistrital.mdp.pets.repositories.MessageRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class MessageService {
    final MessageRepository messageRepository;
    final AdopterRepository adopterRepository;

    @Transactional(rollbackFor = {IllegalOperationException.class})
    public MessageEntity createMessage(Long adopterId, MessageEntity messageEntity) throws IllegalOperationException {
        log.info("Starting the process of creating a message");
        Optional<AdopterEntity> adopterOptional = adopterRepository.findById(adopterId);
        if (adopterOptional.isEmpty())
            throw new IllegalOperationException(ErrorMessage.ADOPTER_NOT_VALID);

        if (!validateMessage(messageEntity))
            throw new IllegalOperationException(ErrorMessage.MESSAGE_NOT_VALID);

        if (messageEntity.getSentDate() != null && messageEntity.getSentDate().after(new Date()))
            throw new IllegalOperationException(ErrorMessage.MESSAGE_DATE_INVALID);

        messageEntity.setAdopter(adopterOptional.get());
        log.info("Finished process to create a message");
        return messageRepository.save(messageEntity);
    }

    @Transactional
    public List<MessageEntity> getMessages(Long adopterId) throws EntityNotFoundException {
        log.info("Starting process to fetch all messages for adopter with id = ", adopterId);
        Optional<AdopterEntity> adopterOptional = adopterRepository.findById(adopterId);
        if (adopterOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);

        return messageRepository.findByAdopterId(adopterId);
    }

    @Transactional(rollbackFor = {EntityNotFoundException.class, IllegalOperationException.class})
    public MessageEntity getMessage(Long adopterId, Long messageId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to fetch message with id = ", messageId);
        Optional<AdopterEntity> adopterOptional = adopterRepository.findById(adopterId);
        if (adopterOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);

        Optional<MessageEntity> messageOptional = messageRepository.findById(messageId);
        if (messageOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.MESSAGE_NOT_FOUND);

        if (messageOptional.get().getAdopter() == null
                || !messageOptional.get().getAdopter().getId().equals(adopterId))
            throw new IllegalOperationException(ErrorMessage.MESSAGE_NOT_ASSOCIATED_TO_ADOPTER);

        log.info("Finished process to fetch message with id = ", messageId);
        return messageOptional.get();
    }

    @Transactional(rollbackFor = {EntityNotFoundException.class, IllegalOperationException.class})
    public MessageEntity updateMessage(Long adopterId, Long messageId, MessageEntity message) throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to update message with id = ", messageId);
        getMessage(adopterId, messageId);

        throw new IllegalOperationException(ErrorMessage.MESSAGE_NOT_VALID);
    }

    @Transactional(rollbackFor = {EntityNotFoundException.class, IllegalOperationException.class})
    public void deleteMessage(Long adopterId, Long messageId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to delete message with id = ", messageId);
        getMessage(adopterId, messageId);

        messageRepository.deleteById(messageId);
        log.info("Finished process to delete message with id = ", messageId);
    }

    private boolean validateMessage(MessageEntity message) {
        return message.getContent() != null && !message.getContent().isEmpty();
    }

}