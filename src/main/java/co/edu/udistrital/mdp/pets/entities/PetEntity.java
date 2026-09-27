package co.edu.udistrital.mdp.pets.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;
import lombok.ToString ;
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class PetEntity extends BaseEntity {
    
    private String name;
    private String species;
    private String breed;
    private int age;
    private String sex;
    private String size;
    private String temperament;
    private String specialNeeds;
    public boolean available;

    @ToString.Exclude 
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "shelter_id")
    private ShelterEntity shelter;  

    @ToString.Exclude 
    @PodamExclude
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "vaccination_record_id")
    private VaccinationRecordEntity vaccinationRecord;

    @ToString.Exclude 
    @PodamExclude 
    @OneToMany (mappedBy = "pet", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<AdoptionEntity> adoptions = new ArrayList<>();

    @ToString.Exclude 
    @PodamExclude 
    @OneToMany (mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LifeEventEntity> lifeEvents = new ArrayList<>();

    @ToString.Exclude 
    @PodamExclude 
    @OneToMany (mappedBy = "pet", cascade = CascadeType.PERSIST)
    private List<PhotoEntity> photos = new ArrayList<>();
}
