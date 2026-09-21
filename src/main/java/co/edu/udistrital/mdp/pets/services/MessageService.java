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

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public MessageEntity createMessage(Long adopterId, MessageEntity messageEntity)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to create message for adopter with id = {0}", adopterId);
		Optional<AdopterEntity> adopterOptional = adopterRepository.findById(adopterId);
		if (adopterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);

		if (!validateMessage(messageEntity))
			throw new IllegalOperationException(ErrorMessage.MESSAGE_NOT_VALID);

		messageEntity.setAdopter(adopterOptional.get());
		log.info("Finished process to create message for adopter with id = {0}", adopterId);
		return messageRepository.save(messageEntity);
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class })
	public List<MessageEntity> getMessages(Long adopterId) throws EntityNotFoundException {
		log.info("Starting process to fetch messages of adopter with id = {0}", adopterId);
		Optional<AdopterEntity> adopterOptional = adopterRepository.findById(adopterId);
		if (adopterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);

		log.info("Finished process to fetch messages of adopter with id = {0}", adopterId);
		return adopterOptional.get().getMessages();
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public MessageEntity getMessage(Long adopterId, Long messageId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to fetch message with id = {0} of adopter with id = " + adopterId, messageId);
		Optional<AdopterEntity> adopterOptional = adopterRepository.findById(adopterId);
		if (adopterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);

		Optional<MessageEntity> messageOptional = messageRepository.findById(messageId);
		if (messageOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.MESSAGE_NOT_FOUND);

		MessageEntity messageEntity = messageOptional.get();
		if (messageEntity.getAdopter() == null || !messageEntity.getAdopter().getId().equals(adopterId))
			throw new IllegalOperationException(ErrorMessage.MESSAGE_NOT_ASSOCIATED_TO_ADOPTER);

		log.info("Finished process to fetch message with id = {0} of adopter with id = " + adopterId, messageId);
		return messageEntity;
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public MessageEntity updateMessage(Long adopterId, Long messageId, MessageEntity message)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to update message with id = {0} of adopter with id = " + adopterId, messageId);
		MessageEntity existingMessage = getMessage(adopterId, messageId);

		if (!validateMessage(message))
			throw new IllegalOperationException(ErrorMessage.MESSAGE_NOT_VALID);

		message.setId(existingMessage.getId());
		message.setAdopter(existingMessage.getAdopter());
		log.info("Finished process to update message with id = {0} of adopter with id = " + adopterId, messageId);
		return messageRepository.save(message);
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public void deleteMessage(Long adopterId, Long messageId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to delete message with id = {0} of adopter with id = " + adopterId, messageId);
		MessageEntity messageEntity = getMessage(adopterId, messageId);
		messageRepository.deleteById(messageEntity.getId());
		log.info("Finished process to delete message with id = {0} of adopter with id = " + adopterId, messageId);
	}

	private boolean validateMessage(MessageEntity message) {
		if (message.getContent() == null || message.getContent().isEmpty())
			return false;
		return message.getSentDate() != null && !message.getSentDate().after(new Date());
	}

}