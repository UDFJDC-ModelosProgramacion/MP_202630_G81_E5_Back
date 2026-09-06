package co.edu.udistrital.mdp.pets.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class VaccinationRecordEntity extends BaseEntity {

    private String vaccinesApplied;
    private Date nextDueDate;
}