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

    /**
     * Regla: el adopter debe existir; rating entre 1 y 5.
     */
    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public ReviewEntity createReview(Long adopterId, ReviewEntity reviewEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting the process of creating a review for adopter with id = {}", adopterId);

        AdopterEntity adopterEntity = getAdopterOrThrow(adopterId);

        if (!validateRating(reviewEntity))
            throw new IllegalOperationException(ErrorMessage.REVIEW_RATING_INVALID);

        reviewEntity.setAdopter(adopterEntity);

        log.info("Finished process to create a review for adopter with id = {}", adopterId);
        return reviewRepository.save(reviewEntity);
    }

    /**
     * Lista todas las reviews de un adopter. El adopter debe existir.
     */
    @Transactional(rollbackFor = { EntityNotFoundException.class })
    public List<ReviewEntity> getReviews(Long adopterId) throws EntityNotFoundException {
        log.info("Starting process to fetch all reviews of adopter with id = {}", adopterId);

        AdopterEntity adopterEntity = getAdopterOrThrow(adopterId);

        log.info("Finished process to fetch all reviews of adopter with id = {}", adopterId);
        return adopterEntity.getReviews();
    }

    /**
     * Regla: el adopter debe existir; la review debe pertenecer a ese adopter.
     */
    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public ReviewEntity getReview(Long adopterId, Long reviewId)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to fetch review with id = {} of adopter with id = {}", reviewId, adopterId);

        getAdopterOrThrow(adopterId);

        Optional<ReviewEntity> reviewOptional = reviewRepository.findById(reviewId);
        if (reviewOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.REVIEW_NOT_FOUND);

        ReviewEntity reviewEntity = reviewOptional.get();
        if (reviewEntity.getAdopter() == null || !reviewEntity.getAdopter().getId().equals(adopterId))
            throw new IllegalOperationException(ErrorMessage.REVIEW_NOT_ASSOCIATED_TO_ADOPTER);

        log.info("Finished process to fetch review with id = {} of adopter with id = {}", reviewId, adopterId);
        return reviewEntity;
    }

    /**
     * Regla: la review debe existir y pertenecer al adopter indicado; rating entre 1 y 5.
     */
    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public ReviewEntity updateReview(Long adopterId, Long reviewId, ReviewEntity reviewEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to update review with id = {} of adopter with id = {}", reviewId, adopterId);

        // Valida que la review exista y pertenezca al adopter indicado
        ReviewEntity existingReview = getReview(adopterId, reviewId);

        if (!validateRating(reviewEntity))
            throw new IllegalOperationException(ErrorMessage.REVIEW_RATING_INVALID);

        reviewEntity.setId(reviewId);
        reviewEntity.setAdopter(existingReview.getAdopter());

        log.info("Finished process to update review with id = {} of adopter with id = {}", reviewId, adopterId);
        return reviewRepository.save(reviewEntity);
    }

    /**
     * Regla: la review debe existir y pertenecer al adopter indicado.
     */
    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public void deleteReview(Long adopterId, Long reviewId)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to delete review with id = {} of adopter with id = {}", reviewId, adopterId);

        // Valida que la review exista y pertenezca al adopter indicado
        getReview(adopterId, reviewId);

        reviewRepository.deleteById(reviewId);
        log.info("Finished process to delete review with id = {} of adopter with id = {}", reviewId, adopterId);
    }

    private AdopterEntity getAdopterOrThrow(Long adopterId) throws EntityNotFoundException {
        Optional<AdopterEntity> adopterOptional = adopterRepository.findById(adopterId);
        if (adopterOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);
        return adopterOptional.get();
    }

    private boolean validateRating(ReviewEntity review) {
        return review.getRating() >= 1 && review.getRating() <= 5;
    }
}