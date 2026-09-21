package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Calendar;
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
	private AdopterEntity otherAdopterEntity;

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

		otherAdopterEntity = factory.manufacturePojo(AdopterEntity.class);
		entityManager.persist(otherAdopterEntity);

		for (int i = 0; i < 3; i++) {
			MessageEntity messageEntity = factory.manufacturePojo(MessageEntity.class);
			messageEntity.setSentDate(pastDate());
			messageEntity.setAdopter(adopterEntity);
			entityManager.persist(messageEntity);
			messageList.add(messageEntity);
		}
		adopterEntity.setMessages(messageList);

	}

	private Date pastDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		return calendar.getTime();
	}

	private Date futureDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 5);
		return calendar.getTime();
	}

	@Test
	void testCreateMessage() throws EntityNotFoundException, IllegalOperationException {
		MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
		newEntity.setSentDate(pastDate());

		MessageEntity result = messageService.createMessage(adopterEntity.getId(), newEntity);
		assertNotNull(result);
		MessageEntity entity = entityManager.find(MessageEntity.class, result.getId());
		assertEquals(newEntity.getContent(), entity.getContent());
		assertEquals(adopterEntity.getId(), entity.getAdopter().getId());
	}

	@Test
	void testCreateMessageInvalidAdopter() {
		assertThrows(EntityNotFoundException.class, () -> {
			MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
			newEntity.setSentDate(pastDate());
			messageService.createMessage(0L, newEntity);
		});
	}

	@Test
	void testCreateMessageWithNoValidContent() {
		assertThrows(IllegalOperationException.class, () -> {
			MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
			newEntity.setContent("");
			newEntity.setSentDate(pastDate());
			messageService.createMessage(adopterEntity.getId(), newEntity);
		});
	}

	@Test
	void testCreateMessageWithFutureDate() {
		assertThrows(IllegalOperationException.class, () -> {
			MessageEntity newEntity = factory.manufacturePojo(MessageEntity.class);
			newEntity.setSentDate(futureDate());
			messageService.createMessage(adopterEntity.getId(), newEntity);
		});
	}

	@Test
	void testGetMessages() throws EntityNotFoundException {
		List<MessageEntity> list = messageService.getMessages(adopterEntity.getId());
		assertEquals(messageList.size(), list.size());
	}

	@Test
	void testGetMessagesInvalidAdopter() {
		assertThrows(EntityNotFoundException.class, () -> {
			messageService.getMessages(0L);
		});
	}

	@Test
	void testGetMessage() throws EntityNotFoundException, IllegalOperationException {
		MessageEntity entity = messageList.get(0);
		MessageEntity result = messageService.getMessage(adopterEntity.getId(), entity.getId());
		assertNotNull(result);
		assertEquals(entity.getId(), result.getId());
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
			messageService.getMessage(otherAdopterEntity.getId(), messageList.get(0).getId());
		});
	}

	@Test
	void testUpdateMessage() throws EntityNotFoundException, IllegalOperationException {
		MessageEntity entity = messageList.get(0);
		MessageEntity pojoEntity = factory.manufacturePojo(MessageEntity.class);
		pojoEntity.setId(entity.getId());
		pojoEntity.setSentDate(pastDate());

		messageService.updateMessage(adopterEntity.getId(), entity.getId(), pojoEntity);
		MessageEntity resp = entityManager.find(MessageEntity.class, entity.getId());
		assertEquals(pojoEntity.getContent(), resp.getContent());
	}

	@Test
	void testUpdateInvalidMessage() {
		assertThrows(EntityNotFoundException.class, () -> {
			MessageEntity pojoEntity = factory.manufacturePojo(MessageEntity.class);
			pojoEntity.setSentDate(pastDate());
			messageService.updateMessage(adopterEntity.getId(), 0L, pojoEntity);
		});
	}

	@Test
	void testDeleteMessage() throws EntityNotFoundException, IllegalOperationException {
		MessageEntity entity = messageList.get(0);
		messageService.deleteMessage(adopterEntity.getId(), entity.getId());
		assertNull(entityManager.find(MessageEntity.class, entity.getId()));
	}

	@Test
	void testDeleteInvalidMessage() {
		assertThrows(EntityNotFoundException.class, () -> {
			messageService.deleteMessage(adopterEntity.getId(), 0L);
		});
	}

	@Test
	void testDeleteMessageNotAssociatedToAdopter() {
		assertThrows(IllegalOperationException.class, () -> {
			messageService.deleteMessage(otherAdopterEntity.getId(), messageList.get(0).getId());
		});
	}

}