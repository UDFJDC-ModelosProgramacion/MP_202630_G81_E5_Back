package co.edu.udistrital.mdp.pets.dto;

import java.util.Date;
 
import lombok.Data;
 
/**
Representación básica sin ninguna asociaciones) de una Adoption.
 
 */
@Data
public class AdoptionDTO {
 
	private Long id;
	private Date date;
	private String status;
}
