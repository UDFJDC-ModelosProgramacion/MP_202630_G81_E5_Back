package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.PhotoEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.PhotoRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
@Service
public class PhotoService {

	final PhotoRepository photoRepository;

	final PetRepository petRepository;

	final ShelterRepository shelterRepository;

	
	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public PhotoEntity createPhoto(PhotoEntity photoEntity) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to create photo");

		boolean hasPet = photoEntity.getPet() != null;
		boolean hasShelter = photoEntity.getShelter() != null;

		if (!hasPet && !hasShelter)
			throw new IllegalOperationException(ErrorMessage.PHOTO_PARENT_NOT_VALID);

		if (photoEntity.getUrl() == null || photoEntity.getUrl().isEmpty())
			throw new IllegalOperationException(ErrorMessage.PHOTO_URL_NOT_VALID);

		if (hasPet) {
			Optional<PetEntity> petOptional = petRepository.findById(photoEntity.getPet().getId());
			if (petOptional.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);
			photoEntity.setPet(petOptional.get());
		}

		if (hasShelter) {
			Optional<ShelterEntity> shelterOptional = shelterRepository.findById(photoEntity.getShelter().getId());
			if (shelterOptional.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);
			photoEntity.setShelter(shelterOptional.get());
		}

		log.info("Finished process to create photo");
		return photoRepository.save(photoEntity);
	}

	

	@Transactional(rollbackFor = { EntityNotFoundException.class })
	public List<PhotoEntity> getPhotosByPet(Long petId) throws EntityNotFoundException {
		log.info("Starting process to fetch photos of pet with id = {0}", petId);
		Optional<PetEntity> petOptional = petRepository.findById(petId);
		if (petOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

		log.info("Finished process to fetch photos of pet with id = {0}", petId);
		return petOptional.get().getPhotos();
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public PhotoEntity getPhotoByPet(Long petId, Long photoId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to fetch photo with id = {0} of pet with id = " + petId, photoId);
		Optional<PetEntity> petOptional = petRepository.findById(petId);
		if (petOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

		Optional<PhotoEntity> photoOptional = photoRepository.findById(photoId);
		if (photoOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PHOTO_NOT_FOUND);

		PhotoEntity photoEntity = photoOptional.get();
		if (photoEntity.getPet() == null || !photoEntity.getPet().getId().equals(petId))
			throw new IllegalOperationException(ErrorMessage.PHOTO_NOT_ASSOCIATED_TO_PET);

		log.info("Finished process to fetch photo with id = {0} of pet with id = " + petId, photoId);
		return photoEntity;
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public PhotoEntity updatePhotoByPet(Long petId, Long photoId, PhotoEntity photo)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to update photo with id = {0} of pet with id = " + petId, photoId);
		PetEntity petEntity = validatePetExists(petId);
		PhotoEntity existingPhoto = getPhotoByPet(petId, photoId);

		if (photo.getUrl() == null || photo.getUrl().isEmpty())
			throw new IllegalOperationException(ErrorMessage.PHOTO_URL_NOT_VALID);

		photo.setId(existingPhoto.getId());
		photo.setPet(petEntity);
		photo.setShelter(existingPhoto.getShelter());
		log.info("Finished process to update photo with id = {0} of pet with id = " + petId, photoId);
		return photoRepository.save(photo);
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public void deletePhotoByPet(Long petId, Long photoId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to delete photo with id = {0} of pet with id = " + petId, photoId);
		PhotoEntity photoEntity = getPhotoByPet(petId, photoId);
		photoRepository.deleteById(photoEntity.getId());
		log.info("Finished process to delete photo with id = {0} of pet with id = " + petId, photoId);
	}

	

	@Transactional(rollbackFor = { EntityNotFoundException.class })
	public List<PhotoEntity> getPhotosByShelter(Long shelterId) throws EntityNotFoundException {
		log.info("Starting process to fetch photos of shelter with id = {0}", shelterId);
		Optional<ShelterEntity> shelterOptional = shelterRepository.findById(shelterId);
		if (shelterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);

		log.info("Finished process to fetch photos of shelter with id = {0}", shelterId);
		return shelterOptional.get().getPhotos();
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public PhotoEntity getPhotoByShelter(Long shelterId, Long photoId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to fetch photo with id = {0} of shelter with id = " + shelterId, photoId);
		Optional<ShelterEntity> shelterOptional = shelterRepository.findById(shelterId);
		if (shelterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);

		Optional<PhotoEntity> photoOptional = photoRepository.findById(photoId);
		if (photoOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PHOTO_NOT_FOUND);

		PhotoEntity photoEntity = photoOptional.get();
		if (photoEntity.getShelter() == null || !photoEntity.getShelter().getId().equals(shelterId))
			throw new IllegalOperationException(ErrorMessage.PHOTO_NOT_ASSOCIATED_TO_SHELTER);

		log.info("Finished process to fetch photo with id = {0} of shelter with id = " + shelterId, photoId);
		return photoEntity;
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public PhotoEntity updatePhotoByShelter(Long shelterId, Long photoId, PhotoEntity photo)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to update photo with id = {0} of shelter with id = " + shelterId, photoId);
		Optional<ShelterEntity> shelterOptional = shelterRepository.findById(shelterId);
		if (shelterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);

		PhotoEntity existingPhoto = getPhotoByShelter(shelterId, photoId);

		if (photo.getUrl() == null || photo.getUrl().isEmpty())
			throw new IllegalOperationException(ErrorMessage.PHOTO_URL_NOT_VALID);

		photo.setId(existingPhoto.getId());
		photo.setShelter(shelterOptional.get());
		photo.setPet(existingPhoto.getPet());
		log.info("Finished process to update photo with id = {0} of shelter with id = " + shelterId, photoId);
		return photoRepository.save(photo);
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public void deletePhotoByShelter(Long shelterId, Long photoId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to delete photo with id = {0} of shelter with id = " + shelterId, photoId);
		PhotoEntity photoEntity = getPhotoByShelter(shelterId, photoId);
		photoRepository.deleteById(photoEntity.getId());
		log.info("Finished process to delete photo with id = {0} of shelter with id = " + shelterId, photoId);
	}

	private PetEntity validatePetExists(Long petId) throws EntityNotFoundException {
		Optional<PetEntity> petOptional = petRepository.findById(petId);
		if (petOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);
		return petOptional.get();
	}
}
