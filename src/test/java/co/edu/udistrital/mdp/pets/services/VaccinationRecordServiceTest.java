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
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.VaccinationRecordEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(VaccinationRecordService.class)
class VaccinationRecordServiceTest {

    @Autowired
    private VaccinationRecordService vaccinationRecordService;

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
        entityManager.getEntityManager().createQuery("delete from VaccinationRecordEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
    }

    private void insertData() {
        shelterEntity = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelterEntity);

        for (int i = 0; i < 3; i++) {
            PetEntity pet = factory.manufacturePojo(PetEntity.class);
            pet.setAge(2);
            pet.setName("Pet " + i);
            pet.setSpecies("Dog");
            pet.setShelter(shelterEntity);
            entityManager.persist(pet);
            petList.add(pet);
        }
    }

    @Test
    void testCreateVaccinationRecord() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = petList.get(0);

        VaccinationRecordEntity newRecord = factory.manufacturePojo(VaccinationRecordEntity.class);
        newRecord.setVaccinesApplied("Rabia");
        newRecord.setNextDueDate(new java.util.Date());

        VaccinationRecordEntity result = vaccinationRecordService.createVaccinationRecord(pet.getId(), newRecord);
        assertNotNull(result);

        VaccinationRecordEntity entity = entityManager.find(VaccinationRecordEntity.class, result.getId());
        assertEquals("Rabia", entity.getVaccinesApplied());

        PetEntity updatedPet = entityManager.find(PetEntity.class, pet.getId());
        assertNotNull(updatedPet.getVaccinationRecord());
        assertEquals(result.getId(), updatedPet.getVaccinationRecord().getId());
    }

    @Test
    void testCreateVaccinationRecordWithInvalidPet() {
        assertThrows(EntityNotFoundException.class, () -> {
            VaccinationRecordEntity newRecord = factory.manufacturePojo(VaccinationRecordEntity.class);
            newRecord.setVaccinesApplied("Rabia");
            vaccinationRecordService.createVaccinationRecord(0L, newRecord);
        });
    }

    @Test
    void testCreateVaccinationRecordAlreadyExists() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity pet = petList.get(0);

            VaccinationRecordEntity existing = factory.manufacturePojo(VaccinationRecordEntity.class);
            existing.setVaccinesApplied("Rabia");
            entityManager.persist(existing);

            pet.setVaccinationRecord(existing);
            entityManager.persist(pet);
            entityManager.flush();

            VaccinationRecordEntity newRecord = factory.manufacturePojo(VaccinationRecordEntity.class);
            newRecord.setVaccinesApplied("Moquillo");
            vaccinationRecordService.createVaccinationRecord(pet.getId(), newRecord);
        });
    }

    @Test
    void testCreateVaccinationRecordWithNoValidVaccines() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity pet = petList.get(1);
            VaccinationRecordEntity newRecord = factory.manufacturePojo(VaccinationRecordEntity.class);
            newRecord.setVaccinesApplied("");
            vaccinationRecordService.createVaccinationRecord(pet.getId(), newRecord);
        });
    }

    @Test
    void testGetVaccinationRecord() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = petList.get(0);

        VaccinationRecordEntity record = factory.manufacturePojo(VaccinationRecordEntity.class);
        record.setVaccinesApplied("Rabia");
        entityManager.persist(record);

        pet.setVaccinationRecord(record);
        entityManager.persist(pet);
        entityManager.flush();

        VaccinationRecordEntity result = vaccinationRecordService.getVaccinationRecord(pet.getId());
        assertNotNull(result);
        assertEquals(record.getId(), result.getId());
        assertEquals("Rabia", result.getVaccinesApplied());
    }

    @Test
    void testGetVaccinationRecordWithInvalidPet() {
        assertThrows(EntityNotFoundException.class, () -> {
            vaccinationRecordService.getVaccinationRecord(0L);
        });
    }

    @Test
    void testGetVaccinationRecordNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            PetEntity pet = petList.get(2);
            vaccinationRecordService.getVaccinationRecord(pet.getId());
        });
    }

    @Test
    void testUpdateVaccinationRecord() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = petList.get(0);

        VaccinationRecordEntity record = factory.manufacturePojo(VaccinationRecordEntity.class);
        record.setVaccinesApplied("Rabia");
        entityManager.persist(record);

        pet.setVaccinationRecord(record);
        entityManager.persist(pet);
        entityManager.flush();

        VaccinationRecordEntity pojoEntity = factory.manufacturePojo(VaccinationRecordEntity.class);
        pojoEntity.setVaccinesApplied("Moquillo");

        VaccinationRecordEntity result = vaccinationRecordService.updateVaccinationRecord(pet.getId(), pojoEntity);
        assertNotNull(result);
        assertEquals(record.getId(), result.getId());
        assertEquals("Moquillo", result.getVaccinesApplied());
    }

    @Test
    void testUpdateVaccinationRecordWithInvalidPet() {
        assertThrows(EntityNotFoundException.class, () -> {
            VaccinationRecordEntity pojoEntity = factory.manufacturePojo(VaccinationRecordEntity.class);
            pojoEntity.setVaccinesApplied("Rabia");
            vaccinationRecordService.updateVaccinationRecord(0L, pojoEntity);
        });
    }

    @Test
    void testUpdateVaccinationRecordNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            PetEntity pet = petList.get(1);
            VaccinationRecordEntity pojoEntity = factory.manufacturePojo(VaccinationRecordEntity.class);
            pojoEntity.setVaccinesApplied("Rabia");
            vaccinationRecordService.updateVaccinationRecord(pet.getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateVaccinationRecordWithNoValidVaccines() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity pet = petList.get(0);

            VaccinationRecordEntity record = factory.manufacturePojo(VaccinationRecordEntity.class);
            record.setVaccinesApplied("Rabia");
            entityManager.persist(record);

            pet.setVaccinationRecord(record);
            entityManager.persist(pet);
            entityManager.flush();

            VaccinationRecordEntity pojoEntity = factory.manufacturePojo(VaccinationRecordEntity.class);
            pojoEntity.setVaccinesApplied(null);
            vaccinationRecordService.updateVaccinationRecord(pet.getId(), pojoEntity);
        });
    }

    @Test
    void testDeleteVaccinationRecord() throws EntityNotFoundException {
        PetEntity pet = petList.get(0);

        VaccinationRecordEntity record = factory.manufacturePojo(VaccinationRecordEntity.class);
        record.setVaccinesApplied("Rabia");
        entityManager.persist(record);

        pet.setVaccinationRecord(record);
        entityManager.persist(pet);
        entityManager.flush();

        Long recordId = record.getId();

        vaccinationRecordService.deleteVaccinationRecord(pet.getId());

        entityManager.flush();
        entityManager.clear();

        PetEntity updatedPet = entityManager.find(PetEntity.class, pet.getId());
        assertNull(updatedPet.getVaccinationRecord());
        assertNull(entityManager.find(VaccinationRecordEntity.class, recordId));
    }

    @Test
    void testDeleteVaccinationRecordWithInvalidPet() {
        assertThrows(EntityNotFoundException.class, () -> {
            vaccinationRecordService.deleteVaccinationRecord(0L);
        });
    }

    @Test
    void testDeleteVaccinationRecordNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            PetEntity pet = petList.get(1);
            vaccinationRecordService.deleteVaccinationRecord(pet.getId());
        });
    }
}