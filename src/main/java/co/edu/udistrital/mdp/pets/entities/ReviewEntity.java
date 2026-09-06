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
public class ReviewEntity extends BaseEntity{
    private int rating;
    private String comment;

    @PodamExclude
    @ManyToOne
    @JoinColumn (name = "adopter_id")
    private AdopterEntity adopter;
}
