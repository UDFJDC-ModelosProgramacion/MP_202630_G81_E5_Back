package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(NotificationService.class)
class NotificationServiceTest {

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<NotificationEntity> notificationList = new ArrayList<>();
	private ShelterEntity shelterEntity;
	private ShelterEntity otherShelterEntity;

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from NotificationEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
	}

	private void insertData() {
		shelterEntity = factory.manufacturePojo(ShelterEntity.class);
		entityManager.persist(shelterEntity);

		otherShelterEntity = factory.manufacturePojo(ShelterEntity.class);
		entityManager.persist(otherShelterEntity);

		for (int i = 0; i < 3; i++) {
			NotificationEntity notificationEntity = factory.manufacturePojo(NotificationEntity.class);

			notificationEntity.setChannel("EMAIL");
			notificationEntity.setShelter(shelterEntity);
			entityManager.persist(notificationEntity);
			notificationList.add(notificationEntity);
		}

		shelterEntity.setNotifications(notificationList);
	}


	@ParameterizedTest
	@ValueSource(strings = { "EMAIL", "SMS", "PUSH" })
	void testCreateNotification(String channel) throws IllegalOperationException {
		NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
		newEntity.setChannel(channel);

		NotificationEntity result = notificationService.createNotification(shelterEntity.getId(), newEntity);
		assertNotNull(result);

		NotificationEntity entity = entityManager.find(NotificationEntity.class, result.getId());
		assertEquals(newEntity.getMessage(), entity.getMessage());
		assertEquals(channel, entity.getChannel());
		assertEquals(shelterEntity.getId(), entity.getShelter().getId());
	}

	@Test
	void testCreateNotificationInvalidShelter() {

		NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
		newEntity.setChannel("SMS");
		assertThrows(IllegalOperationException.class, () -> notificationService.createNotification(0L, newEntity));
	}

	@Test
	void testCreateNotificationWithEmptyMessage() {
		NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
		newEntity.setChannel("EMAIL");
		newEntity.setMessage("");
		assertThrows(IllegalOperationException.class,
				() -> notificationService.createNotification(shelterEntity.getId(), newEntity));
	}

	@Test
	void testCreateNotificationWithNullMessage() {
		NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
		newEntity.setChannel("EMAIL");
		newEntity.setMessage(null);
		assertThrows(IllegalOperationException.class,
				() -> notificationService.createNotification(shelterEntity.getId(), newEntity));
	}

	@Test
	void testCreateNotificationWithInvalidChannel() {
		NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
		newEntity.setChannel("WHATSAPP");
		assertThrows(IllegalOperationException.class,
				() -> notificationService.createNotification(shelterEntity.getId(), newEntity));
	}

	@Test
	void testCreateNotificationWithLowerCaseChannel() {
	
		NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
		newEntity.setChannel("email");
		assertThrows(IllegalOperationException.class,
				() -> notificationService.createNotification(shelterEntity.getId(), newEntity));
	}

	@Test
	void testCreateNotificationWithNullChannel() {
		NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
		newEntity.setChannel(null);
		assertThrows(IllegalOperationException.class,
				() -> notificationService.createNotification(shelterEntity.getId(), newEntity));
	}



	@Test
	void testGetNotifications() throws EntityNotFoundException {
		List<NotificationEntity> list = notificationService.getNotifications(shelterEntity.getId());
		assertEquals(notificationList.size(), list.size());
		for (NotificationEntity entity : list) {
			boolean found = false;
			for (NotificationEntity stored : notificationList) {
				if (entity.getId().equals(stored.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	@Test
	void testGetNotificationsOfShelterWithoutNotifications() throws EntityNotFoundException {
		List<NotificationEntity> list = notificationService.getNotifications(otherShelterEntity.getId());
		assertTrue(list.isEmpty());
	}

	@Test
	void testGetNotificationsInvalidShelter() {
		assertThrows(EntityNotFoundException.class, () -> notificationService.getNotifications(0L));
	}

	@Test
	void testGetNotification() throws EntityNotFoundException, IllegalOperationException {
		NotificationEntity entity = notificationList.get(0);
		NotificationEntity result = notificationService.getNotification(shelterEntity.getId(), entity.getId());
		assertNotNull(result);
		assertEquals(entity.getId(), result.getId());
		assertEquals(entity.getMessage(), result.getMessage());
		assertEquals(entity.getChannel(), result.getChannel());
	}

	@Test
	void testGetNotificationInvalidShelter() {
		NotificationEntity entity = notificationList.get(0);
		assertThrows(EntityNotFoundException.class, () -> notificationService.getNotification(0L, entity.getId()));
	}

	@Test
	void testGetInvalidNotification() {
		assertThrows(EntityNotFoundException.class,
				() -> notificationService.getNotification(shelterEntity.getId(), 0L));
	}

	@Test
	void testGetNotificationNotAssociatedToShelter() {
		NotificationEntity entity = notificationList.get(0);
		assertThrows(IllegalOperationException.class,
				() -> notificationService.getNotification(otherShelterEntity.getId(), entity.getId()));
	}



	@Test
	void testUpdateNotification() throws EntityNotFoundException, IllegalOperationException {
		NotificationEntity entity = notificationList.get(0);
		NotificationEntity pojoEntity = factory.manufacturePojo(NotificationEntity.class);
		pojoEntity.setChannel("PUSH");
		pojoEntity.setId(entity.getId());

		notificationService.updateNotification(shelterEntity.getId(), entity.getId(), pojoEntity);

		NotificationEntity resp = entityManager.find(NotificationEntity.class, entity.getId());
		assertEquals(pojoEntity.getMessage(), resp.getMessage());
		assertEquals("PUSH", resp.getChannel());

		assertEquals(shelterEntity.getId(), resp.getShelter().getId());
	}

	@Test
	void testUpdateNotificationInvalidShelter() {
		NotificationEntity entity = notificationList.get(0);
		NotificationEntity pojoEntity = factory.manufacturePojo(NotificationEntity.class);
		pojoEntity.setChannel("SMS");
		assertThrows(EntityNotFoundException.class,
				() -> notificationService.updateNotification(0L, entity.getId(), pojoEntity));
	}

	@Test
	void testUpdateInvalidNotification() {
		NotificationEntity pojoEntity = factory.manufacturePojo(NotificationEntity.class);
		pojoEntity.setChannel("SMS");
		assertThrows(EntityNotFoundException.class,
				() -> notificationService.updateNotification(shelterEntity.getId(), 0L, pojoEntity));
	}

	@Test
	void testUpdateNotificationNotAssociatedToShelter() {
		NotificationEntity entity = notificationList.get(0);
		NotificationEntity pojoEntity = factory.manufacturePojo(NotificationEntity.class);
		pojoEntity.setChannel("SMS");
		assertThrows(IllegalOperationException.class,
				() -> notificationService.updateNotification(otherShelterEntity.getId(), entity.getId(), pojoEntity));
	}

	@Test
	void testUpdateNotificationWithNoValidMessage() {
		NotificationEntity entity = notificationList.get(0);
		NotificationEntity pojoEntity = factory.manufacturePojo(NotificationEntity.class);
		pojoEntity.setChannel("SMS");
		pojoEntity.setMessage("");
		assertThrows(IllegalOperationException.class,
				() -> notificationService.updateNotification(shelterEntity.getId(), entity.getId(), pojoEntity));
	}

	@Test
	void testUpdateNotificationWithInvalidChannel() {
		NotificationEntity entity = notificationList.get(0);
		NotificationEntity pojoEntity = factory.manufacturePojo(NotificationEntity.class);
		pojoEntity.setChannel("TELEGRAM");
		assertThrows(IllegalOperationException.class,
				() -> notificationService.updateNotification(shelterEntity.getId(), entity.getId(), pojoEntity));
	}



	@Test
	void testDeleteNotification() throws EntityNotFoundException, IllegalOperationException {
		NotificationEntity entity = notificationList.get(1);
		notificationService.deleteNotification(shelterEntity.getId(), entity.getId());
		assertNull(entityManager.find(NotificationEntity.class, entity.getId()));
	}

	@Test
	void testDeleteNotificationInvalidShelter() {
		NotificationEntity entity = notificationList.get(0);
		assertThrows(EntityNotFoundException.class, () -> notificationService.deleteNotification(0L, entity.getId()));
	}

	@Test
	void testDeleteInvalidNotification() {
		assertThrows(EntityNotFoundException.class,
				() -> notificationService.deleteNotification(shelterEntity.getId(), 0L));
	}

	@Test
	void testDeleteNotificationNotAssociatedToShelter() {
		NotificationEntity entity = notificationList.get(0);
		assertThrows(IllegalOperationException.class,
				() -> notificationService.deleteNotification(otherShelterEntity.getId(), entity.getId()));
	}

}

