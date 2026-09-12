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
public class AdopterEntity extends PersonEntity {

    private String address;

    @PodamExclude
    @OneToMany(mappedBy = "adopter", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<AdoptionEntity> adoptions = new ArrayList<>();

    @PodamExclude
    @OneToMany(mappedBy = "adopter", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<MessageEntity> messages = new ArrayList<>();

    @PodamExclude
    @OneToMany(mappedBy = "adopter", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<ReviewEntity> reviews = new ArrayList<>();

}

