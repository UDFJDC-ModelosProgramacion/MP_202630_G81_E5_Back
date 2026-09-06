package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class VideoEntity extends BaseEntity {
    private String url;

    @PodamExclude 
    @ManyToOne 
    @JoinColumn (name = "shelter_id")
    private ShelterEntity shelter;
    
}
