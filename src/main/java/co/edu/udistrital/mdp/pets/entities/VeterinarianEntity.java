package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode (callSuper = true)
@Entity
public class VeterinarianEntity extends PersonEntity {

    private String specialty;
    private String availability;
}