package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class PetEntity extends BaseEntity {
    
    private String name;
    private String species;
    private String breed;
    private int age;
    private String sex;
    private String size;
    private String temperament;
    private String specialNeeds;
    public boolean available;

}
