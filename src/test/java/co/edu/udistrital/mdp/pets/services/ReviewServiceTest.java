package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(ReviewService.class)
class ReviewServiceTest {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<ReviewEntity> reviewList = new ArrayList<>();
	private AdopterEntity adopterEntity;
	private AdopterEntity otherAdopterEntity;
	private ReviewEntity otherAdopterReview;

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from ReviewEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from AdopterEntity").executeUpdate();
	}

	private void insertData() {
		adopterEntity = factory.manufacturePojo(AdopterEntity.class);
		entityManager.persist(adopterEntity);

		for (int i = 0; i < 3; i++) {
			ReviewEntity reviewEntity = factory.manufacturePojo(ReviewEntity.class);
			reviewEntity.setRating(4);
			reviewEntity.setAdopter(adopterEntity);
			entityManager.persist(reviewEntity);
			reviewList.add(reviewEntity);
		}
		adopterEntity.setReviews(reviewList);

		otherAdopterEntity = factory.manufacturePojo(AdopterEntity.class);
		entityManager.persist(otherAdopterEntity);

		otherAdopterReview = factory.manufacturePojo(ReviewEntity.class);
		otherAdopterReview.setRating(3);
		otherAdopterReview.setAdopter(otherAdopterEntity);
		entityManager.persist(otherAdopterReview);
	}

	// createReview

	@Test
	void testCreateReview() throws EntityNotFoundException, IllegalOperationException {
		ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
		newEntity.setRating(5);

		ReviewEntity result = reviewService.createReview(adopterEntity.getId(), newEntity);
		assertNotNull(result);
		ReviewEntity entity = entityManager.find(ReviewEntity.class, result.getId());
		assertEquals(newEntity.getComment(), entity.getComment());
		assertEquals(newEntity.getRating(), entity.getRating());
		assertEquals(adopterEntity.getId(), entity.getAdopter().getId());
	}

	@Test
	void testCreateReviewWithInvalidAdopter() {
		assertThrows(EntityNotFoundException.class, () -> {
			ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
			newEntity.setRating(5);
			reviewService.createReview(0L, newEntity);
		});
	}

	@Test
	void testCreateReviewWithRatingTooLow() {
		assertThrows(IllegalOperationException.class, () -> {
			ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
			newEntity.setRating(0);
			reviewService.createReview(adopterEntity.getId(), newEntity);
		});
	}

	@Test
	void testCreateReviewWithRatingTooHigh() {
		assertThrows(IllegalOperationException.class, () -> {
			ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
			newEntity.setRating(6);
			reviewService.createReview(adopterEntity.getId(), newEntity);
		});
	}

	// getReviews / getReview

	@Test
	void testGetReviews() throws EntityNotFoundException {
		List<ReviewEntity> list = reviewService.getReviews(adopterEntity.getId());
		assertEquals(reviewList.size(), list.size());
	}

	@Test
	void testGetReviewsWithInvalidAdopter() {
		assertThrows(EntityNotFoundException.class, () -> {
			reviewService.getReviews(0L);
		});
	}

	@Test
	void testGetReview() throws EntityNotFoundException, IllegalOperationException {
		ReviewEntity entity = reviewList.get(0);
		ReviewEntity result = reviewService.getReview(adopterEntity.getId(), entity.getId());
		assertNotNull(result);
		assertEquals(entity.getId(), result.getId());
		assertEquals(entity.getRating(), result.getRating());
	}

	@Test
	void testGetReviewWithInvalidAdopter() {
		assertThrows(EntityNotFoundException.class, () -> {
			reviewService.getReview(0L, reviewList.get(0).getId());
		});
	}

	@Test
	void testGetInvalidReview() {
		assertThrows(EntityNotFoundException.class, () -> {
			reviewService.getReview(adopterEntity.getId(), 0L);
		});
	}

	@Test
	void testGetReviewNotAssociatedToAdopter() {
		assertThrows(IllegalOperationException.class, () -> {
			reviewService.getReview(adopterEntity.getId(), otherAdopterReview.getId());
		});
	}

	// updateReview

	@Test
	void testUpdateReview() throws EntityNotFoundException, IllegalOperationException {
		ReviewEntity entity = reviewList.get(0);
		ReviewEntity pojoEntity = factory.manufacturePojo(ReviewEntity.class);
		pojoEntity.setId(entity.getId());
		pojoEntity.setRating(2);

		reviewService.updateReview(adopterEntity.getId(), entity.getId(), pojoEntity);

		ReviewEntity resp = entityManager.find(ReviewEntity.class, entity.getId());
		assertEquals(pojoEntity.getComment(), resp.getComment());
		assertEquals(pojoEntity.getRating(), resp.getRating());
		assertEquals(adopterEntity.getId(), resp.getAdopter().getId());
	}

	@Test
	void testUpdateReviewWithInvalidAdopter() {
		assertThrows(EntityNotFoundException.class, () -> {
			ReviewEntity entity = reviewList.get(0);
			ReviewEntity pojoEntity = factory.manufacturePojo(ReviewEntity.class);
			pojoEntity.setId(entity.getId());
			pojoEntity.setRating(3);
			reviewService.updateReview(0L, entity.getId(), pojoEntity);
		});
	}

	@Test
	void testUpdateInvalidReview() {
		assertThrows(EntityNotFoundException.class, () -> {
			ReviewEntity pojoEntity = factory.manufacturePojo(ReviewEntity.class);
			pojoEntity.setId(0L);
			pojoEntity.setRating(3);
			reviewService.updateReview(adopterEntity.getId(), 0L, pojoEntity);
		});
	}

	@Test
	void testUpdateReviewWithInvalidRating() {
		assertThrows(IllegalOperationException.class, () -> {
			ReviewEntity entity = reviewList.get(0);
			ReviewEntity pojoEntity = factory.manufacturePojo(ReviewEntity.class);
			pojoEntity.setId(entity.getId());
			pojoEntity.setRating(-1);
			reviewService.updateReview(adopterEntity.getId(), entity.getId(), pojoEntity);
		});
	}

	@Test
	void testUpdateReviewNotAssociatedToAdopter() {
		assertThrows(IllegalOperationException.class, () -> {
			ReviewEntity pojoEntity = factory.manufacturePojo(ReviewEntity.class);
			pojoEntity.setId(otherAdopterReview.getId());
			pojoEntity.setRating(3);
			reviewService.updateReview(adopterEntity.getId(), otherAdopterReview.getId(), pojoEntity);
		});
	}

	// deleteReview

	@Test
	void testDeleteReview() throws EntityNotFoundException, IllegalOperationException {
		ReviewEntity entity = reviewList.get(0);
		reviewService.deleteReview(adopterEntity.getId(), entity.getId());
		assertNull(entityManager.find(ReviewEntity.class, entity.getId()));
	}

	@Test
	void testDeleteReviewWithInvalidAdopter() {
		assertThrows(EntityNotFoundException.class, () -> {
			reviewService.deleteReview(0L, reviewList.get(0).getId());
		});
	}

	@Test
	void testDeleteInvalidReview() {
		assertThrows(EntityNotFoundException.class, () -> {
			reviewService.deleteReview(adopterEntity.getId(), 0L);
		});
	}

	@Test
	void testDeleteReviewNotAssociatedToAdopter() {
		assertThrows(IllegalOperationException.class, () -> {
			reviewService.deleteReview(adopterEntity.getId(), otherAdopterReview.getId());
		});
	}
}
