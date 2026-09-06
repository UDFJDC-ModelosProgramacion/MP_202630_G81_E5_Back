package co.edu.udistrital.mdp.pets.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
public class ShelterEventEntity extends BaseEntity {

    private String title;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String type;

}