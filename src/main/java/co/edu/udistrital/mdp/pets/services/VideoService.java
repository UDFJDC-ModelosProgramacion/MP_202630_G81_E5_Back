package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.VideoEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import co.edu.udistrital.mdp.pets.repositories.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
@Service
public class VideoService {

	final VideoRepository videoRepository;

	final ShelterRepository shelterRepository;

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public VideoEntity createVideo(Long shelterId, VideoEntity videoEntity)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to create video for shelter with id = {0}", shelterId);
		Optional<ShelterEntity> shelterOptional = shelterRepository.findById(shelterId);
		if (shelterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);

		if (videoEntity.getUrl() == null || videoEntity.getUrl().isEmpty())
			throw new IllegalOperationException(ErrorMessage.VIDEO_URL_NOT_VALID);

		videoEntity.setShelter(shelterOptional.get());
		log.info("Finished process to create video for shelter with id = {0}", shelterId);
		return videoRepository.save(videoEntity);
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class })
	public List<VideoEntity> getVideos(Long shelterId) throws EntityNotFoundException {
		log.info("Starting process to fetch videos of shelter with id = {0}", shelterId);
		Optional<ShelterEntity> shelterOptional = shelterRepository.findById(shelterId);
		if (shelterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);

		log.info("Finished process to fetch videos of shelter with id = {0}", shelterId);
		return shelterOptional.get().getVideos();
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public VideoEntity getVideo(Long shelterId, Long videoId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to fetch video with id = {0} of shelter with id = " + shelterId, videoId);
		Optional<ShelterEntity> shelterOptional = shelterRepository.findById(shelterId);
		if (shelterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);

		Optional<VideoEntity> videoOptional = videoRepository.findById(videoId);
		if (videoOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.VIDEO_NOT_FOUND);

		VideoEntity videoEntity = videoOptional.get();
		if (videoEntity.getShelter() == null || !videoEntity.getShelter().getId().equals(shelterId))
			throw new IllegalOperationException(ErrorMessage.VIDEO_NOT_ASSOCIATED_TO_SHELTER);

		log.info("Finished process to fetch video with id = {0} of shelter with id = " + shelterId, videoId);
		return videoEntity;
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public VideoEntity updateVideo(Long shelterId, Long videoId, VideoEntity video)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to update video with id = {0} of shelter with id = " + shelterId, videoId);
		VideoEntity existingVideo = getVideo(shelterId, videoId);

		if (video.getUrl() == null || video.getUrl().isEmpty())
			throw new IllegalOperationException(ErrorMessage.VIDEO_URL_NOT_VALID);

		video.setId(existingVideo.getId());
		video.setShelter(existingVideo.getShelter());
		log.info("Finished process to update video with id = {0} of shelter with id = " + shelterId, videoId);
		return videoRepository.save(video);
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public void deleteVideo(Long shelterId, Long videoId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to delete video with id = {0} of shelter with id = " + shelterId, videoId);
		VideoEntity videoEntity = getVideo(shelterId, videoId);
		videoRepository.deleteById(videoEntity.getId());
		log.info("Finished process to delete video with id = {0} of shelter with id = " + shelterId, videoId);
	}
}
