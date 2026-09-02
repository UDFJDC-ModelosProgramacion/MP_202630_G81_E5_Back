package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class PhotoEntity extends BaseEntity {

    private String url;
    

    
}
