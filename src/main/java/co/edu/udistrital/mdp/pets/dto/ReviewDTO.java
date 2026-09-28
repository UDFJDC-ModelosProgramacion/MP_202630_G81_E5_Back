package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
 
/**
Representación básica sin ninguna asociación de una Review.
 
 */
@Data
public class ReviewDTO {
 
	private Long id;
	private int rating;
	private String comment;
}
 