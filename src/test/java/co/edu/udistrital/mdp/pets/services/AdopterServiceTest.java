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
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(AdopterService.class)
class AdopterServiceTest {

	@Autowired
	private AdopterService adopterService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<AdopterEntity> adopterList = new ArrayList<>();

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		
		entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from MessageEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from ReviewEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from AdopterEntity").executeUpdate();
	}

	private void insertData() {
		for (int i = 0; i < 3; i++) {
			AdopterEntity adopterEntity = factory.manufacturePojo(AdopterEntity.class);
			entityManager.persist(adopterEntity);
			adopterList.add(adopterEntity);
		}
	}



	@Test
	void testCreateAdopter() throws IllegalOperationException {
		AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
		AdopterEntity result = adopterService.createAdopter(newEntity);
		assertNotNull(result);

		AdopterEntity entity = entityManager.find(AdopterEntity.class, result.getId());

		assertEquals(newEntity.getName(), entity.getName());
		assertEquals(newEntity.getEmail(), entity.getEmail());
		assertEquals(newEntity.getPhone(), entity.getPhone());
		// este es propio del adopter
		assertEquals(newEntity.getAddress(), entity.getAddress());
	}

	@Test
	void testCreateAdopterWithEmptyName() {
		AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
		newEntity.setName("");
		assertThrows(IllegalOperationException.class, () -> adopterService.createAdopter(newEntity));
	}

	@Test
	void testCreateAdopterWithNullName() {
		AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
		newEntity.setName(null);
		assertThrows(IllegalOperationException.class, () -> adopterService.createAdopter(newEntity));
	}

	@Test
	void testCreateAdopterWithEmptyEmail() {
		AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
		newEntity.setEmail("");
		assertThrows(IllegalOperationException.class, () -> adopterService.createAdopter(newEntity));
	}

	@Test
	void testCreateAdopterWithNullEmail() {
		AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
		newEntity.setEmail(null);
		assertThrows(IllegalOperationException.class, () -> adopterService.createAdopter(newEntity));
	}

	@Test
	void testCreateAdopterWithExistingEmail() {
		AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
		// en mayusculas para probar que no distingue entre mayusculas y minusculas
		newEntity.setEmail(adopterList.get(0).getEmail().toUpperCase());
		assertThrows(IllegalOperationException.class, () -> adopterService.createAdopter(newEntity));
	}

	// ---------- get ----------

	@Test
	void testGetAdopters() {
		List<AdopterEntity> list = adopterService.getAdopters();
		assertEquals(adopterList.size(), list.size());
		for (AdopterEntity entity : list) {
			boolean found = false;
			for (AdopterEntity stored : adopterList) {
				if (entity.getId().equals(stored.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	@Test
	void testGetAdopter() throws EntityNotFoundException {
		AdopterEntity entity = adopterList.get(0);
		AdopterEntity result = adopterService.getAdopter(entity.getId());
		assertNotNull(result);
		assertEquals(entity.getId(), result.getId());
		assertEquals(entity.getName(), result.getName());
		assertEquals(entity.getEmail(), result.getEmail());
		assertEquals(entity.getPhone(), result.getPhone());
		assertEquals(entity.getAddress(), result.getAddress());
	}

	@Test
	void testGetInvalidAdopter() {
		assertThrows(EntityNotFoundException.class, () -> adopterService.getAdopter(0L));
	}

	// ---------- update ----------

	@Test
	void testUpdateAdopter() throws EntityNotFoundException, IllegalOperationException {
		AdopterEntity entity = adopterList.get(0);
		AdopterEntity pojoEntity = factory.manufacturePojo(AdopterEntity.class);
		pojoEntity.setId(entity.getId());

		adopterService.updateAdopter(entity.getId(), pojoEntity);

		AdopterEntity resp = entityManager.find(AdopterEntity.class, entity.getId());
		assertEquals(pojoEntity.getName(), resp.getName());
		assertEquals(pojoEntity.getEmail(), resp.getEmail());
		assertEquals(pojoEntity.getPhone(), resp.getPhone());
		assertEquals(pojoEntity.getAddress(), resp.getAddress());
	}

	@Test
	void testUpdateAdopterKeepingSameEmail() throws EntityNotFoundException, IllegalOperationException {
		// si el correo es el mismo del adopter que se actualiza no debe dar error
		AdopterEntity entity = adopterList.get(0);
		AdopterEntity pojoEntity = factory.manufacturePojo(AdopterEntity.class);
		pojoEntity.setEmail(entity.getEmail());

		adopterService.updateAdopter(entity.getId(), pojoEntity);

		AdopterEntity resp = entityManager.find(AdopterEntity.class, entity.getId());
		assertEquals(entity.getEmail(), resp.getEmail());
		assertEquals(pojoEntity.getName(), resp.getName());
	}

	@Test
	void testUpdateInvalidAdopter() {
		AdopterEntity pojoEntity = factory.manufacturePojo(AdopterEntity.class);
		assertThrows(EntityNotFoundException.class, () -> adopterService.updateAdopter(0L, pojoEntity));
	}

	@Test
	void testUpdateAdopterWithNoValidName() {
		AdopterEntity entity = adopterList.get(0);
		AdopterEntity pojoEntity = factory.manufacturePojo(AdopterEntity.class);
		pojoEntity.setName("");
		assertThrows(IllegalOperationException.class, () -> adopterService.updateAdopter(entity.getId(), pojoEntity));
	}

	@Test
	void testUpdateAdopterWithNoValidEmail() {
		AdopterEntity entity = adopterList.get(0);
		AdopterEntity pojoEntity = factory.manufacturePojo(AdopterEntity.class);
		pojoEntity.setEmail(null);
		assertThrows(IllegalOperationException.class, () -> adopterService.updateAdopter(entity.getId(), pojoEntity));
	}

	@Test
	void testUpdateAdopterWithEmailOfAnotherAdopter() {
		AdopterEntity entity = adopterList.get(0);
		AdopterEntity pojoEntity = factory.manufacturePojo(AdopterEntity.class);
		pojoEntity.setEmail(adopterList.get(1).getEmail());
		assertThrows(IllegalOperationException.class, () -> adopterService.updateAdopter(entity.getId(), pojoEntity));
	}



	@Test
	void testDeleteAdopter() throws EntityNotFoundException, IllegalOperationException {
		AdopterEntity entity = adopterList.get(1);
		adopterService.deleteAdopter(entity.getId());
		assertNull(entityManager.find(AdopterEntity.class, entity.getId()));
	}

	@Test
	void testDeleteInvalidAdopter() {
		assertThrows(EntityNotFoundException.class, () -> adopterService.deleteAdopter(0L));
	}

	@Test
	void testDeleteAdopterWithAdoptions() {
		AdopterEntity entity = adopterList.get(0);
		AdoptionEntity adoptionEntity = factory.manufacturePojo(AdoptionEntity.class);
		adoptionEntity.setAdopter(entity);
		entityManager.persist(adoptionEntity);
		entity.getAdoptions().add(adoptionEntity);

		assertThrows(IllegalOperationException.class, () -> adopterService.deleteAdopter(entity.getId()));
	}

	@Test
	void testDeleteAdopterWithMessages() {
		AdopterEntity entity = adopterList.get(0);
		MessageEntity messageEntity = factory.manufacturePojo(MessageEntity.class);
		messageEntity.setAdopter(entity);
		entityManager.persist(messageEntity);
		entity.getMessages().add(messageEntity);

		assertThrows(IllegalOperationException.class, () -> adopterService.deleteAdopter(entity.getId()));
	}

	@Test
	void testDeleteAdopterWithReviews() {
		AdopterEntity entity = adopterList.get(0);
		ReviewEntity reviewEntity = factory.manufacturePojo(ReviewEntity.class);
		reviewEntity.setAdopter(entity);
		entityManager.persist(reviewEntity);
		entity.getReviews().add(reviewEntity);

		assertThrows(IllegalOperationException.class, () -> adopterService.deleteAdopter(entity.getId()));
	}

}

