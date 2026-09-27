package co.edu.udistrital.mdp.pets.services;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.NotificationRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor 
@Slf4j
@Service 
public class NotificationService {

    private static final List<String> VALID_CHANNELS = Arrays.asList("EMAIL", "SMS", "PUSH");

    final NotificationRepository notificationRepository;
    final ShelterRepository shelterRepository;

    @Transactional(rollbackFor = {IllegalOperationException.class})
    public NotificationEntity createNotification(Long shelterId, NotificationEntity notificationEntity) throws IllegalOperationException{
        log.info("Starting the process of creating a notification for shelter with id = ", shelterId);

        Optional<ShelterEntity> shelterOptional = shelterRepository.findById(shelterId);
        if (shelterOptional.isEmpty())
            throw new IllegalOperationException(ErrorMessage.SHELTER_NOT_VALID);

        if (!validateNotification(notificationEntity))
            throw new IllegalOperationException(ErrorMessage.NOTIFICATION_NOT_VALID);

        if (notificationEntity.getChannel() == null || !VALID_CHANNELS.contains(notificationEntity.getChannel()))
            throw new IllegalOperationException(ErrorMessage.NOTIFICATION_CHANNEL_INVALID);

        notificationEntity.setShelter(shelterOptional.get());
        log.info("Finished process to create a notification for shelter with id = ", shelterId);
        return notificationRepository.save(notificationEntity);
    
    }

    @Transactional(rollbackFor = {EntityNotFoundException.class})
    public List<NotificationEntity> getNotifications(Long shelterId) throws EntityNotFoundException{
        log.info("Starting process to fetch all notifications of shelter with id = ", shelterId);
        Optional<ShelterEntity> shelterOptional = shelterRepository.findById(shelterId);
        if (shelterOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);

        log.info("Finished process to fetch all notifications of shelter with id = ", shelterId);
        return shelterOptional.get().getNotifications();
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public NotificationEntity getNotification(Long shelterId, Long notificationId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to fetch notification with id = ", notificationId);
		Optional<ShelterEntity> shelterOptional = shelterRepository.findById(shelterId);
		if (shelterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);
 
		Optional<NotificationEntity> notificationOptional = notificationRepository.findById(notificationId);
		if (notificationOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.NOTIFICATION_NOT_FOUND);
 
		NotificationEntity notificationEntity = notificationOptional.get();
		if (notificationEntity.getShelter() == null || !notificationEntity.getShelter().getId().equals(shelterId))
			throw new IllegalOperationException(ErrorMessage.NOTIFICATION_NOT_ASSOCIATED_TO_SHELTER);
 
		log.info("Finished process to fetch notification with id = ", notificationId);
		return notificationEntity;
	}

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public NotificationEntity updateNotification(Long shelterId, Long notificationId, NotificationEntity notificationEntity) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to update notification with id = ", notificationId);
		NotificationEntity existingNotification = getNotification(shelterId, notificationId);

		if (!validateNotification(notificationEntity))
			throw new IllegalOperationException(ErrorMessage.NOTIFICATION_NOT_VALID);

		if (notificationEntity.getChannel() == null || !VALID_CHANNELS.contains(notificationEntity.getChannel()))
			throw new IllegalOperationException(ErrorMessage.NOTIFICATION_CHANNEL_INVALID);

		notificationEntity.setId(existingNotification.getId());
		notificationEntity.setShelter(existingNotification.getShelter());
		log.info("Finished process to update notification with id = ", notificationId);
		return notificationRepository.save(notificationEntity);
	}

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public void deleteNotification(Long shelterId, Long notificationId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to delete notification with id = ", notificationId);
		NotificationEntity notificationEntity = getNotification(shelterId, notificationId);
		notificationRepository.deleteById(notificationEntity.getId());
		log.info("Finished process to delete notification with id = ", notificationId);
	}

    private boolean validateNotification(NotificationEntity notification){
        return notification.getMessage() != null && !notification.getMessage().isEmpty();
    }
    
}

