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
import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(MessageService.class)
class MessageServiceTest {

	@Autowired
	private MessageService messageService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<MessageEntity> messageList = new ArrayList<>();
	private AdopterEntity adopterEntity;

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from MessageEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from AdopterEntity").executeUpdate();
	}

	private void insertData() {
		adopterEntity = factory.manufacturePojo(AdopterEntity.class);
		entityManager.persist(adopterEntity);

		for (int i = 0; i < 3; i++) {
			MessageEntity messageEntity = factory.manufacturePojo(MessageEntity.class);
			messageEntity.setSentDate(new Date());
			messageEntity.setAdopter(adopterEntity);
			entityManager.persist(messageEntity);
			messageList.add(messageEntity);
		}
	}

	@Test
	void testCreateMessage() throws IllegalOperationException {
		MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
		newEntity.setSentDate(new Date());

		MessageEntity result = messageService.createMessage(adopterEntity.getId(), newEntity);
		assertNotNull(result);
		MessageEntity entity = entityManager.find(MessageEntity.class, result.getId());
		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getContent(), entity.getContent());
		assertEquals(adopterEntity.getId(), entity.getAdopter().getId());
	}

	@Test
	void testCreateMessageWithInvalidAdopter() {
		assertThrows(IllegalOperationException.class, () -> {
			MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
			newEntity.setSentDate(new Date());
			messageService.createMessage(0L, newEntity);
		});
	}

	@Test
	void testCreateMessageWithNoValidContent() {
		assertThrows(IllegalOperationException.class, () -> {
			MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
			newEntity.setSentDate(new Date());
			newEntity.setContent("");
			messageService.createMessage(adopterEntity.getId(), newEntity);
		});
	}

	@Test
	void testCreateMessageWithFutureDate() {
		assertThrows(IllegalOperationException.class, () -> {
			MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
			newEntity.setSentDate(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24));
			messageService.createMessage(adopterEntity.getId(), newEntity);
		});
	}

	@Test
	void testGetMessages() throws EntityNotFoundException {
		List<MessageEntity> list = messageService.getMessages(adopterEntity.getId());
		assertEquals(messageList.size(), list.size());
	}

	@Test
	void testGetMessagesWithInvalidAdopter() {
		assertThrows(EntityNotFoundException.class, () -> {
			messageService.getMessages(0L);
		});
	}

	@Test
	void testGetMessage() throws EntityNotFoundException, IllegalOperationException {
		MessageEntity entity = messageList.get(0);
		MessageEntity resultEntity = messageService.getMessage(adopterEntity.getId(), entity.getId());
		assertNotNull(resultEntity);
		assertEquals(entity.getId(), resultEntity.getId());
		assertEquals(entity.getContent(), resultEntity.getContent());
	}

	@Test
	void testGetInvalidMessage() {
		assertThrows(EntityNotFoundException.class, () -> {
			messageService.getMessage(adopterEntity.getId(), 0L);
		});
	}

	@Test
	void testGetMessageNotAssociatedToAdopter() {
		assertThrows(IllegalOperationException.class, () -> {
			AdopterEntity otherAdopter = factory.manufacturePojo(AdopterEntity.class);
			entityManager.persist(otherAdopter);
			MessageEntity entity = messageList.get(0);
			messageService.getMessage(otherAdopter.getId(), entity.getId());
		});
	}

	@Test
	void testUpdateMessage() {
		assertThrows(IllegalOperationException.class, () -> {
			MessageEntity entity = messageList.get(0);
			MessageEntity pojoEntity = factory.manufacturePojo(MessageEntity.class);
			pojoEntity.setId(entity.getId());
			messageService.updateMessage(adopterEntity.getId(), entity.getId(), pojoEntity);
		});
	}

	@Test
	void testUpdateInvalidMessage() {
		assertThrows(EntityNotFoundException.class, () -> {
			MessageEntity pojoEntity = factory.manufacturePojo(MessageEntity.class);
			pojoEntity.setId(0L);
			messageService.updateMessage(adopterEntity.getId(), 0L, pojoEntity);
		});
	}

	@Test
	void testDeleteMessage() throws EntityNotFoundException, IllegalOperationException {
		MessageEntity entity = messageList.get(0);
		messageService.deleteMessage(adopterEntity.getId(), entity.getId());
		MessageEntity deleted = entityManager.find(MessageEntity.class, entity.getId());
		assertNull(deleted);
	}

	@Test
	void testDeleteInvalidMessage() {
		assertThrows(EntityNotFoundException.class, () -> {
			messageService.deleteMessage(adopterEntity.getId(), 0L);
		});
	}

}