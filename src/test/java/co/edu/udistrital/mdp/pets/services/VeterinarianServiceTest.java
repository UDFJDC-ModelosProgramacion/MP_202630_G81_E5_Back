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
import co.edu.udistrital.mdp.pets.entities.LifeEventEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(VeterinarianService.class)
class VeterinarianServiceTest {

    @Autowired
    private VeterinarianService veterinarianService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<VeterinarianEntity> veterinarianList = new ArrayList<>();
    private ShelterEntity shelterEntity;

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from LifeEventEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from VeterinarianEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
    }

    private void insertData() {
        shelterEntity = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelterEntity);

        for (int i = 0; i < 3; i++) {
            VeterinarianEntity vet = factory.manufacturePojo(VeterinarianEntity.class);
            vet.setShelter(shelterEntity);
            vet.setName("Vet " + i);
            vet.setEmail("vet" + i + "@test.com");
            vet.setSpecialty("General");
            entityManager.persist(vet);
            veterinarianList.add(vet);
        }
    }

    @Test
    void testCreateVeterinarian() throws IllegalOperationException {
        VeterinarianEntity newEntity = factory.manufacturePojo(VeterinarianEntity.class);
        newEntity.setShelter(shelterEntity);
        newEntity.setName("Nuevo Vet");
        newEntity.setEmail("nuevo@test.com");
        newEntity.setSpecialty("Cirugia");

        VeterinarianEntity result = veterinarianService.createVeterinarian(newEntity);
        assertNotNull(result);

        VeterinarianEntity entity = entityManager.find(VeterinarianEntity.class, result.getId());
        assertEquals(newEntity.getName(), entity.getName());
        assertEquals(newEntity.getEmail(), entity.getEmail());
        assertEquals(newEntity.getSpecialty(), entity.getSpecialty());
        assertEquals(shelterEntity.getId(), entity.getShelter().getId());
    }

    @Test
    void testCreateVeterinarianWithoutShelter() throws IllegalOperationException {
        // El service actual NO obliga shelter, solo lo valida si viene.
        VeterinarianEntity newEntity = factory.manufacturePojo(VeterinarianEntity.class);
        newEntity.setShelter(null);
        newEntity.setName("Sin Shelter");
        newEntity.setEmail("sinshelter@test.com");
        newEntity.setSpecialty("General");

        VeterinarianEntity result = veterinarianService.createVeterinarian(newEntity);
        assertNotNull(result);
        assertNull(result.getShelter());
    }

    @Test
    void testCreateVeterinarianWithInvalidShelter() {
        assertThrows(IllegalOperationException.class, () -> {
            VeterinarianEntity newEntity = factory.manufacturePojo(VeterinarianEntity.class);
            ShelterEntity invalidShelter = new ShelterEntity();
            invalidShelter.setId(0L);
            newEntity.setShelter(invalidShelter);
            newEntity.setName("Vet X");
            newEntity.setEmail("x@test.com");
            newEntity.setSpecialty("General");
            veterinarianService.createVeterinarian(newEntity);
        });
    }

    @Test
    void testCreateVeterinarianWithNoValidName() {
        assertThrows(IllegalOperationException.class, () -> {
            VeterinarianEntity newEntity = factory.manufacturePojo(VeterinarianEntity.class);
            newEntity.setShelter(shelterEntity);
            newEntity.setName("");
            newEntity.setEmail("a@test.com");
            newEntity.setSpecialty("General");
            veterinarianService.createVeterinarian(newEntity);
        });
    }

    @Test
    void testCreateVeterinarianWithNoValidEmail() {
        assertThrows(IllegalOperationException.class, () -> {
            VeterinarianEntity newEntity = factory.manufacturePojo(VeterinarianEntity.class);
            newEntity.setShelter(shelterEntity);
            newEntity.setName("Vet");
            newEntity.setEmail(null);
            newEntity.setSpecialty("General");
            veterinarianService.createVeterinarian(newEntity);
        });
    }

    @Test
    void testCreateVeterinarianWithNoValidSpecialty() {
        assertThrows(IllegalOperationException.class, () -> {
            VeterinarianEntity newEntity = factory.manufacturePojo(VeterinarianEntity.class);
            newEntity.setShelter(shelterEntity);
            newEntity.setName("Vet");
            newEntity.setEmail("v@test.com");
            newEntity.setSpecialty("");
            veterinarianService.createVeterinarian(newEntity);
        });
    }

    @Test
    void testGetVeterinarians() {
        List<VeterinarianEntity> list = veterinarianService.getVeterinarians();
        assertEquals(veterinarianList.size(), list.size());
    }

    @Test
    void testGetVeterinarian() throws EntityNotFoundException {
        VeterinarianEntity entity = veterinarianList.get(0);
        VeterinarianEntity result = veterinarianService.getVeterinarian(entity.getId());
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getName(), result.getName());
    }

    @Test
    void testGetInvalidVeterinarian() {
        assertThrows(EntityNotFoundException.class, () -> {
            veterinarianService.getVeterinarian(0L);
        });
    }

    @Test
    void testUpdateVeterinarian() throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity entity = veterinarianList.get(0);
        VeterinarianEntity pojoEntity = factory.manufacturePojo(VeterinarianEntity.class);
        pojoEntity.setId(entity.getId());
        pojoEntity.setName("Actualizado");
        pojoEntity.setEmail("act@test.com");
        pojoEntity.setSpecialty("Dermatologia");

        veterinarianService.updateVeterinarian(entity.getId(), pojoEntity);

        VeterinarianEntity resp = entityManager.find(VeterinarianEntity.class, entity.getId());
        assertEquals(pojoEntity.getName(), resp.getName());
        assertEquals(pojoEntity.getEmail(), resp.getEmail());
        assertEquals(pojoEntity.getSpecialty(), resp.getSpecialty());
        // El shelter se conserva del existente
        assertEquals(entity.getShelter().getId(), resp.getShelter().getId());
    }

    @Test
    void testUpdateInvalidVeterinarian() {
        assertThrows(EntityNotFoundException.class, () -> {
            VeterinarianEntity pojoEntity = factory.manufacturePojo(VeterinarianEntity.class);
            pojoEntity.setId(0L);
            pojoEntity.setName("X");
            pojoEntity.setEmail("x@test.com");
            pojoEntity.setSpecialty("G");
            veterinarianService.updateVeterinarian(0L, pojoEntity);
        });
    }

    @Test
    void testUpdateVeterinarianWithNoValidName() {
        assertThrows(IllegalOperationException.class, () -> {
            VeterinarianEntity entity = veterinarianList.get(0);
            VeterinarianEntity pojoEntity = factory.manufacturePojo(VeterinarianEntity.class);
            pojoEntity.setId(entity.getId());
            pojoEntity.setName(null);
            pojoEntity.setEmail("a@test.com");
            pojoEntity.setSpecialty("G");
            veterinarianService.updateVeterinarian(entity.getId(), pojoEntity);
        });
    }

    @Test
    void testDeleteVeterinarian() throws EntityNotFoundException, IllegalOperationException {
        VeterinarianEntity entity = veterinarianList.get(1);
        veterinarianService.deleteVeterinarian(entity.getId());
        VeterinarianEntity deleted = entityManager.find(VeterinarianEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteInvalidVeterinarian() {
        assertThrows(EntityNotFoundException.class, () -> {
            veterinarianService.deleteVeterinarian(0L);
        });
    }

    @Test
    void testDeleteVeterinarianWithRegisteredEvents() {
        assertThrows(IllegalOperationException.class, () -> {
            VeterinarianEntity vet = veterinarianList.get(0);

            LifeEventEntity event = factory.manufacturePojo(LifeEventEntity.class);
            event.setDate(new java.util.Date());
            event.setVeterinarian(vet);

            // Como la relación es el lado inverso (mappedBy), hay que agregar
            // manualmente al vet en memoria para que la lista no esté vacía
            vet.getRegisteredEvents().add(event);

            entityManager.persist(event);
            entityManager.flush();

            veterinarianService.deleteVeterinarian(vet.getId());
        });
    }

    @Test
    void testDeleteVeterinarianWithAdoptions() {
        assertThrows(IllegalOperationException.class, () -> {
            VeterinarianEntity vet = veterinarianList.get(0);

            AdoptionEntity adoption = factory.manufacturePojo(AdoptionEntity.class);
            adoption.setResponsibleVeterinarian(vet);

            vet.getAdoptions().add(adoption);

            entityManager.persist(adoption);
            entityManager.flush();

            veterinarianService.deleteVeterinarian(vet.getId());
        });
    }
}