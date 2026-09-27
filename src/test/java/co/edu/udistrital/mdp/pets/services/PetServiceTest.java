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

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.PhotoEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@DataJpaTest
@Transactional
@Import(PetService.class)
class PetServiceTest {

	@Autowired
	private PetService petService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<PetEntity> petList = new ArrayList<>();
	private ShelterEntity shelterEntity;

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
	}

	private void insertData() {
		shelterEntity = factory.manufacturePojo(ShelterEntity.class);
		entityManager.persist(shelterEntity);

		for (int i = 0; i < 3; i++) {
			PetEntity petEntity = factory.manufacturePojo(PetEntity.class);
			petEntity.setAge(2);
			petEntity.setShelter(shelterEntity);
			entityManager.persist(petEntity);
			petList.add(petEntity);
		}
	}
    //test de creacion de una pet, validando que se cree correctamente y que se asocie a un shelter existente.
	@Test
	void testCreatePet() throws IllegalOperationException {
		PetEntity newEntity = factory.manufacturePojo(PetEntity.class);
		newEntity.setAge(3);
		newEntity.setShelter(shelterEntity);

		PetEntity result = petService.createPet(newEntity);
		assertNotNull(result);
		PetEntity entity = entityManager.find(PetEntity.class, result.getId());
		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getName(), entity.getName());
		assertEquals(newEntity.getSpecies(), entity.getSpecies());
	}

	@Test
	void testCreatePetWithNoShelter() {
		assertThrows(IllegalOperationException.class, () -> {
			PetEntity newEntity = factory.manufacturePojo(PetEntity.class);
			newEntity.setAge(1);
			newEntity.setShelter(null);
			petService.createPet(newEntity);
		});
	}

	@Test
	void testCreatePetWithInvalidShelter() {
		assertThrows(IllegalOperationException.class, () -> {
			PetEntity newEntity = factory.manufacturePojo(PetEntity.class);
			newEntity.setAge(1);
			ShelterEntity invalidShelter = new ShelterEntity();
			invalidShelter.setId(0L);
			newEntity.setShelter(invalidShelter);
			petService.createPet(newEntity);
		});
	}

	@Test
	void testCreatePetWithNoValidName() {
		assertThrows(IllegalOperationException.class, () -> {
			PetEntity newEntity = factory.manufacturePojo(PetEntity.class);
			newEntity.setAge(1);
			newEntity.setShelter(shelterEntity);
			newEntity.setName("");
			petService.createPet(newEntity);
		});
	}

	@Test
	void testCreatePetWithNegativeAge() {
		assertThrows(IllegalOperationException.class, () -> {
			PetEntity newEntity = factory.manufacturePojo(PetEntity.class);
			newEntity.setAge(-1);
			newEntity.setShelter(shelterEntity);
			petService.createPet(newEntity);
		});
	}

	@Test
	void testGetPets() {
		List<PetEntity> list = petService.getPets();
		assertEquals(petList.size(), list.size());
	}

	@Test
	void testGetPet() throws EntityNotFoundException {
		PetEntity entity = petList.get(0);
		PetEntity resultEntity = petService.getPet(entity.getId());
		assertNotNull(resultEntity);
		assertEquals(entity.getId(), resultEntity.getId());
		assertEquals(entity.getName(), resultEntity.getName());
	}

	@Test
	void testGetInvalidPet() {
		assertThrows(EntityNotFoundException.class, () -> {
			petService.getPet(0L);
		});
	}

	@Test
	void testUpdatePet() throws EntityNotFoundException, IllegalOperationException {
		PetEntity entity = petList.get(0);
		PetEntity pojoEntity = factory.manufacturePojo(PetEntity.class);
		pojoEntity.setId(entity.getId());
		pojoEntity.setAge(4);

		petService.updatePet(entity.getId(), pojoEntity);

		PetEntity resp = entityManager.find(PetEntity.class, entity.getId());
		assertEquals(pojoEntity.getName(), resp.getName());
		assertEquals(pojoEntity.getSpecies(), resp.getSpecies());
		assertEquals(shelterEntity.getId(), resp.getShelter().getId());
	}

	@Test
	void testUpdateInvalidPet() {
		assertThrows(EntityNotFoundException.class, () -> {
			PetEntity pojoEntity = factory.manufacturePojo(PetEntity.class);
			pojoEntity.setId(0L);
			pojoEntity.setAge(1);
			petService.updatePet(0L, pojoEntity);
		});
	}

	@Test
	void testUpdatePetWithNegativeAge() {
		assertThrows(IllegalOperationException.class, () -> {
			PetEntity entity = petList.get(0);
			PetEntity pojoEntity = factory.manufacturePojo(PetEntity.class);
			pojoEntity.setId(entity.getId());
			pojoEntity.setAge(-5);
			petService.updatePet(entity.getId(), pojoEntity);
		});
	}

	@Test
	void testUpdatePetWithNoValidName() {
		assertThrows(IllegalOperationException.class, () -> {
			PetEntity entity = petList.get(0);
			PetEntity pojoEntity = factory.manufacturePojo(PetEntity.class);
			pojoEntity.setId(entity.getId());
			pojoEntity.setAge(1);
			pojoEntity.setName(null);
			petService.updatePet(entity.getId(), pojoEntity);
		});
	}

	@Test
	void testDeletePet() throws EntityNotFoundException, IllegalOperationException {
		PetEntity entity = petList.get(1);
		petService.deletePet(entity.getId());
		PetEntity deleted = entityManager.find(PetEntity.class, entity.getId());
		assertNull(deleted);
	}

	@Test
	void testDeleteInvalidPet() {
		assertThrows(EntityNotFoundException.class, () -> {
			petService.deletePet(0L);
		});
	}

	@Test
	void testDeletePetWithAdoptions() {
		assertThrows(IllegalOperationException.class, () -> {
			PetEntity entity = petList.get(0);
			AdoptionEntity adoptionEntity = factory.manufacturePojo(AdoptionEntity.class);
			adoptionEntity.setPet(entity);
			entityManager.persist(adoptionEntity);
			entity.getAdoptions().add(adoptionEntity);
			petService.deletePet(entity.getId());
		});
	}

	@Test
	void testDeletePetWithPhotos() {
		assertThrows(IllegalOperationException.class, () -> {
			PetEntity entity = petList.get(0);
			PhotoEntity photoEntity = factory.manufacturePojo(PhotoEntity.class);
			photoEntity.setPet(entity);
			entityManager.persist(photoEntity);
			entity.getPhotos().add(photoEntity);
			petService.deletePet(entity.getId());
		});
	}
}
