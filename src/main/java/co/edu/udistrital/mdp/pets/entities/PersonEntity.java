package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class PersonEntity extends BaseEntity {

    private String name;
    private String email;
    private String phone;

}