package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data
public abstract class PersonDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
}
