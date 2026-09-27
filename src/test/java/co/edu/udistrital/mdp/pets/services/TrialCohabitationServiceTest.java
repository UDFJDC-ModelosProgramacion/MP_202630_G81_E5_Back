package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.TrialCohabitationEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(TrialCohabitationService.class)
class TrialCohabitationServiceTest {

	@Autowired
	private TrialCohabitationService trialCohabitationService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();


	private AdoptionEntity adoptionWithTrial;

	private AdoptionEntity adoptionWithoutTrial;

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from TrialCohabitationEntity").executeUpdate();
	}

	private void insertData() {
		TrialCohabitationEntity trialEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
		trialEntity.setStartDate(daysFromToday(-10));
		trialEntity.setEndDate(daysFromToday(-2));

		adoptionWithTrial = factory.manufacturePojo(AdoptionEntity.class);
		adoptionWithTrial.setTrialCohabitation(trialEntity);
		entityManager.persist(adoptionWithTrial);


		adoptionWithoutTrial = factory.manufacturePojo(AdoptionEntity.class);
		adoptionWithoutTrial.setTrialCohabitation(null);
		entityManager.persist(adoptionWithoutTrial);
	}


	private Date daysFromToday(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}


	private TrialCohabitationEntity validTrial() {
		TrialCohabitationEntity trialEntity = factory.manufacturePojo(TrialCohabitationEntity.class);
		trialEntity.setStartDate(daysFromToday(1));
		trialEntity.setEndDate(daysFromToday(10));
		return trialEntity;
	}



	@Test
	void testCreateTrialCohabitation() throws EntityNotFoundException, IllegalOperationException {
		TrialCohabitationEntity newEntity = validTrial();

		TrialCohabitationEntity result = trialCohabitationService.createTrialCohabitation(adoptionWithoutTrial.getId(), newEntity);
		assertNotNull(result);
		assertNotNull(result.getId());


		TrialCohabitationEntity entity = entityManager.find(TrialCohabitationEntity.class, result.getId());
		assertNotNull(entity);
		assertEquals(newEntity.getOutcome(), entity.getOutcome());
		assertEquals(newEntity.getStartDate().getTime(), entity.getStartDate().getTime());
		assertEquals(newEntity.getEndDate().getTime(), entity.getEndDate().getTime());
		assertEquals(result.getId(), adoptionWithoutTrial.getTrialCohabitation().getId());
	}

	@Test
	void testCreateTrialCohabitationWithoutEndDate() throws EntityNotFoundException, IllegalOperationException {

		TrialCohabitationEntity newEntity = validTrial();
		newEntity.setEndDate(null);

		TrialCohabitationEntity result = trialCohabitationService.createTrialCohabitation(adoptionWithoutTrial.getId(), newEntity);
		assertNotNull(result);
		assertNull(result.getEndDate());
	}

	@Test
	void testCreateTrialCohabitationInvalidAdoption() {
		TrialCohabitationEntity newEntity = validTrial();
		assertThrows(EntityNotFoundException.class, () -> trialCohabitationService.createTrialCohabitation(0L, newEntity));
	}

	@Test
	void testCreateTrialCohabitationAlreadyExists() {
		TrialCohabitationEntity newEntity = validTrial();
		assertThrows(IllegalOperationException.class,
				() -> trialCohabitationService.createTrialCohabitation(adoptionWithTrial.getId(), newEntity));
	}

	@Test
	void testCreateTrialCohabitationWithoutStartDate() {
		TrialCohabitationEntity newEntity = validTrial();
		newEntity.setStartDate(null);
		assertThrows(IllegalOperationException.class,
				() -> trialCohabitationService.createTrialCohabitation(adoptionWithoutTrial.getId(), newEntity));
	}

	@Test
	void testCreateTrialCohabitationEndBeforeStart() {
		TrialCohabitationEntity newEntity = validTrial();
		newEntity.setStartDate(daysFromToday(10));
		newEntity.setEndDate(daysFromToday(1));
		assertThrows(IllegalOperationException.class,
				() -> trialCohabitationService.createTrialCohabitation(adoptionWithoutTrial.getId(), newEntity));
	}

	@Test
	void testCreateTrialCohabitationEndEqualsStart() {
		TrialCohabitationEntity newEntity = validTrial();
		Date sameDate = daysFromToday(3);
		newEntity.setStartDate(sameDate);
		newEntity.setEndDate(sameDate);
		assertThrows(IllegalOperationException.class,
				() -> trialCohabitationService.createTrialCohabitation(adoptionWithoutTrial.getId(), newEntity));
	}


	@Test
	void testGetTrialCohabitation() throws EntityNotFoundException {
		TrialCohabitationEntity expected = adoptionWithTrial.getTrialCohabitation();
		TrialCohabitationEntity result = trialCohabitationService.getTrialCohabitation(adoptionWithTrial.getId());
		assertNotNull(result);
		assertEquals(expected.getId(), result.getId());
		assertEquals(expected.getOutcome(), result.getOutcome());
	}

	@Test
	void testGetTrialCohabitationInvalidAdoption() {
		assertThrows(EntityNotFoundException.class, () -> trialCohabitationService.getTrialCohabitation(0L));
	}

	@Test
	void testGetTrialCohabitationOfAdoptionWithoutTrial() {
		assertThrows(EntityNotFoundException.class,
				() -> trialCohabitationService.getTrialCohabitation(adoptionWithoutTrial.getId()));
	}



	@Test
	void testUpdateTrialCohabitation() throws EntityNotFoundException, IllegalOperationException {
		Long oldId = adoptionWithTrial.getTrialCohabitation().getId();
		TrialCohabitationEntity pojoEntity = validTrial();

		trialCohabitationService.updateTrialCohabitation(adoptionWithTrial.getId(), pojoEntity);

	
		TrialCohabitationEntity resp = entityManager.find(TrialCohabitationEntity.class, oldId);
		assertNotNull(resp);
		assertEquals(pojoEntity.getOutcome(), resp.getOutcome());
		assertEquals(pojoEntity.getStartDate().getTime(), resp.getStartDate().getTime());
		assertEquals(pojoEntity.getEndDate().getTime(), resp.getEndDate().getTime());
	}

	@Test
	void testUpdateTrialCohabitationInvalidAdoption() {
		TrialCohabitationEntity pojoEntity = validTrial();
		assertThrows(EntityNotFoundException.class, () -> trialCohabitationService.updateTrialCohabitation(0L, pojoEntity));
	}

	@Test
	void testUpdateTrialCohabitationOfAdoptionWithoutTrial() {
		TrialCohabitationEntity pojoEntity = validTrial();
		assertThrows(EntityNotFoundException.class,
				() -> trialCohabitationService.updateTrialCohabitation(adoptionWithoutTrial.getId(), pojoEntity));
	}

	@Test
	void testUpdateTrialCohabitationWithoutStartDate() {
		TrialCohabitationEntity pojoEntity = validTrial();
		pojoEntity.setStartDate(null);
		assertThrows(IllegalOperationException.class,
				() -> trialCohabitationService.updateTrialCohabitation(adoptionWithTrial.getId(), pojoEntity));
	}

	@Test
	void testUpdateTrialCohabitationEndBeforeStart() {
		TrialCohabitationEntity pojoEntity = validTrial();
		pojoEntity.setStartDate(daysFromToday(10));
		pojoEntity.setEndDate(daysFromToday(1));
		assertThrows(IllegalOperationException.class,
				() -> trialCohabitationService.updateTrialCohabitation(adoptionWithTrial.getId(), pojoEntity));
	}



	@Test
	void testDeleteTrialCohabitation() throws EntityNotFoundException {
		Long trialId = adoptionWithTrial.getTrialCohabitation().getId();

		trialCohabitationService.deleteTrialCohabitation(adoptionWithTrial.getId());


		entityManager.flush();
		entityManager.clear();
		assertNull(entityManager.find(TrialCohabitationEntity.class, trialId));
		assertNull(entityManager.find(AdoptionEntity.class, adoptionWithTrial.getId()).getTrialCohabitation());
	}

	@Test
	void testDeleteTrialCohabitationInvalidAdoption() {
		assertThrows(EntityNotFoundException.class, () -> trialCohabitationService.deleteTrialCohabitation(0L));
	}

	@Test
	void testDeleteTrialCohabitationOfAdoptionWithoutTrial() {
		assertThrows(EntityNotFoundException.class,
				() -> trialCohabitationService.deleteTrialCohabitation(adoptionWithoutTrial.getId()));
	}

}
