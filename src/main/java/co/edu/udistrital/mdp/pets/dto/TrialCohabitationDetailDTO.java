package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TrialCohabitation no tiene asociaciones propias (la relacion 1 a 1 con
 * Adoption se define en AdoptionDTO), por eso el detalle no agrega campos.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TrialCohabitationDetailDTO extends TrialCohabitationDTO {

}
