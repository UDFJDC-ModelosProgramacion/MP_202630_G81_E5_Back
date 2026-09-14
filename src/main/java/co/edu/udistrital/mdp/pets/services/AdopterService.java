package co.edu.udistrital.mdp.pets.services;


import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor 
@Slf4j
@Service 
public class AdopterService {
    final AdopterRepository adopterRepository;

    @Transactional(rollbackFor = {IllegalOperationException.class})
    public AdopterEntity createAdopter(AdopterEntity adopterEntity) throws IllegalOperationException{
        log.info("Starting the process of creating an adopter");

        if (!validateAdopter(adopterEntity))
            throw new IllegalOperationException(ErrorMessage.ADOPTER_NOT_VALID);

        if (emailAlreadyUsed(adopterEntity.getEmail(), null))
            throw new IllegalOperationException(ErrorMessage.ADOPTER_EMAIL_EXISTS);

        log.info("Finished process to create an adopter");
        return adopterRepository.save(adopterEntity);
    
    }

    @Transactional
    public List<AdopterEntity> getAdopters(){
        log.info("Starting process to fetch all adopters");
        return adopterRepository.findAll();
    }

    @Transactional(rollbackFor = {EntityNotFoundException.class})
    public AdopterEntity getAdopter(Long adopterId) throws EntityNotFoundException{
        log.info("Starting process to fetch an adopter with id = ", adopterId);
        Optional<AdopterEntity> adopterOptional = adopterRepository.findById(adopterId);
        if (adopterOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);

        log.info("Finished process to fetch an adopter with id = ", adopterId);
        return adopterOptional.get();
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public AdopterEntity updateAdopter(Long adopterId, AdopterEntity adopter) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to update adopter with id = ", adopterId);
		Optional<AdopterEntity> adopterOptional = adopterRepository.findById(adopterId);
		if (adopterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);
 
		if (!validateAdopter(adopter))
			throw new IllegalOperationException(ErrorMessage.ADOPTER_NOT_VALID);
 
		AdopterEntity existingAdopter = adopterOptional.get();
		if (!existingAdopter.getEmail().equalsIgnoreCase(adopter.getEmail())
				&& emailAlreadyUsed(adopter.getEmail(), adopterId))
			throw new IllegalOperationException(ErrorMessage.ADOPTER_EMAIL_EXISTS);

		adopter.setId(adopterId);
		log.info("Finished process to update adopter with id = ", adopterId);
		return adopterRepository.save(adopter);
	}

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public void deleteAdopter(Long adopterId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to delete adopter with id = ", adopterId);
		Optional<AdopterEntity> adopterOptional = adopterRepository.findById(adopterId);
		if (adopterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);
 
		AdopterEntity adopterEntity = adopterOptional.get();
 
		if (!adopterEntity.getAdoptions().isEmpty())
			throw new IllegalOperationException(ErrorMessage.ADOPTER_ASSOCIATED_ADOPTIONS);

		if (!adopterEntity.getMessages().isEmpty())
			throw new IllegalOperationException(ErrorMessage.ADOPTER_ASSOCIATED_MESSAGES);

		if (!adopterEntity.getReviews().isEmpty())
			throw new IllegalOperationException(ErrorMessage.ADOPTER_ASSOCIATED_REVIEWS);
 
		adopterRepository.deleteById(adopterId);
		log.info("Finished process to delete adopter with id = ", adopterId);
	}

    private boolean validateAdopter(AdopterEntity adopter){
        return adopter.getName() != null && !adopter.getName().isEmpty() && adopter.getEmail() != null && !adopter.getEmail().isEmpty();
    }

    private boolean emailAlreadyUsed(String email, Long excludedId){
        return adopterRepository.findAll().stream()
                .anyMatch(a -> a.getEmail().equalsIgnoreCase(email) && !a.getId().equals(excludedId));
    }
    
}


