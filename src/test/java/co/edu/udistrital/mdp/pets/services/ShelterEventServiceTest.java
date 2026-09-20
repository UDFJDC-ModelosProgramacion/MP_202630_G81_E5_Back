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

	private List<ShelterEventEntity> eventList = new ArrayList<>();
	private ShelterEntity shelterEntity;
	private ShelterEntity otherShelterEntity;

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

		otherShelterEntity = factory.manufacturePojo(ShelterEntity.class);
		entityManager.persist(otherShelterEntity);

		for (int i = 0; i < 3; i++) {
			ShelterEventEntity eventEntity = factory.manufacturePojo(ShelterEventEntity.class);
			eventEntity.setShelter(shelterEntity);
			entityManager.persist(eventEntity);
			eventList.add(eventEntity);
		}
		shelterEntity.setShelterEvents(eventList);
	}

	@Test
	void testCreateShelterEvent() throws EntityNotFoundException, IllegalOperationException {
		ShelterEventEntity newEntity = factory.manufacturePojo(ShelterEventEntity.class);
		ShelterEventEntity result = shelterEventService.createShelterEvent(shelterEntity.getId(), newEntity);
		assertNotNull(result);
		ShelterEventEntity entity = entityManager.find(ShelterEventEntity.class, result.getId());
		assertEquals(newEntity.getTitle(), entity.getTitle());
		assertEquals(shelterEntity.getId(), entity.getShelter().getId());
	}

	@Test
	void testCreateShelterEventInvalidShelter() {
		assertThrows(EntityNotFoundException.class, () -> {
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
	void testCreateShelterEventWithNoDate() {
		assertThrows(IllegalOperationException.class, () -> {
			ShelterEventEntity newEntity = factory.manufacturePojo(ShelterEventEntity.class);
			newEntity.setDate(null);
			shelterEventService.createShelterEvent(shelterEntity.getId(), newEntity);
		});
	}

	@Test
	void testGetShelterEvents() throws EntityNotFoundException {
		List<ShelterEventEntity> list = shelterEventService.getShelterEvents(shelterEntity.getId());
		assertEquals(eventList.size(), list.size());
	}

	@Test
	void testGetShelterEventsInvalidShelter() {
		assertThrows(EntityNotFoundException.class, () -> {
			shelterEventService.getShelterEvents(0L);
		});
	}

	@Test
	void testGetShelterEvent() throws EntityNotFoundException, IllegalOperationException {
		ShelterEventEntity entity = eventList.get(0);
		ShelterEventEntity result = shelterEventService.getShelterEvent(shelterEntity.getId(), entity.getId());
		assertNotNull(result);
		assertEquals(entity.getId(), result.getId());
	}

	@Test
	void testGetInvalidShelterEvent() {
		assertThrows(EntityNotFoundException.class, () -> {
			shelterEventService.getShelterEvent(shelterEntity.getId(), 0L);
		});
	}

	@Test
	void testGetShelterEventNotAssociatedToShelter() {
		assertThrows(IllegalOperationException.class, () -> {
			shelterEventService.getShelterEvent(otherShelterEntity.getId(), eventList.get(0).getId());
		});
	}

	@Test
	void testUpdateShelterEvent() throws EntityNotFoundException, IllegalOperationException {
		ShelterEventEntity entity = eventList.get(0);
		ShelterEventEntity pojoEntity = factory.manufacturePojo(ShelterEventEntity.class);
		pojoEntity.setId(entity.getId());

		shelterEventService.updateShelterEvent(shelterEntity.getId(), entity.getId(), pojoEntity);
		ShelterEventEntity resp = entityManager.find(ShelterEventEntity.class, entity.getId());
		assertEquals(pojoEntity.getTitle(), resp.getTitle());
	}

	@Test
	void testUpdateInvalidShelterEvent() {
		assertThrows(EntityNotFoundException.class, () -> {
			ShelterEventEntity pojoEntity = factory.manufacturePojo(ShelterEventEntity.class);
			shelterEventService.updateShelterEvent(shelterEntity.getId(), 0L, pojoEntity);
		});
	}

	@Test
	void testDeleteShelterEvent() throws EntityNotFoundException, IllegalOperationException {
		ShelterEventEntity entity = eventList.get(0);
		shelterEventService.deleteShelterEvent(shelterEntity.getId(), entity.getId());
		assertNull(entityManager.find(ShelterEventEntity.class, entity.getId()));
	}

	@Test
	void testDeleteInvalidShelterEvent() {
		assertThrows(EntityNotFoundException.class, () -> {
			shelterEventService.deleteShelterEvent(shelterEntity.getId(), 0L);
		});
	}

	@Test
	void testDeleteShelterEventNotAssociatedToShelter() {
		assertThrows(IllegalOperationException.class, () -> {
			shelterEventService.deleteShelterEvent(otherShelterEntity.getId(), eventList.get(0).getId());
		});
	}

}