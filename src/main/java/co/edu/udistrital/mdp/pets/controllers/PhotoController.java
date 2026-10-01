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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.PhotoDTO;
import co.edu.udistrital.mdp.pets.entities.PhotoEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.PhotoService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class PhotoController {

	private final PhotoService photoService;
	private final ModelMapper modelMapper;

	

	@PostMapping(value = "/photos")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PhotoDTO create(@RequestBody PhotoDTO photoDTO)
			throws EntityNotFoundException, IllegalOperationException {
		PhotoEntity photo = photoService.createPhoto(modelMapper.map(photoDTO, PhotoEntity.class));
		return modelMapper.map(photo, PhotoDTO.class);
	}

	

	@GetMapping(value = "/pets/{petId}/photos")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PhotoDTO> findAllByPet(@PathVariable Long petId) throws EntityNotFoundException {
		List<PhotoEntity> photos = photoService.getPhotosByPet(petId);
		return modelMapper.map(photos, new TypeToken<List<PhotoDTO>>() {
		}.getType());
	}

	@GetMapping(value = "/pets/{petId}/photos/{photoId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PhotoDTO findOneByPet(@PathVariable Long petId, @PathVariable Long photoId)
			throws EntityNotFoundException, IllegalOperationException {
		return modelMapper.map(photoService.getPhotoByPet(petId, photoId), PhotoDTO.class);
	}

	@PutMapping(value = "/pets/{petId}/photos/{photoId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PhotoDTO updateByPet(@PathVariable Long petId, @PathVariable Long photoId,
			@RequestBody PhotoDTO photoDTO) throws EntityNotFoundException, IllegalOperationException {
		PhotoEntity photo = photoService.updatePhotoByPet(petId, photoId,
				modelMapper.map(photoDTO, PhotoEntity.class));
		return modelMapper.map(photo, PhotoDTO.class);
	}

	@DeleteMapping(value = "/pets/{petId}/photos/{photoId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteByPet(@PathVariable Long petId, @PathVariable Long photoId)
			throws EntityNotFoundException, IllegalOperationException {
		photoService.deletePhotoByPet(petId, photoId);
	}

	

	@GetMapping(value = "/shelters/{shelterId}/photos")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PhotoDTO> findAllByShelter(@PathVariable Long shelterId) throws EntityNotFoundException {
		List<PhotoEntity> photos = photoService.getPhotosByShelter(shelterId);
		return modelMapper.map(photos, new TypeToken<List<PhotoDTO>>() {
		}.getType());
	}

	@GetMapping(value = "/shelters/{shelterId}/photos/{photoId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PhotoDTO findOneByShelter(@PathVariable Long shelterId, @PathVariable Long photoId)
			throws EntityNotFoundException, IllegalOperationException {
		return modelMapper.map(photoService.getPhotoByShelter(shelterId, photoId), PhotoDTO.class);
	}

	@PutMapping(value = "/shelters/{shelterId}/photos/{photoId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PhotoDTO updateByShelter(@PathVariable Long shelterId, @PathVariable Long photoId,
			@RequestBody PhotoDTO photoDTO) throws EntityNotFoundException, IllegalOperationException {
		PhotoEntity photo = photoService.updatePhotoByShelter(shelterId, photoId,
				modelMapper.map(photoDTO, PhotoEntity.class));
		return modelMapper.map(photo, PhotoDTO.class);
	}

	@DeleteMapping(value = "/shelters/{shelterId}/photos/{photoId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteByShelter(@PathVariable Long shelterId, @PathVariable Long photoId)
			throws EntityNotFoundException, IllegalOperationException {
		photoService.deletePhotoByShelter(shelterId, photoId);
	}
}
