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

import co.edu.udistrital.mdp.pets.dto.MessageDTO;
import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.MessageService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/adopters")
public class AdopterMessageController {

	private final MessageService messageService;

	private final ModelMapper modelMapper;

	@GetMapping(value = "/{adopterId}/messages")
	@ResponseStatus(code = HttpStatus.OK)
	public List<MessageDTO> findAll(@PathVariable("adopterId") Long adopterId) throws EntityNotFoundException {
		List<MessageEntity> messages = messageService.getMessages(adopterId);
		return modelMapper.map(messages, new TypeToken<List<MessageDTO>>() {
		}.getType());
	}

	@GetMapping(value = "/{adopterId}/messages/{messageId}")
	@ResponseStatus(code = HttpStatus.OK)
	public MessageDTO findOne(@PathVariable("adopterId") Long adopterId, @PathVariable("messageId") Long messageId)
			throws EntityNotFoundException, IllegalOperationException {
		MessageEntity messageEntity = messageService.getMessage(adopterId, messageId);
		return modelMapper.map(messageEntity, MessageDTO.class);
	}

	@PostMapping(value = "/{adopterId}/messages")
	@ResponseStatus(code = HttpStatus.CREATED)
	public MessageDTO create(@PathVariable("adopterId") Long adopterId, @RequestBody MessageDTO messageDTO)
			throws EntityNotFoundException, IllegalOperationException {
		MessageEntity messageEntity = messageService.createMessage(adopterId,
				modelMapper.map(messageDTO, MessageEntity.class));
		return modelMapper.map(messageEntity, MessageDTO.class);
	}

	@PutMapping(value = "/{adopterId}/messages/{messageId}")
	@ResponseStatus(code = HttpStatus.OK)
	public MessageDTO update(@PathVariable("adopterId") Long adopterId, @PathVariable("messageId") Long messageId,
			@RequestBody MessageDTO messageDTO) throws EntityNotFoundException, IllegalOperationException {
		MessageEntity messageEntity = messageService.updateMessage(adopterId, messageId,
				modelMapper.map(messageDTO, MessageEntity.class));
		return modelMapper.map(messageEntity, MessageDTO.class);
	}

	@DeleteMapping(value = "/{adopterId}/messages/{messageId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("adopterId") Long adopterId, @PathVariable("messageId") Long messageId)
			throws EntityNotFoundException, IllegalOperationException {
		messageService.deleteMessage(adopterId, messageId);
	}
}