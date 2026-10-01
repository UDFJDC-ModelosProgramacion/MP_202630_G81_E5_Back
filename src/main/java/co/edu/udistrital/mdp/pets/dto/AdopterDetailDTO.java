package co.edu.udistrital.mdp.pets.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdopterDetailDTO extends AdopterDTO {

    private List<AdoptionDTO> adoptions = new ArrayList<>();
    private List<MessageDTO> messages = new ArrayList<>();
    private List<ReviewDTO> reviews = new ArrayList<>();

}
