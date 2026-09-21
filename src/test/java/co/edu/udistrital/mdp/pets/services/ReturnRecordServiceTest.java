package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

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
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(ReturnRecordService.class)
class ReturnRecordServiceTest {

	@Autowired
	private ReturnRecordService returnRecordService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private ShelterEntity shelterEntity;
	private AdopterEntity adopterEntity;
	private VeterinarianEntity veterinarianEntity;

	private AdoptionEntity adoptionWithReturnRecord;
	private ReturnRecordEntity returnRecordEntity;

	private AdoptionEntity adoptionWithoutReturnRecord;

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from ReturnRecordEntity").executeUpdate();
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

		// adopción ya devuelta, con returnRecord existente
		PetEntity petWithReturnRecord = factory.manufacturePojo(PetEntity.class);
		petWithReturnRecord.setShelter(shelterEntity);
		petWithReturnRecord.setAvailable(true);
		entityManager.persist(petWithReturnRecord);

		returnRecordEntity = factory.manufacturePojo(ReturnRecordEntity.class);
		entityManager.persist(returnRecordEntity);

		adoptionWithReturnRecord = factory.manufacturePojo(AdoptionEntity.class);
		adoptionWithReturnRecord.setDate(new Date());
		adoptionWithReturnRecord.setStatus("RETURNED");
		adoptionWithReturnRecord.setPet(petWithReturnRecord);
		adoptionWithReturnRecord.setAdopter(adopterEntity);
		adoptionWithReturnRecord.setResponsibleVeterinarian(veterinarianEntity);
		adoptionWithReturnRecord.setReturnRecord(returnRecordEntity);
		adoptionWithReturnRecord.setTrialCohabitation(null);
		entityManager.persist(adoptionWithReturnRecord);

		// adopción activa, sin returnRecord todavía
		PetEntity petWithoutReturnRecord = factory.manufacturePojo(PetEntity.class);
		petWithoutReturnRecord.setShelter(shelterEntity);
		petWithoutReturnRecord.setAvailable(false);
		entityManager.persist(petWithoutReturnRecord);

		adoptionWithoutReturnRecord = factory.manufacturePojo(AdoptionEntity.class);
		adoptionWithoutReturnRecord.setDate(new Date());
		adoptionWithoutReturnRecord.setStatus("IN_PROGRESS");
		adoptionWithoutReturnRecord.setPet(petWithoutReturnRecord);
		adoptionWithoutReturnRecord.setAdopter(adopterEntity);
		adoptionWithoutReturnRecord.setResponsibleVeterinarian(veterinarianEntity);
		adoptionWithoutReturnRecord.setReturnRecord(null);
		adoptionWithoutReturnRecord.setTrialCohabitation(null);
		entityManager.persist(adoptionWithoutReturnRecord);
	}

	// createReturnRecord

	@Test
	void testCreateReturnRecord() throws EntityNotFoundException, IllegalOperationException {
		ReturnRecordEntity newEntity = factory.manufacturePojo(ReturnRecordEntity.class);
		newEntity.setDate(new Date());
		newEntity.setReason("No compatibility with other pets");

		ReturnRecordEntity result = returnRecordService.createReturnRecord(adoptionWithoutReturnRecord.getId(),
				newEntity);
		assertNotNull(result);

		AdoptionEntity adoptionEntity = entityManager.find(AdoptionEntity.class, adoptionWithoutReturnRecord.getId());
		assertNotNull(adoptionEntity.getReturnRecord());
		assertEquals(newEntity.getReason(), adoptionEntity.getReturnRecord().getReason());
		assertEquals("RETURNED", adoptionEntity.getStatus());

		PetEntity petEntity = entityManager.find(PetEntity.class, adoptionWithoutReturnRecord.getPet().getId());
		assertTrue(petEntity.isAvailable());
	}

	@Test
	void testCreateReturnRecordWithInvalidAdoption() {
		assertThrows(EntityNotFoundException.class, () -> {
			ReturnRecordEntity newEntity = factory.manufacturePojo(ReturnRecordEntity.class);
			newEntity.setDate(new Date());
			newEntity.setReason("reason");
			returnRecordService.createReturnRecord(0L, newEntity);
		});
	}

	@Test
	void testCreateReturnRecordAlreadyExists() {
		assertThrows(IllegalOperationException.class, () -> {
			ReturnRecordEntity newEntity = factory.manufacturePojo(ReturnRecordEntity.class);
			newEntity.setDate(new Date());
			newEntity.setReason("reason");
			returnRecordService.createReturnRecord(adoptionWithReturnRecord.getId(), newEntity);
		});
	}

	@Test
	void testCreateReturnRecordWithNoDate() {
		assertThrows(IllegalOperationException.class, () -> {
			ReturnRecordEntity newEntity = factory.manufacturePojo(ReturnRecordEntity.class);
			newEntity.setDate(null);
			newEntity.setReason("reason");
			returnRecordService.createReturnRecord(adoptionWithoutReturnRecord.getId(), newEntity);
		});
	}

	@Test
	void testCreateReturnRecordWithNoReason() {
		assertThrows(IllegalOperationException.class, () -> {
			ReturnRecordEntity newEntity = factory.manufacturePojo(ReturnRecordEntity.class);
			newEntity.setDate(new Date());
			newEntity.setReason(null);
			returnRecordService.createReturnRecord(adoptionWithoutReturnRecord.getId(), newEntity);
		});
	}

	@Test
	void testCreateReturnRecordWithEmptyReason() {
		assertThrows(IllegalOperationException.class, () -> {
			ReturnRecordEntity newEntity = factory.manufacturePojo(ReturnRecordEntity.class);
			newEntity.setDate(new Date());
			newEntity.setReason("");
			returnRecordService.createReturnRecord(adoptionWithoutReturnRecord.getId(), newEntity);
		});
	}

	// getReturnRecord

	@Test
	void testGetReturnRecord() throws EntityNotFoundException {
		ReturnRecordEntity result = returnRecordService.getReturnRecord(adoptionWithReturnRecord.getId());
		assertNotNull(result);
		assertEquals(returnRecordEntity.getId(), result.getId());
		assertEquals(returnRecordEntity.getReason(), result.getReason());
	}

	@Test
	void testGetReturnRecordWithInvalidAdoption() {
		assertThrows(EntityNotFoundException.class, () -> {
			returnRecordService.getReturnRecord(0L);
		});
	}

	@Test
	void testGetReturnRecordNotFound() {
		assertThrows(EntityNotFoundException.class, () -> {
			returnRecordService.getReturnRecord(adoptionWithoutReturnRecord.getId());
		});
	}

	// updateReturnRecord

	@Test
	void testUpdateReturnRecord() throws EntityNotFoundException, IllegalOperationException {
		ReturnRecordEntity pojoEntity = factory.manufacturePojo(ReturnRecordEntity.class);
		pojoEntity.setDate(new Date());
		pojoEntity.setReason("Updated reason");

		returnRecordService.updateReturnRecord(adoptionWithReturnRecord.getId(), pojoEntity);

		AdoptionEntity adoptionEntity = entityManager.find(AdoptionEntity.class, adoptionWithReturnRecord.getId());
		assertEquals("Updated reason", adoptionEntity.getReturnRecord().getReason());
		assertEquals(returnRecordEntity.getId(), adoptionEntity.getReturnRecord().getId());
	}

	@Test
	void testUpdateReturnRecordWithInvalidAdoption() {
		assertThrows(EntityNotFoundException.class, () -> {
			ReturnRecordEntity pojoEntity = factory.manufacturePojo(ReturnRecordEntity.class);
			pojoEntity.setDate(new Date());
			pojoEntity.setReason("reason");
			returnRecordService.updateReturnRecord(0L, pojoEntity);
		});
	}

	@Test
	void testUpdateReturnRecordNotFound() {
		assertThrows(EntityNotFoundException.class, () -> {
			ReturnRecordEntity pojoEntity = factory.manufacturePojo(ReturnRecordEntity.class);
			pojoEntity.setDate(new Date());
			pojoEntity.setReason("reason");
			returnRecordService.updateReturnRecord(adoptionWithoutReturnRecord.getId(), pojoEntity);
		});
	}

	@Test
	void testUpdateReturnRecordWithNoDate() {
		assertThrows(IllegalOperationException.class, () -> {
			ReturnRecordEntity pojoEntity = factory.manufacturePojo(ReturnRecordEntity.class);
			pojoEntity.setDate(null);
			pojoEntity.setReason("reason");
			returnRecordService.updateReturnRecord(adoptionWithReturnRecord.getId(), pojoEntity);
		});
	}

	// deleteReturnRecord

	@Test
	void testDeleteReturnRecord() throws EntityNotFoundException {
		Long returnRecordId = returnRecordEntity.getId();
		returnRecordService.deleteReturnRecord(adoptionWithReturnRecord.getId());
		// orphanRemoval solo se aplica al hacer flush, no al instante
		entityManager.getEntityManager().flush();

		assertNull(entityManager.find(ReturnRecordEntity.class, returnRecordId));
		AdoptionEntity adoptionEntity = entityManager.find(AdoptionEntity.class, adoptionWithReturnRecord.getId());
		assertNull(adoptionEntity.getReturnRecord());
	}

	@Test
	void testDeleteReturnRecordWithInvalidAdoption() {
		assertThrows(EntityNotFoundException.class, () -> {
			returnRecordService.deleteReturnRecord(0L);
		});
	}

	@Test
	void testDeleteReturnRecordNotFound() {
		assertThrows(EntityNotFoundException.class, () -> {
			returnRecordService.deleteReturnRecord(adoptionWithoutReturnRecord.getId());
		});
	}
}