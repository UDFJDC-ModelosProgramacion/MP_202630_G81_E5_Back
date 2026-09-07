package co.edu.udistrital.mdp.pets.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class ShelterEntity extends BaseEntity {

    private String name;
    private String city;

    @PodamExclude
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.PERSIST)
    private List<PetEntity> pets = new ArrayList<>();

    @PodamExclude
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.PERSIST)
    private List<ShelterEventEntity> shelterEvents = new ArrayList<>();

}