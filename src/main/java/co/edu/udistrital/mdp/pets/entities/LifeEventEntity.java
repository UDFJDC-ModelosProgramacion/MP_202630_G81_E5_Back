package co.edu.udistrital.mdp.pets.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class LifeEventEntity extends BaseEntity {

    private String type;
    private String description;
    private Date date;
}