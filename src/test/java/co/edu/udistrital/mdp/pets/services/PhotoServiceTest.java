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

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.PhotoEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@DataJpaTest
@Transactional
@Import(PhotoService.class)
class PhotoServiceTest {

	@Autowired
	private PhotoService photoService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<PhotoEntity> petPhotoList = new ArrayList<>();
	private List<PhotoEntity> shelterPhotoList = new ArrayList<>();
	private PetEntity petEntity;
	private ShelterEntity shelterEntity;

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from PhotoEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
	}

	private void insertData() {
		shelterEntity = factory.manufacturePojo(ShelterEntity.class);
		entityManager.persist(shelterEntity);

		petEntity = factory.manufacturePojo(PetEntity.class);
		petEntity.setShelter(shelterEntity);
		entityManager.persist(petEntity);

		for (int i = 0; i < 3; i++) {
			PhotoEntity photoEntity = factory.manufacturePojo(PhotoEntity.class);
			photoEntity.setPet(petEntity);
			photoEntity.setShelter(null);
			entityManager.persist(photoEntity);
			petPhotoList.add(photoEntity);
		}
		petEntity.setPhotos(petPhotoList);

		for (int i = 0; i < 3; i++) {
			PhotoEntity photoEntity = factory.manufacturePojo(PhotoEntity.class);
			photoEntity.setShelter(shelterEntity);
			photoEntity.setPet(null);
			entityManager.persist(photoEntity);
			shelterPhotoList.add(photoEntity);
		}
        shelterEntity.setPhotos(shelterPhotoList);
	}

	// createPhoto 

	@Test
	void testCreatePhotoForPet() throws EntityNotFoundException, IllegalOperationException {
		PhotoEntity newEntity = factory.manufacturePojo(PhotoEntity.class);
		newEntity.setPet(petEntity);
		newEntity.setShelter(null);

		PhotoEntity result = photoService.createPhoto(newEntity);
		assertNotNull(result);
		PhotoEntity entity = entityManager.find(PhotoEntity.class, result.getId());
		assertEquals(newEntity.getUrl(), entity.getUrl());
		assertEquals(petEntity.getId(), entity.getPet().getId());
	}

	@Test
	void testCreatePhotoForShelter() throws EntityNotFoundException, IllegalOperationException {
		PhotoEntity newEntity = factory.manufacturePojo(PhotoEntity.class);
		newEntity.setShelter(shelterEntity);
		newEntity.setPet(null);

		PhotoEntity result = photoService.createPhoto(newEntity);
		assertNotNull(result);
		PhotoEntity entity = entityManager.find(PhotoEntity.class, result.getId());
		assertEquals(shelterEntity.getId(), entity.getShelter().getId());
	}

	@Test
	void testCreatePhotoWithNoParent() {
		assertThrows(IllegalOperationException.class, () -> {
			PhotoEntity newEntity = factory.manufacturePojo(PhotoEntity.class);
			newEntity.setPet(null);
			newEntity.setShelter(null);
			photoService.createPhoto(newEntity);
		});
	}

	@Test
	void testCreatePhotoWithNoValidUrl() {
		assertThrows(IllegalOperationException.class, () -> {
			PhotoEntity newEntity = factory.manufacturePojo(PhotoEntity.class);
			newEntity.setPet(petEntity);
			newEntity.setShelter(null);
			newEntity.setUrl("");
			photoService.createPhoto(newEntity);
		});
	}

	@Test
	void testCreatePhotoWithInvalidPet() {
		assertThrows(EntityNotFoundException.class, () -> {
			PhotoEntity newEntity = factory.manufacturePojo(PhotoEntity.class);
			PetEntity invalidPet = new PetEntity();
			invalidPet.setId(0L);
			newEntity.setPet(invalidPet);
			newEntity.setShelter(null);
			photoService.createPhoto(newEntity);
		});
	}

	@Test
	void testCreatePhotoWithInvalidShelter() {
		assertThrows(EntityNotFoundException.class, () -> {
			PhotoEntity newEntity = factory.manufacturePojo(PhotoEntity.class);
			ShelterEntity invalidShelter = new ShelterEntity();
			invalidShelter.setId(0L);
			newEntity.setShelter(invalidShelter);
			newEntity.setPet(null);
			photoService.createPhoto(newEntity);
		});
	}

	//  fotos anidadas bajo Pet 

	@Test
	void testGetPhotosByPet() throws EntityNotFoundException {
		List<PhotoEntity> list = photoService.getPhotosByPet(petEntity.getId());
		assertEquals(petPhotoList.size(), list.size());
	}

	@Test
	void testGetPhotosByInvalidPet() {
		assertThrows(EntityNotFoundException.class, () -> {
			photoService.getPhotosByPet(0L);
		});
	}

	@Test
	void testGetPhotoByPet() throws EntityNotFoundException, IllegalOperationException {
		PhotoEntity entity = petPhotoList.get(0);
		PhotoEntity result = photoService.getPhotoByPet(petEntity.getId(), entity.getId());
		assertNotNull(result);
		assertEquals(entity.getId(), result.getId());
	}

	@Test
	void testGetPhotoByInvalidPet() {
		assertThrows(EntityNotFoundException.class, () -> {
			photoService.getPhotoByPet(0L, petPhotoList.get(0).getId());
		});
	}

	@Test
	void testGetInvalidPhotoByPet() {
		assertThrows(EntityNotFoundException.class, () -> {
			photoService.getPhotoByPet(petEntity.getId(), 0L);
		});
	}

	@Test
	void testGetPhotoNotAssociatedToPet() {
		assertThrows(IllegalOperationException.class, () -> {
			photoService.getPhotoByPet(petEntity.getId(), shelterPhotoList.get(0).getId());
		});
	}

	@Test
	void testUpdatePhotoByPet() throws EntityNotFoundException, IllegalOperationException {
		PhotoEntity entity = petPhotoList.get(0);
		PhotoEntity pojoEntity = factory.manufacturePojo(PhotoEntity.class);
		pojoEntity.setId(entity.getId());

		photoService.updatePhotoByPet(petEntity.getId(), entity.getId(), pojoEntity);
		PhotoEntity resp = entityManager.find(PhotoEntity.class, entity.getId());
		assertEquals(pojoEntity.getUrl(), resp.getUrl());
	}

	@Test
	void testUpdatePhotoByPetWithNoValidUrl() {
		assertThrows(IllegalOperationException.class, () -> {
			PhotoEntity entity = petPhotoList.get(0);
			PhotoEntity pojoEntity = factory.manufacturePojo(PhotoEntity.class);
			pojoEntity.setId(entity.getId());
			pojoEntity.setUrl(null);
			photoService.updatePhotoByPet(petEntity.getId(), entity.getId(), pojoEntity);
		});
	}

	@Test
	void testDeletePhotoByPet() throws EntityNotFoundException, IllegalOperationException {
		PhotoEntity entity = petPhotoList.get(0);
		photoService.deletePhotoByPet(petEntity.getId(), entity.getId());
		assertNull(entityManager.find(PhotoEntity.class, entity.getId()));
	}

	@Test
	void testDeletePhotoNotAssociatedToPet() {
		assertThrows(IllegalOperationException.class, () -> {
			photoService.deletePhotoByPet(petEntity.getId(), shelterPhotoList.get(0).getId());
		});
	}

	// fotos anidadas bajo Shelter 

	@Test
	void testGetPhotosByShelter() throws EntityNotFoundException {
		List<PhotoEntity> list = photoService.getPhotosByShelter(shelterEntity.getId());
		assertEquals(shelterPhotoList.size(), list.size());
	}

	@Test
	void testGetPhotosByInvalidShelter() {
		assertThrows(EntityNotFoundException.class, () -> {
			photoService.getPhotosByShelter(0L);
		});
	}

	@Test
	void testGetPhotoByShelter() throws EntityNotFoundException, IllegalOperationException {
		PhotoEntity entity = shelterPhotoList.get(0);
		PhotoEntity result = photoService.getPhotoByShelter(shelterEntity.getId(), entity.getId());
		assertNotNull(result);
		assertEquals(entity.getId(), result.getId());
	}

	@Test
	void testGetPhotoNotAssociatedToShelter() {
		assertThrows(IllegalOperationException.class, () -> {
			photoService.getPhotoByShelter(shelterEntity.getId(), petPhotoList.get(0).getId());
		});
	}

	@Test
	void testUpdatePhotoByShelter() throws EntityNotFoundException, IllegalOperationException {
		PhotoEntity entity = shelterPhotoList.get(0);
		PhotoEntity pojoEntity = factory.manufacturePojo(PhotoEntity.class);
		pojoEntity.setId(entity.getId());

		photoService.updatePhotoByShelter(shelterEntity.getId(), entity.getId(), pojoEntity);
		PhotoEntity resp = entityManager.find(PhotoEntity.class, entity.getId());
		assertEquals(pojoEntity.getUrl(), resp.getUrl());
	}

	@Test
	void testDeletePhotoByShelter() throws EntityNotFoundException, IllegalOperationException {
		PhotoEntity entity = shelterPhotoList.get(0);
		photoService.deletePhotoByShelter(shelterEntity.getId(), entity.getId());
		assertNull(entityManager.find(PhotoEntity.class, entity.getId()));
	}

	@Test
	void testDeletePhotoNotAssociatedToShelter() {
		assertThrows(IllegalOperationException.class, () -> {
			photoService.deletePhotoByShelter(shelterEntity.getId(), petPhotoList.get(0).getId());
		});
	}
}
