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

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ReturnRecordEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.TrialCohabitationEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(AdoptionService.class)
class AdoptionServiceTest {

	@Autowired
	private AdoptionService adoptionService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<AdoptionEntity> adoptionList = new ArrayList<>();
	private ShelterEntity shelterEntity;
	private AdopterEntity adopterEntity;
	private VeterinarianEntity veterinarianEntity;
	private PetEntity availablePetEntity;

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from ReturnRecordEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from TrialCohabitationEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from AdopterEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from VeterinarianEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
	}

	private void insertData() {
		shelterEntity = factory.manufacturePojo(ShelterEntity.class);
		entityManager.persist(shelterEntity);

		adopterEntity = factory.manufacturePojo(AdopterEntity.class);
		entityManager.persist(adopterEntity);

		veterinarianEntity = factory.manufacturePojo(VeterinarianEntity.class);
		veterinarianEntity.setShelter(shelterEntity);
		entityManager.persist(veterinarianEntity);

		for (int i = 0; i < 3; i++) {
			PetEntity petEntity = factory.manufacturePojo(PetEntity.class);
			petEntity.setShelter(shelterEntity);
			petEntity.setAvailable(false);
			entityManager.persist(petEntity);

			AdoptionEntity adoptionEntity = factory.manufacturePojo(AdoptionEntity.class);
			adoptionEntity.setDate(new Date());
			adoptionEntity.setStatus("IN_PROGRESS");
			adoptionEntity.setPet(petEntity);
			adoptionEntity.setAdopter(adopterEntity);
			adoptionEntity.setResponsibleVeterinarian(veterinarianEntity);
			adoptionEntity.setReturnRecord(null);
			adoptionEntity.setTrialCohabitation(null);
			entityManager.persist(adoptionEntity);
			adoptionList.add(adoptionEntity);
		}

		availablePetEntity = factory.manufacturePojo(PetEntity.class);
		availablePetEntity.setShelter(shelterEntity);
		availablePetEntity.setAvailable(true);
		entityManager.persist(availablePetEntity);
	}

	// createAdoption

	@Test
	void testCreateAdoption() throws EntityNotFoundException, IllegalOperationException {
		AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
		newEntity.setDate(new Date());
		newEntity.setStatus("APPROVED");
		newEntity.setPet(availablePetEntity);
		newEntity.setAdopter(adopterEntity);
		newEntity.setResponsibleVeterinarian(veterinarianEntity);

		AdoptionEntity result = adoptionService.createAdoption(newEntity);
		assertNotNull(result);

		AdoptionEntity entity = entityManager.find(AdoptionEntity.class, result.getId());
		assertEquals(availablePetEntity.getId(), entity.getPet().getId());
		assertEquals(adopterEntity.getId(), entity.getAdopter().getId());
		assertEquals(veterinarianEntity.getId(), entity.getResponsibleVeterinarian().getId());
		assertEquals("APPROVED", entity.getStatus());

		PetEntity petEntity = entityManager.find(PetEntity.class, availablePetEntity.getId());
		assertFalse(petEntity.isAvailable());
	}

	@Test
	void testCreateAdoptionAssignsDefaultStatus() throws EntityNotFoundException, IllegalOperationException {
		AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
		newEntity.setDate(new Date());
		newEntity.setStatus(null);
		newEntity.setPet(availablePetEntity);
		newEntity.setAdopter(adopterEntity);
		newEntity.setResponsibleVeterinarian(veterinarianEntity);

		AdoptionEntity result = adoptionService.createAdoption(newEntity);
		assertEquals("IN_PROGRESS", result.getStatus());
	}

	@Test
	void testCreateAdoptionWithNoPet() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
			newEntity.setDate(new Date());
			newEntity.setPet(null);
			newEntity.setAdopter(adopterEntity);
			newEntity.setResponsibleVeterinarian(veterinarianEntity);
			adoptionService.createAdoption(newEntity);
		});
	}

	@Test
	void testCreateAdoptionWithInvalidPet() {
		assertThrows(EntityNotFoundException.class, () -> {
			AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
			newEntity.setDate(new Date());
			PetEntity invalidPet = new PetEntity();
			invalidPet.setId(0L);
			newEntity.setPet(invalidPet);
			newEntity.setAdopter(adopterEntity);
			newEntity.setResponsibleVeterinarian(veterinarianEntity);
			adoptionService.createAdoption(newEntity);
		});
	}

	@Test
	void testCreateAdoptionWithNoAdopter() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
			newEntity.setDate(new Date());
			newEntity.setPet(availablePetEntity);
			newEntity.setAdopter(null);
			newEntity.setResponsibleVeterinarian(veterinarianEntity);
			adoptionService.createAdoption(newEntity);
		});
	}

	@Test
	void testCreateAdoptionWithInvalidAdopter() {
		assertThrows(EntityNotFoundException.class, () -> {
			AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
			newEntity.setDate(new Date());
			newEntity.setPet(availablePetEntity);
			AdopterEntity invalidAdopter = new AdopterEntity();
			invalidAdopter.setId(0L);
			newEntity.setAdopter(invalidAdopter);
			newEntity.setResponsibleVeterinarian(veterinarianEntity);
			adoptionService.createAdoption(newEntity);
		});
	}

	@Test
	void testCreateAdoptionWithNoVeterinarian() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
			newEntity.setDate(new Date());
			newEntity.setPet(availablePetEntity);
			newEntity.setAdopter(adopterEntity);
			newEntity.setResponsibleVeterinarian(null);
			adoptionService.createAdoption(newEntity);
		});
	}

	@Test
	void testCreateAdoptionWithInvalidVeterinarian() {
		assertThrows(EntityNotFoundException.class, () -> {
			AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
			newEntity.setDate(new Date());
			newEntity.setPet(availablePetEntity);
			newEntity.setAdopter(adopterEntity);
			VeterinarianEntity invalidVet = new VeterinarianEntity();
			invalidVet.setId(0L);
			newEntity.setResponsibleVeterinarian(invalidVet);
			adoptionService.createAdoption(newEntity);
		});
	}

	@Test
	void testCreateAdoptionWithNoDate() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
			newEntity.setDate(null);
			newEntity.setPet(availablePetEntity);
			newEntity.setAdopter(adopterEntity);
			newEntity.setResponsibleVeterinarian(veterinarianEntity);
			adoptionService.createAdoption(newEntity);
		});
	}

	@Test
	void testCreateAdoptionWithUnavailablePet() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
			newEntity.setDate(new Date());
			newEntity.setPet(adoptionList.get(0).getPet()); // ya no está disponible
			newEntity.setAdopter(adopterEntity);
			newEntity.setResponsibleVeterinarian(veterinarianEntity);
			adoptionService.createAdoption(newEntity);
		});
	}

	// getAdoptions / getAdoption

	@Test
	void testGetAdoptions() {
		List<AdoptionEntity> list = adoptionService.getAdoptions();
		assertEquals(adoptionList.size(), list.size());
	}

	@Test
	void testGetAdoption() throws EntityNotFoundException {
		AdoptionEntity entity = adoptionList.get(0);
		AdoptionEntity result = adoptionService.getAdoption(entity.getId());
		assertNotNull(result);
		assertEquals(entity.getId(), result.getId());
		assertEquals(entity.getStatus(), result.getStatus());
	}

	@Test
	void testGetInvalidAdoption() {
		assertThrows(EntityNotFoundException.class, () -> {
			adoptionService.getAdoption(0L);
		});
	}

	// updateAdoption

	@Test
	void testUpdateAdoption() throws EntityNotFoundException, IllegalOperationException {
		AdoptionEntity entity = adoptionList.get(0);
		AdoptionEntity pojoEntity = factory.manufacturePojo(AdoptionEntity.class);
		pojoEntity.setId(entity.getId());
		pojoEntity.setStatus("APPROVED");
		pojoEntity.setDate(entity.getDate());

		adoptionService.updateAdoption(entity.getId(), pojoEntity);

		AdoptionEntity resp = entityManager.find(AdoptionEntity.class, entity.getId());
		assertEquals("APPROVED", resp.getStatus());
	}

	@Test
	void testUpdateAdoptionDoesNotReassignPetAdopterOrVeterinarian()
			throws EntityNotFoundException, IllegalOperationException {
		AdoptionEntity entity = adoptionList.get(0);

		AdopterEntity anotherAdopter = factory.manufacturePojo(AdopterEntity.class);
		entityManager.persist(anotherAdopter);

		VeterinarianEntity anotherVet = factory.manufacturePojo(VeterinarianEntity.class);
		entityManager.persist(anotherVet);

		AdoptionEntity pojoEntity = factory.manufacturePojo(AdoptionEntity.class);
		pojoEntity.setId(entity.getId());
		pojoEntity.setStatus("APPROVED");
		pojoEntity.setDate(entity.getDate());
		pojoEntity.setPet(availablePetEntity);
		pojoEntity.setAdopter(anotherAdopter);
		pojoEntity.setResponsibleVeterinarian(anotherVet);

		adoptionService.updateAdoption(entity.getId(), pojoEntity);

		AdoptionEntity resp = entityManager.find(AdoptionEntity.class, entity.getId());
		assertEquals(entity.getPet().getId(), resp.getPet().getId());
		assertEquals(entity.getAdopter().getId(), resp.getAdopter().getId());
		assertEquals(entity.getResponsibleVeterinarian().getId(), resp.getResponsibleVeterinarian().getId());
	}

	@Test
	void testUpdateInvalidAdoption() {
		assertThrows(EntityNotFoundException.class, () -> {
			AdoptionEntity pojoEntity = factory.manufacturePojo(AdoptionEntity.class);
			pojoEntity.setId(0L);
			pojoEntity.setStatus("APPROVED");
			adoptionService.updateAdoption(0L, pojoEntity);
		});
	}

	@Test
	void testUpdateAdoptionWithNoValidStatus() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionEntity entity = adoptionList.get(0);
			AdoptionEntity pojoEntity = factory.manufacturePojo(AdoptionEntity.class);
			pojoEntity.setId(entity.getId());
			pojoEntity.setStatus(null);
			adoptionService.updateAdoption(entity.getId(), pojoEntity);
		});
	}

	@Test
	void testUpdateAdoptionWithEmptyStatus() {
		assertThrows(IllegalOperationException.class, () -> {
			AdoptionEntity entity = adoptionList.get(0);
			AdoptionEntity pojoEntity = factory.manufacturePojo(AdoptionEntity.class);
			pojoEntity.setId(entity.getId());
			pojoEntity.setStatus("");
			adoptionService.updateAdoption(entity.getId(), pojoEntity);
		});
	}

	// deleteAdoption

	@Test
	void testDeleteAdoption() throws EntityNotFoundException {
		AdoptionEntity entity = adoptionList.get(0);
		adoptionService.deleteAdoption(entity.getId());

		assertNull(entityManager.find(AdoptionEntity.class, entity.getId()));
		PetEntity petEntity = entityManager.find(PetEntity.class, entity.getPet().getId());
		assertTrue(petEntity.isAvailable());
	}

	@Test
	void testDeleteInvalidAdoption() {
		assertThrows(EntityNotFoundException.class, () -> {
			adoptionService.deleteAdoption(0L);
		});
	}

	@Test
	void testDeleteAdoptionCascadesReturnRecordAndTrialCohabitation() throws EntityNotFoundException {
		ReturnRecordEntity returnRecordEntity = factory.manufacturePojo(ReturnRecordEntity.class);
		entityManager.persist(returnRecordEntity);

		TrialCohabitationEntity trialCohabitationEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
		entityManager.persist(trialCohabitationEntity);

		AdoptionEntity entity = adoptionList.get(1);
		entity.setReturnRecord(returnRecordEntity);
		entity.setTrialCohabitation(trialCohabitationEntity);
		entityManager.getEntityManager().flush();

		Long returnRecordId = returnRecordEntity.getId();
		Long trialCohabitationId = trialCohabitationEntity.getId();

		adoptionService.deleteAdoption(entity.getId());

		assertNull(entityManager.find(ReturnRecordEntity.class, returnRecordId));
		assertNull(entityManager.find(TrialCohabitationEntity.class, trialCohabitationId));
	}
}