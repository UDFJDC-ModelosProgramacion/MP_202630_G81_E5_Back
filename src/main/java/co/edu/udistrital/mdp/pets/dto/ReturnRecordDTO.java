package co.edu.udistrital.mdp.pets.dto;

import java.util.Date;
 
import lombok.Data;
 
/**
 Representación básica de un ReturnRecord (registro de devolución).
  No tiene asociaciones propias: la relación 1 a 1 la posee Adoption.
 */
@Data
public class ReturnRecordDTO {
 
	private Long id;
	private Date date;
	private String reason;
}
