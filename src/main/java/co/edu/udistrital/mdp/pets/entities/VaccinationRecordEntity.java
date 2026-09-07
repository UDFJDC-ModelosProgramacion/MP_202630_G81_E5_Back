package co.edu.udistrital.mdp.pets.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class VaccinationRecordEntity extends BaseEntity {

    private String vaccinesApplied;
    private Date nextDueDate;

    @PodamExclude
    @OneToOne(mappedBy = "vaccinationRecord")
    private PetEntity pet;
}