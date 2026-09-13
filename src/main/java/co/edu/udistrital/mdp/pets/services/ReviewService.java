package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import co.edu.udistrital.mdp.pets.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReviewService {

	final ReviewRepository reviewRepository;

	final AdopterRepository adopterRepository;

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public ReviewEntity createReview(Long adopterId, ReviewEntity reviewEntity)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to create review for adopter with id = {0}", adopterId);
		Optional<AdopterEntity> adopterOptional = adopterRepository.findById(adopterId);
		if (adopterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);

		if (!validateRating(reviewEntity.getRating()))
			throw new IllegalOperationException(ErrorMessage.REVIEW_RATING_INVALID);

		reviewEntity.setAdopter(adopterOptional.get());
		log.info("Finished process to create review for adopter with id = {0}", adopterId);
		return reviewRepository.save(reviewEntity);
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class })
	public List<ReviewEntity> getReviews(Long adopterId) throws EntityNotFoundException {
		log.info("Starting process to fetch reviews of adopter with id = {0}", adopterId);
		Optional<AdopterEntity> adopterOptional = adopterRepository.findById(adopterId);
		if (adopterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);

		log.info("Finished process to fetch reviews of adopter with id = {0}", adopterId);
		return adopterOptional.get().getReviews();
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public ReviewEntity getReview(Long adopterId, Long reviewId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to fetch review with id = {0} of adopter with id = " + adopterId, reviewId);
		Optional<AdopterEntity> adopterOptional = adopterRepository.findById(adopterId);
		if (adopterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);

		Optional<ReviewEntity> reviewOptional = reviewRepository.findById(reviewId);
		if (reviewOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.REVIEW_NOT_FOUND);

		ReviewEntity reviewEntity = reviewOptional.get();
		if (reviewEntity.getAdopter() == null || !reviewEntity.getAdopter().getId().equals(adopterId))
			throw new IllegalOperationException(ErrorMessage.REVIEW_NOT_ASSOCIATED_TO_ADOPTER);

		log.info("Finished process to fetch review with id = {0} of adopter with id = " + adopterId, reviewId);
		return reviewEntity;
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public ReviewEntity updateReview(Long adopterId, Long reviewId, ReviewEntity review)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to update review with id = {0} of adopter with id = " + adopterId, reviewId);
		ReviewEntity existingReview = getReview(adopterId, reviewId);

		if (!validateRating(review.getRating()))
			throw new IllegalOperationException(ErrorMessage.REVIEW_RATING_INVALID);

		review.setId(existingReview.getId());
		review.setAdopter(existingReview.getAdopter());
		log.info("Finished process to update review with id = {0} of adopter with id = " + adopterId, reviewId);
		return reviewRepository.save(review);
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public void deleteReview(Long adopterId, Long reviewId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to delete review with id = {0} of adopter with id = " + adopterId, reviewId);
		ReviewEntity reviewEntity = getReview(adopterId, reviewId);
		reviewRepository.deleteById(reviewEntity.getId());
		log.info("Finished process to delete review with id = {0} of adopter with id = " + adopterId, reviewId);
	}

	private boolean validateRating(int rating) {
		return rating >= 1 && rating <= 5;
	}
}
