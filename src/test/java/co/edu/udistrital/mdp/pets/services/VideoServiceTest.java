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

import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.VideoEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@DataJpaTest
@Transactional
@Import(VideoService.class)
class VideoServiceTest {

	@Autowired
	private VideoService videoService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<VideoEntity> videoList = new ArrayList<>();
	private ShelterEntity shelterEntity;
	private ShelterEntity otherShelterEntity;

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from VideoEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
	}

	private void insertData() {
		shelterEntity = factory.manufacturePojo(ShelterEntity.class);
		entityManager.persist(shelterEntity);

		otherShelterEntity = factory.manufacturePojo(ShelterEntity.class);
		entityManager.persist(otherShelterEntity);

		for (int i = 0; i < 3; i++) {
			VideoEntity videoEntity = factory.manufacturePojo(VideoEntity.class);
			videoEntity.setShelter(shelterEntity);
			entityManager.persist(videoEntity);
			videoList.add(videoEntity);
		}
        shelterEntity.setVideos(videoList);
	}

	@Test
	void testCreateVideo() throws EntityNotFoundException, IllegalOperationException {
		VideoEntity newEntity = factory.manufacturePojo(VideoEntity.class);
		VideoEntity result = videoService.createVideo(shelterEntity.getId(), newEntity);
		assertNotNull(result);
		VideoEntity entity = entityManager.find(VideoEntity.class, result.getId());
		assertEquals(newEntity.getUrl(), entity.getUrl());
		assertEquals(shelterEntity.getId(), entity.getShelter().getId());
	}

	@Test
	void testCreateVideoInvalidShelter() {
		assertThrows(EntityNotFoundException.class, () -> {
			VideoEntity newEntity = factory.manufacturePojo(VideoEntity.class);
			videoService.createVideo(0L, newEntity);
		});
	}

	@Test
	void testCreateVideoWithNoValidUrl() {
		assertThrows(IllegalOperationException.class, () -> {
			VideoEntity newEntity = factory.manufacturePojo(VideoEntity.class);
			newEntity.setUrl("");
			videoService.createVideo(shelterEntity.getId(), newEntity);
		});
	}

	@Test
	void testGetVideos() throws EntityNotFoundException {
		List<VideoEntity> list = videoService.getVideos(shelterEntity.getId());
		assertEquals(videoList.size(), list.size());
	}

	@Test
	void testGetVideosInvalidShelter() {
		assertThrows(EntityNotFoundException.class, () -> {
			videoService.getVideos(0L);
		});
	}

	@Test
	void testGetVideo() throws EntityNotFoundException, IllegalOperationException {
		VideoEntity entity = videoList.get(0);
		VideoEntity result = videoService.getVideo(shelterEntity.getId(), entity.getId());
		assertNotNull(result);
		assertEquals(entity.getId(), result.getId());
	}

	@Test
	void testGetVideoInvalidShelter() {
		assertThrows(EntityNotFoundException.class, () -> {
			videoService.getVideo(0L, videoList.get(0).getId());
		});
	}

	@Test
	void testGetInvalidVideo() {
		assertThrows(EntityNotFoundException.class, () -> {
			videoService.getVideo(shelterEntity.getId(), 0L);
		});
	}

	@Test
	void testGetVideoNotAssociatedToShelter() {
		assertThrows(IllegalOperationException.class, () -> {
			videoService.getVideo(otherShelterEntity.getId(), videoList.get(0).getId());
		});
	}

	@Test
	void testUpdateVideo() throws EntityNotFoundException, IllegalOperationException {
		VideoEntity entity = videoList.get(0);
		VideoEntity pojoEntity = factory.manufacturePojo(VideoEntity.class);
		pojoEntity.setId(entity.getId());

		videoService.updateVideo(shelterEntity.getId(), entity.getId(), pojoEntity);
		VideoEntity resp = entityManager.find(VideoEntity.class, entity.getId());
		assertEquals(pojoEntity.getUrl(), resp.getUrl());
	}

	@Test
	void testUpdateVideoInvalidShelter() {
		assertThrows(EntityNotFoundException.class, () -> {
			VideoEntity entity = videoList.get(0);
			VideoEntity pojoEntity = factory.manufacturePojo(VideoEntity.class);
			pojoEntity.setId(entity.getId());
			videoService.updateVideo(0L, entity.getId(), pojoEntity);
		});
	}

	@Test
	void testUpdateInvalidVideo() {
		assertThrows(EntityNotFoundException.class, () -> {
			VideoEntity pojoEntity = factory.manufacturePojo(VideoEntity.class);
			videoService.updateVideo(shelterEntity.getId(), 0L, pojoEntity);
		});
	}

	@Test
	void testDeleteVideo() throws EntityNotFoundException, IllegalOperationException {
		VideoEntity entity = videoList.get(0);
		videoService.deleteVideo(shelterEntity.getId(), entity.getId());
		assertNull(entityManager.find(VideoEntity.class, entity.getId()));
	}

	@Test
	void testDeleteInvalidVideo() {
		assertThrows(EntityNotFoundException.class, () -> {
			videoService.deleteVideo(shelterEntity.getId(), 0L);
		});
	}

	@Test
	void testDeleteVideoNotAssociatedToShelter() {
		assertThrows(IllegalOperationException.class, () -> {
			videoService.deleteVideo(otherShelterEntity.getId(), videoList.get(0).getId());
		});
	}
}
