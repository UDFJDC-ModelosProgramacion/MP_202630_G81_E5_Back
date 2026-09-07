package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

import java.util.Date;


import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity

public class AdoptionEntity extends BaseEntity {



    @Temporal(TemporalType.DATE)
    private Date date;
    
    private String status;
    


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "return_record_id")
    private ReturnRecordEntity returnRecord;

    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "pet_id")
    private PetEntity pet;

    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "veterinarian_id")
    private VeterinarianEntity responsibleVeterinarian;

    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "adopter_id")
    private AdopterEntity adopter;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "trial_cohabitation_id")
    private TrialCohabitationEntity trialCohabitation;

}