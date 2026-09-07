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
public class VeterinarianEntity extends BaseEntity {
 
    private String name;
    private String email;
    private String phone;
    private String specialty;
    private String availability;
 
    @PodamExclude
    @OneToMany(mappedBy = "veterinarian", cascade = CascadeType.PERSIST)
    private List<LifeEventEntity> registeredEvents = new ArrayList<>();
 
    @PodamExclude
    @OneToMany(mappedBy = "responsibleVeterinarian", cascade = CascadeType.PERSIST)
    private List<AdoptionEntity> adoptions = new ArrayList<>();
}