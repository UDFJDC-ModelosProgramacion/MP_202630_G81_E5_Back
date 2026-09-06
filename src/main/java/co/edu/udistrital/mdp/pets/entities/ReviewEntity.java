package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class ReviewEntity extends BaseEntity{
    private int rating;
    private String comment;
}
