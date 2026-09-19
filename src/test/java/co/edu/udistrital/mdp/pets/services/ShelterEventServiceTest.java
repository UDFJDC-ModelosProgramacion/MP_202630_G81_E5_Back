package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEventEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(ShelterEventService.class)
class ShelterEventServiceTest {

	@Autowired
	private ShelterEventService shelterEventService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<ShelterEventEntity> shelterEventList = new ArrayList<>();
	private ShelterEntity shelterEntity;

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from ShelterEventEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
	}

	private void insertData() {
		shelterEntity = factory.manufacturePojo(ShelterEntity.class);
		entityManager.persist(shelterEntity);

		for (int i = 0; i < 3; i++) {
			ShelterEventEntity eventEntity = factory.manufacturePojo(ShelterEventEntity.class);
			eventEntity.setDate(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * (i + 1)));
			eventEntity.setShelter(shelterEntity);
			entityManager.persist(eventEntity);
			shelterEventList.add(eventEntity);
		}
	}

	@Test
	void testCreateShelterEvent() throws IllegalOperationException {
		ShelterEventEntity newEntity = factory.manufacturePojo(ShelterEventEntity.class);
		newEntity.setDate(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24));

		ShelterEventEntity result = shelterEventService.createShelterEvent(shelterEntity.getId(), newEntity);
		assertNotNull(result);
		ShelterEventEntity entity = entityManager.find(ShelterEventEntity.class, result.getId());
		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getTitle(), entity.getTitle());
		assertEquals(shelterEntity.getId(), entity.getShelter().getId());
	}

	@Test
	void testCreateShelterEventWithInvalidShelter() {
		assertThrows(IllegalOperationException.class, () -> {
			ShelterEventEntity newEntity = factory.manufacturePojo(ShelterEventEntity.class);
			shelterEventService.createShelterEvent(0L, newEntity);
		});
	}

	@Test
	void testCreateShelterEventWithNoValidTitle() {
		assertThrows(IllegalOperationException.class, () -> {
			ShelterEventEntity newEntity = factory.manufacturePojo(ShelterEventEntity.class);
			newEntity.setTitle("");
			shelterEventService.createShelterEvent(shelterEntity.getId(), newEntity);
		});
	}

	@Test
	void testCreateShelterEventWithNoValidDate() {
		assertThrows(IllegalOperationException.class, () -> {
			ShelterEventEntity newEntity = factory.manufacturePojo(ShelterEventEntity.class);
			newEntity.setDate(null);
			shelterEventService.createShelterEvent(shelterEntity.getId(), newEntity);
		});
	}

	@Test
	void testGetShelterEvents() throws EntityNotFoundException {
		List<ShelterEventEntity> list = shelterEventService.getShelterEvents(shelterEntity.getId());
		assertEquals(shelterEventList.size(), list.size());
	}

	@Test
	void testGetShelterEventsWithInvalidShelter() {
		assertThrows(EntityNotFoundException.class, () -> {
			shelterEventService.getShelterEvents(0L);
		});
	}

	@Test
	void testGetShelterEvent() throws EntityNotFoundException, IllegalOperationException {
		ShelterEventEntity entity = shelterEventList.get(0);
		ShelterEventEntity resultEntity = shelterEventService.getShelterEvent(shelterEntity.getId(), entity.getId());
		assertNotNull(resultEntity);
		assertEquals(entity.getId(), resultEntity.getId());
		assertEquals(entity.getTitle(), resultEntity.getTitle());
	}

	@Test
	void testGetInvalidShelterEvent() {
		assertThrows(EntityNotFoundException.class, () -> {
			shelterEventService.getShelterEvent(shelterEntity.getId(), 0L);
		});
	}

	@Test
	void testGetShelterEventNotAssociatedToShelter() throws IllegalOperationException {
		assertThrows(IllegalOperationException.class, () -> {
			ShelterEntity otherShelter = factory.manufacturePojo(ShelterEntity.class);
			entityManager.persist(otherShelter);
			ShelterEventEntity entity = shelterEventList.get(0);
			shelterEventService.getShelterEvent(otherShelter.getId(), entity.getId());
		});
	}

	@Test
	void testUpdateShelterEvent() throws EntityNotFoundException, IllegalOperationException {
		ShelterEventEntity entity = shelterEventList.get(0);
		ShelterEventEntity pojoEntity = factory.manufacturePojo(ShelterEventEntity.class);
		pojoEntity.setId(entity.getId());
		pojoEntity.setDate(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24));

		shelterEventService.updateShelterEvent(shelterEntity.getId(), entity.getId(), pojoEntity);

		ShelterEventEntity resp = entityManager.find(ShelterEventEntity.class, entity.getId());
		assertEquals(pojoEntity.getTitle(), resp.getTitle());
		assertEquals(shelterEntity.getId(), resp.getShelter().getId());
	}

	@Test
	void testUpdateInvalidShelterEvent() {
		assertThrows(EntityNotFoundException.class, () -> {
			ShelterEventEntity pojoEntity = factory.manufacturePojo(ShelterEventEntity.class);
			pojoEntity.setId(0L);
			shelterEventService.updateShelterEvent(shelterEntity.getId(), 0L, pojoEntity);
		});
	}

	@Test
	void testUpdateShelterEventWithNoValidTitle() {
		assertThrows(IllegalOperationException.class, () -> {
			ShelterEventEntity entity = shelterEventList.get(0);
			ShelterEventEntity pojoEntity = factory.manufacturePojo(ShelterEventEntity.class);
			pojoEntity.setId(entity.getId());
			pojoEntity.setTitle("");
			shelterEventService.updateShelterEvent(shelterEntity.getId(), entity.getId(), pojoEntity);
		});
	}

	@Test
	void testDeleteShelterEvent() throws EntityNotFoundException, IllegalOperationException {
		ShelterEventEntity entity = shelterEventList.get(0);
		shelterEventService.deleteShelterEvent(shelterEntity.getId(), entity.getId());
		ShelterEventEntity deleted = entityManager.find(ShelterEventEntity.class, entity.getId());
		assertNull(deleted);
	}

	@Test
	void testDeleteInvalidShelterEvent() {
		assertThrows(EntityNotFoundException.class, () -> {
			shelterEventService.deleteShelterEvent(shelterEntity.getId(), 0L);
		});
	}

	@Test
	void testDeleteShelterEventAlreadyPassed() {
		assertThrows(IllegalOperationException.class, () -> {
			ShelterEventEntity entity = factory.manufacturePojo(ShelterEventEntity.class);
			entity.setDate(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24));
			entity.setShelter(shelterEntity);
			entityManager.persist(entity);
			shelterEventService.deleteShelterEvent(shelterEntity.getId(), entity.getId());
		});
	}

}