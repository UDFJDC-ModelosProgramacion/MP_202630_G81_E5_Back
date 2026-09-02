package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class VeterinarianEntity extends BaseEntity {

    private String name;
    private String email;
    private String phone;
    private String specialty;
    private String availability;
}