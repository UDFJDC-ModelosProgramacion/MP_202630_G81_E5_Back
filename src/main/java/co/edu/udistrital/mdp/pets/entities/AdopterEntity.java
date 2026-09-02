package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class AdopterEntity extends PersonEntity {

    private String address;

    // TODO (integración posterior con Persona 1, 2 y 3):
    // cuando existan AdoptionEntity, MessageEntity y ReviewEntity, agregar:
    //
    // @OneToMany(mappedBy = "adopter", cascade = CascadeType.PERSIST, orphanRemoval = true)
    // private List<AdoptionEntity> adoptions = new ArrayList<>();
    //
    // @OneToMany(mappedBy = "adopter", cascade = CascadeType.PERSIST, orphanRemoval = true)
    // private List<MessageEntity> messages = new ArrayList<>();
    //
    // @OneToMany(mappedBy = "adopter", cascade = CascadeType.PERSIST, orphanRemoval = true)
    // private List<ReviewEntity> reviews = new ArrayList<>();

}