package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.NotificationDTO;
import co.edu.udistrital.mdp.pets.dto.NotificationDetailDTO;
import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.NotificationService;
import lombok.RequiredArgsConstructor;

/**
 * Clase que implementa el recurso DEPENDIENTE "notifications", anidado bajo
 * "/shelters/{shelterId}/notifications" (no tiene endpoint raiz propio, igual
 * que se diseno en la coleccion Postman Notifications).
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/shelters")
public class NotificationController {

	
	private final NotificationService notificationService;

	
	private final ModelMapper modelMapper;

	@GetMapping(value = "/{shelterId}/notifications")
	@ResponseStatus(code = HttpStatus.OK)
	public List<NotificationDetailDTO> findAll(@PathVariable("shelterId") Long shelterId)
			throws EntityNotFoundException {
		List<NotificationEntity> notifications = notificationService.getNotifications(shelterId);
		return modelMapper.map(notifications, new TypeToken<List<NotificationDetailDTO>>() {
		}.getType());
	}

	@GetMapping(value = "/{shelterId}/notifications/{notificationId}")
	@ResponseStatus(code = HttpStatus.OK)
	public NotificationDetailDTO findOne(@PathVariable("shelterId") Long shelterId,
			@PathVariable("notificationId") Long notificationId)
			throws EntityNotFoundException, IllegalOperationException {
		NotificationEntity notificationEntity = notificationService.getNotification(shelterId, notificationId);
		return modelMapper.map(notificationEntity, NotificationDetailDTO.class);
	}

	@PostMapping(value = "/{shelterId}/notifications")
	@ResponseStatus(code = HttpStatus.CREATED)
	public NotificationDTO create(@PathVariable("shelterId") Long shelterId,
			@RequestBody NotificationDTO notificationDTO) throws IllegalOperationException {
		NotificationEntity notificationEntity = notificationService.createNotification(shelterId,
				modelMapper.map(notificationDTO, NotificationEntity.class));
		return modelMapper.map(notificationEntity, NotificationDTO.class);
	}

	@PutMapping(value = "/{shelterId}/notifications/{notificationId}")
	@ResponseStatus(code = HttpStatus.OK)
	public NotificationDTO update(@PathVariable("shelterId") Long shelterId,
			@PathVariable("notificationId") Long notificationId, @RequestBody NotificationDTO notificationDTO)
			throws EntityNotFoundException, IllegalOperationException {
		NotificationEntity notificationEntity = notificationService.updateNotification(shelterId, notificationId,
				modelMapper.map(notificationDTO, NotificationEntity.class));
		return modelMapper.map(notificationEntity, NotificationDTO.class);
	}

	@DeleteMapping(value = "/{shelterId}/notifications/{notificationId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("shelterId") Long shelterId,
			@PathVariable("notificationId") Long notificationId)
			throws EntityNotFoundException, IllegalOperationException {
		notificationService.deleteNotification(shelterId, notificationId);
	}
}
