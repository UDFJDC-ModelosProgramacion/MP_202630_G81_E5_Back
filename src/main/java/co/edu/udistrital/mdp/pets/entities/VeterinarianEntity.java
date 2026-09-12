package co.edu.udistrital.mdp.pets.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class VeterinarianEntity extends PersonEntity {

    private String specialty;
    private String availability;

    @PodamExclude 
    @ManyToOne 
    @JoinColumn (name = "shelter_id")
    private ShelterEntity shelter;

    @PodamExclude
    @OneToMany(mappedBy = "veterinarian", cascade = CascadeType.PERSIST)
    private List<LifeEventEntity> registeredEvents = new ArrayList<>();

    @PodamExclude
    @OneToMany(mappedBy = "responsibleVeterinarian", cascade = CascadeType.PERSIST)
    private List<AdoptionEntity> adoptions = new ArrayList<>();
}