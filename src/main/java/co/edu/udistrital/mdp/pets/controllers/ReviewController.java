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

import co.edu.udistrital.mdp.pets.dto.ReviewDTO;
import co.edu.udistrital.mdp.pets.dto.ReviewDetailDTO;
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ReviewService;
import lombok.RequiredArgsConstructor;

/**
 * Clase que implementa el recurso DEPENDIENTE "reviews", anidado bajo
 * "/adopters/{adopterId}/reviews" (no tiene endpoint raíz propio, igual que
 * se diseñó en la colección Postman AdopterReview).
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/adopters")

public class ReviewController {

	private final ReviewService reviewService;

	private final ModelMapper modelMapper;

	@GetMapping(value = "/{adopterId}/reviews")
	@ResponseStatus(code = HttpStatus.OK)
	public List<ReviewDetailDTO> findAll(@PathVariable("adopterId") Long adopterId) throws EntityNotFoundException {
		List<ReviewEntity> reviews = reviewService.getReviews(adopterId);
		return modelMapper.map(reviews, new TypeToken<List<ReviewDetailDTO>>() {
		}.getType());
	}

	@GetMapping(value = "/{adopterId}/reviews/{reviewId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ReviewDetailDTO findOne(@PathVariable("adopterId") Long adopterId,
			@PathVariable("reviewId") Long reviewId) throws EntityNotFoundException, IllegalOperationException {
		ReviewEntity reviewEntity = reviewService.getReview(adopterId, reviewId);
		return modelMapper.map(reviewEntity, ReviewDetailDTO.class);
	}

	@PostMapping(value = "/{adopterId}/reviews")
	@ResponseStatus(code = HttpStatus.CREATED)
	public ReviewDTO create(@PathVariable("adopterId") Long adopterId, @RequestBody ReviewDTO reviewDTO)
			throws EntityNotFoundException, IllegalOperationException {
		ReviewEntity reviewEntity = reviewService.createReview(adopterId,
				modelMapper.map(reviewDTO, ReviewEntity.class));
		return modelMapper.map(reviewEntity, ReviewDTO.class);
	}

	@PutMapping(value = "/{adopterId}/reviews/{reviewId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ReviewDTO update(@PathVariable("adopterId") Long adopterId, @PathVariable("reviewId") Long reviewId,
			@RequestBody ReviewDTO reviewDTO) throws EntityNotFoundException, IllegalOperationException {
		ReviewEntity reviewEntity = reviewService.updateReview(adopterId, reviewId,
				modelMapper.map(reviewDTO, ReviewEntity.class));
		return modelMapper.map(reviewEntity, ReviewDTO.class);
	}

	@DeleteMapping(value = "/{adopterId}/reviews/{reviewId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("adopterId") Long adopterId, @PathVariable("reviewId") Long reviewId)
			throws EntityNotFoundException, IllegalOperationException {
		reviewService.deleteReview(adopterId, reviewId);
	}
}