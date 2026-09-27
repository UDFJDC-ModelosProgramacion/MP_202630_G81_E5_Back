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

import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.PhotoEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEventEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.entities.VideoEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(ShelterService.class)
class ShelterServiceTest {

	@Autowired
	private ShelterService shelterService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<ShelterEntity> shelterList = new ArrayList<>();

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
	}

	private void insertData() {
		for (int i = 0; i < 3; i++) {
			ShelterEntity shelterEntity = factory.manufacturePojo(ShelterEntity.class);
			entityManager.persist(shelterEntity);
			shelterList.add(shelterEntity);
		}
	}

	@Test
	void testCreateShelter() throws IllegalOperationException {
		ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
		ShelterEntity result = shelterService.createShelter(newEntity);
		assertNotNull(result);
		ShelterEntity entity = entityManager.find(ShelterEntity.class, result.getId());
		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getName(), entity.getName());
		assertEquals(newEntity.getCity(), entity.getCity());
	}

	@Test
	void testCreateShelterWithNoValidName() {
		assertThrows(IllegalOperationException.class, () -> {
			ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
			newEntity.setName("");
			shelterService.createShelter(newEntity);
		});
	}

	@Test
	void testCreateShelterWithNoValidCity() {
		assertThrows(IllegalOperationException.class, () -> {
			ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
			newEntity.setCity(null);
			shelterService.createShelter(newEntity);
		});
	}

	@Test
	void testGetShelters() {
		List<ShelterEntity> list = shelterService.getShelters();
		assertEquals(shelterList.size(), list.size());
	}

	@Test
	void testGetShelter() throws EntityNotFoundException {
		ShelterEntity entity = shelterList.get(0);
		ShelterEntity resultEntity = shelterService.getShelter(entity.getId());
		assertNotNull(resultEntity);
		assertEquals(entity.getId(), resultEntity.getId());
		assertEquals(entity.getName(), resultEntity.getName());
	}

	@Test
	void testGetInvalidShelter() {
		assertThrows(EntityNotFoundException.class, () -> {
			shelterService.getShelter(0L);
		});
	}

	@Test
	void testUpdateShelter() throws EntityNotFoundException, IllegalOperationException {
		ShelterEntity entity = shelterList.get(0);
		ShelterEntity pojoEntity = factory.manufacturePojo(ShelterEntity.class);
		pojoEntity.setId(entity.getId());

		shelterService.updateShelter(entity.getId(), pojoEntity);

		ShelterEntity resp = entityManager.find(ShelterEntity.class, entity.getId());
		assertEquals(pojoEntity.getName(), resp.getName());
		assertEquals(pojoEntity.getCity(), resp.getCity());
	}

	@Test
	void testUpdateInvalidShelter() {
		assertThrows(EntityNotFoundException.class, () -> {
			ShelterEntity pojoEntity = factory.manufacturePojo(ShelterEntity.class);
			pojoEntity.setId(0L);
			shelterService.updateShelter(0L, pojoEntity);
		});
	}

	@Test
	void testUpdateShelterWithNoValidName() {
		assertThrows(IllegalOperationException.class, () -> {
			ShelterEntity entity = shelterList.get(0);
			ShelterEntity pojoEntity = factory.manufacturePojo(ShelterEntity.class);
			pojoEntity.setId(entity.getId());
			pojoEntity.setName("");
			shelterService.updateShelter(entity.getId(), pojoEntity);
		});
	}

	@Test
	void testDeleteShelter() throws EntityNotFoundException, IllegalOperationException {
		ShelterEntity entity = shelterList.get(1);
		shelterService.deleteShelter(entity.getId());
		ShelterEntity deleted = entityManager.find(ShelterEntity.class, entity.getId());
		assertNull(deleted);
	}

	@Test
	void testDeleteInvalidShelter() {
		assertThrows(EntityNotFoundException.class, () -> {
			shelterService.deleteShelter(0L);
		});
	}

	@Test
	void testDeleteShelterWithPets() {
		assertThrows(IllegalOperationException.class, () -> {
			ShelterEntity entity = shelterList.get(0);
			PetEntity petEntity = factory.manufacturePojo(PetEntity.class);
			petEntity.setShelter(entity);
			entityManager.persist(petEntity);
			entity.getPets().add(petEntity);
			shelterService.deleteShelter(entity.getId());
		});
	}

	@Test
	void testDeleteShelterWithVeterinarians() {
		assertThrows(IllegalOperationException.class, () -> {
			ShelterEntity entity = shelterList.get(0);
			VeterinarianEntity vetEntity = factory.manufacturePojo(VeterinarianEntity.class);
			vetEntity.setShelter(entity);
			entityManager.persist(vetEntity);
			entity.getVeterinarians().add(vetEntity);
			shelterService.deleteShelter(entity.getId());
		});
	}

	@Test
	void testDeleteShelterWithNotifications() {
		assertThrows(IllegalOperationException.class, () -> {
			ShelterEntity entity = shelterList.get(0);
			NotificationEntity notificationEntity = factory.manufacturePojo(NotificationEntity.class);
			notificationEntity.setShelter(entity);
			entityManager.persist(notificationEntity);
			entity.getNotifications().add(notificationEntity);
			shelterService.deleteShelter(entity.getId());
		});
	}

	@Test
	void testDeleteShelterWithPhotos() {
		assertThrows(IllegalOperationException.class, () -> {
			ShelterEntity entity = shelterList.get(0);
			PhotoEntity photoEntity = factory.manufacturePojo(PhotoEntity.class);
			photoEntity.setShelter(entity);
			entityManager.persist(photoEntity);
			entity.getPhotos().add(photoEntity);
			shelterService.deleteShelter(entity.getId());
		});
	}

	@Test
	void testDeleteShelterWithVideos() {
		assertThrows(IllegalOperationException.class, () -> {
			ShelterEntity entity = shelterList.get(0);
			VideoEntity videoEntity = factory.manufacturePojo(VideoEntity.class);
			videoEntity.setShelter(entity);
			entityManager.persist(videoEntity);
			entity.getVideos().add(videoEntity);
			shelterService.deleteShelter(entity.getId());
		});
	}

	@Test
	void testDeleteShelterWithShelterEvents() {
		assertThrows(IllegalOperationException.class, () -> {
			ShelterEntity entity = shelterList.get(0);
			ShelterEventEntity eventEntity = factory.manufacturePojo(ShelterEventEntity.class);
			eventEntity.setShelter(entity);
			entityManager.persist(eventEntity);
			entity.getShelterEvents().add(eventEntity);
			shelterService.deleteShelter(entity.getId());
		});
	}

}