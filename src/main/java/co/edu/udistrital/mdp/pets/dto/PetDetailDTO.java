package co.edu.udistrital.mdp.pets.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PetDetailDTO extends PetDTO {
	private VaccinationRecordDTO vaccinationRecord;
	private List<AdoptionDTO> adoptions = new ArrayList<>();
	private List<LifeEventDTO> lifeEvents = new ArrayList<>();
	private List<PhotoDTO> photos = new ArrayList<>();
}