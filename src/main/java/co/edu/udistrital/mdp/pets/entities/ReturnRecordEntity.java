package co.edu.udistrital.mdp.pets.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity

public class ReturnRecordEntity extends BaseEntity {

    @Temporal(TemporalType.DATE)
    private Date date;
    
    private String reason;
    

}
