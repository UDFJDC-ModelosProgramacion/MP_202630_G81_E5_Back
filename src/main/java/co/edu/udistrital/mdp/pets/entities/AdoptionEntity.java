package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    

}
