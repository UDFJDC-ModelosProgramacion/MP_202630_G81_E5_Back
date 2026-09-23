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

import co.edu.udistrital.mdp.pets.entities.LifeEventEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(LifeEventService.class)
class LifeEventServiceTest {

    @Autowired
    private LifeEventService lifeEventService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<PetEntity> petList = new ArrayList<>();
    private List<LifeEventEntity> lifeEventList = new ArrayList<>();
    private ShelterEntity shelterEntity;
    private VeterinarianEntity veterinarianEntity;

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from LifeEventEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from VeterinarianEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
    }

    private void insertData() {
        shelterEntity = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelterEntity);

        veterinarianEntity = factory.manufacturePojo(VeterinarianEntity.class);
        veterinarianEntity.setShelter(shelterEntity);
        veterinarianEntity.setName("Vet Principal");
        veterinarianEntity.setEmail("vet@test.com");
        veterinarianEntity.setSpecialty("General");
        entityManager.persist(veterinarianEntity);

        for (int i = 0; i < 3; i++) {
            PetEntity pet = factory.manufacturePojo(PetEntity.class);
            pet.setAge(2);
            pet.setName("Pet " + i);
            pet.setSpecies("Dog");
            pet.setShelter(shelterEntity);
            entityManager.persist(pet);
            petList.add(pet);
        }

        // Eventos asociados al primer pet
        for (int i = 0; i < 3; i++) {
            LifeEventEntity event = factory.manufacturePojo(LifeEventEntity.class);
            event.setType("Checkup " + i);
            event.setDate(new Date());
            event.setPet(petList.get(0));
            event.setVeterinarian(veterinarianEntity);
            entityManager.persist(event);

            // Sincroniza el lado inverso (mappedBy = "pet")
            petList.get(0).getLifeEvents().add(event);

            lifeEventList.add(event);
        }

        entityManager.flush();
    }

    @Test
    void testCreateLifeEvent() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = petList.get(1);

        LifeEventEntity newEvent = factory.manufacturePojo(LifeEventEntity.class);
        newEvent.setType("Vaccination");
        newEvent.setDate(new Date());
        newEvent.setVeterinarian(veterinarianEntity);

        LifeEventEntity result = lifeEventService.createLifeEvent(pet.getId(), newEvent);
        assertNotNull(result);

        LifeEventEntity entity = entityManager.find(LifeEventEntity.class, result.getId());
        assertEquals("Vaccination", entity.getType());
        assertEquals(pet.getId(), entity.getPet().getId());
        assertEquals(veterinarianEntity.getId(), entity.getVeterinarian().getId());
    }

    @Test
    void testCreateLifeEventWithoutVeterinarian() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = petList.get(1);

        LifeEventEntity newEvent = factory.manufacturePojo(LifeEventEntity.class);
        newEvent.setType("Grooming");
        newEvent.setDate(new Date());
        newEvent.setVeterinarian(null);

        LifeEventEntity result = lifeEventService.createLifeEvent(pet.getId(), newEvent);
        assertNotNull(result);
        assertNull(result.getVeterinarian());
    }

    @Test
    void testCreateLifeEventWithInvalidPet() {
        assertThrows(EntityNotFoundException.class, () -> {
            LifeEventEntity newEvent = factory.manufacturePojo(LifeEventEntity.class);
            newEvent.setType("Checkup");
            newEvent.setDate(new Date());
            lifeEventService.createLifeEvent(0L, newEvent);
        });
    }

    @Test
    void testCreateLifeEventWithNoValidType() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity pet = petList.get(1);
            LifeEventEntity newEvent = factory.manufacturePojo(LifeEventEntity.class);
            newEvent.setType("");
            newEvent.setDate(new Date());
            lifeEventService.createLifeEvent(pet.getId(), newEvent);
        });
    }

    @Test
    void testCreateLifeEventWithNoValidDate() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity pet = petList.get(1);
            LifeEventEntity newEvent = factory.manufacturePojo(LifeEventEntity.class);
            newEvent.setType("Checkup");
            newEvent.setDate(null);
            lifeEventService.createLifeEvent(pet.getId(), newEvent);
        });
    }

    @Test
    void testCreateLifeEventWithInvalidVeterinarian() {
        assertThrows(EntityNotFoundException.class, () -> {
            PetEntity pet = petList.get(1);
            LifeEventEntity newEvent = factory.manufacturePojo(LifeEventEntity.class);
            newEvent.setType("Checkup");
            newEvent.setDate(new Date());

            VeterinarianEntity invalidVet = new VeterinarianEntity();
            invalidVet.setId(0L);
            newEvent.setVeterinarian(invalidVet);

            lifeEventService.createLifeEvent(pet.getId(), newEvent);
        });
    }

    @Test
    void testGetLifeEvents() throws EntityNotFoundException {
        entityManager.flush();
        entityManager.clear();

        PetEntity pet = petList.get(0);
        List<LifeEventEntity> list = lifeEventService.getLifeEvents(pet.getId());
        assertEquals(lifeEventList.size(), list.size());
    }

    @Test
    void testGetLifeEventsWithInvalidPet() {
        assertThrows(EntityNotFoundException.class, () -> {
            lifeEventService.getLifeEvents(0L);
        });
    }

    @Test
    void testGetLifeEvent() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = petList.get(0);
        LifeEventEntity event = lifeEventList.get(0);

        LifeEventEntity result = lifeEventService.getLifeEvent(pet.getId(), event.getId());
        assertNotNull(result);
        assertEquals(event.getId(), result.getId());
        assertEquals(event.getType(), result.getType());
    }

    @Test
    void testGetLifeEventWithInvalidPet() {
        assertThrows(EntityNotFoundException.class, () -> {
            lifeEventService.getLifeEvent(0L, lifeEventList.get(0).getId());
        });
    }

    @Test
    void testGetLifeEventWithInvalidLifeEvent() {
        assertThrows(EntityNotFoundException.class, () -> {
            lifeEventService.getLifeEvent(petList.get(0).getId(), 0L);
        });
    }

    @Test
    void testGetLifeEventNotAssociatedToPet() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity otherPet = petList.get(1);
            LifeEventEntity event = lifeEventList.get(0);
            lifeEventService.getLifeEvent(otherPet.getId(), event.getId());
        });
    }

    @Test
    void testUpdateLifeEvent() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = petList.get(0);
        LifeEventEntity event = lifeEventList.get(0);

        LifeEventEntity pojoEntity = factory.manufacturePojo(LifeEventEntity.class);
        pojoEntity.setType("Updated Checkup");
        pojoEntity.setDate(new Date());
        pojoEntity.setVeterinarian(veterinarianEntity);

        LifeEventEntity result = lifeEventService.updateLifeEvent(pet.getId(), event.getId(), pojoEntity);
        assertNotNull(result);
        assertEquals(event.getId(), result.getId());
        assertEquals("Updated Checkup", result.getType());
        assertEquals(pet.getId(), result.getPet().getId());
    }

    @Test
    void testUpdateLifeEventWithInvalidPet() {
        assertThrows(EntityNotFoundException.class, () -> {
            LifeEventEntity pojoEntity = factory.manufacturePojo(LifeEventEntity.class);
            pojoEntity.setType("X");
            pojoEntity.setDate(new Date());
            lifeEventService.updateLifeEvent(0L, lifeEventList.get(0).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateLifeEventWithInvalidLifeEvent() {
        assertThrows(EntityNotFoundException.class, () -> {
            LifeEventEntity pojoEntity = factory.manufacturePojo(LifeEventEntity.class);
            pojoEntity.setType("X");
            pojoEntity.setDate(new Date());
            lifeEventService.updateLifeEvent(petList.get(0).getId(), 0L, pojoEntity);
        });
    }

    @Test
    void testUpdateLifeEventNotAssociatedToPet() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity otherPet = petList.get(1);
            LifeEventEntity pojoEntity = factory.manufacturePojo(LifeEventEntity.class);
            pojoEntity.setType("X");
            pojoEntity.setDate(new Date());
            lifeEventService.updateLifeEvent(otherPet.getId(), lifeEventList.get(0).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateLifeEventWithNoValidType() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity pet = petList.get(0);
            LifeEventEntity event = lifeEventList.get(0);
            LifeEventEntity pojoEntity = factory.manufacturePojo(LifeEventEntity.class);
            pojoEntity.setType(null);
            pojoEntity.setDate(new Date());
            lifeEventService.updateLifeEvent(pet.getId(), event.getId(), pojoEntity);
        });
    }

    @Test
    void testDeleteLifeEvent() throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = petList.get(0);
        LifeEventEntity event = lifeEventList.get(1);

        Long eventId = event.getId();

        lifeEventService.deleteLifeEvent(pet.getId(), eventId);

        entityManager.flush();
        entityManager.clear();

        LifeEventEntity deleted = entityManager.find(LifeEventEntity.class, eventId);
        assertNull(deleted);
    }

    @Test
    void testDeleteLifeEventWithInvalidPet() {
        assertThrows(EntityNotFoundException.class, () -> {
            lifeEventService.deleteLifeEvent(0L, lifeEventList.get(0).getId());
        });
    }

    @Test
    void testDeleteLifeEventWithInvalidLifeEvent() {
        assertThrows(EntityNotFoundException.class, () -> {
            lifeEventService.deleteLifeEvent(petList.get(0).getId(), 0L);
        });
    }

    @Test
    void testDeleteLifeEventNotAssociatedToPet() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity otherPet = petList.get(1);
            lifeEventService.deleteLifeEvent(otherPet.getId(), lifeEventList.get(0).getId());
        });
    }
}