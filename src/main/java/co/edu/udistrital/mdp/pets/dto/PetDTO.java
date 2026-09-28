package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data
public class PetDTO {
	private Long id;
	private String name;
	private String species;
	private String breed;
	private int age;
	private String sex;
	private String size;
	private String temperament;
	private String specialNeeds;
	private boolean available;
}