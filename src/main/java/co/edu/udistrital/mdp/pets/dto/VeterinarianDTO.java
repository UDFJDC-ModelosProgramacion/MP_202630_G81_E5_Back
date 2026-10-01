package co.edu.udistrital.mdp.pets.dto;


import lombok.Data;

@Data
public class VeterinarianDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String specialty;
    private String availability;

}