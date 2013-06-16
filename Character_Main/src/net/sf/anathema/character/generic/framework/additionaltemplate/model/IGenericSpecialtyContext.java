package net.sf.anathema.character.generic.framework.additionaltemplate.model;

import net.sf.anathema.character.generic.traits.TraitType;
import net.sf.anathema.character.library.trait.specialties.Specialty;
import net.sf.anathema.lib.control.IChangeListener;

public interface IGenericSpecialtyContext {

  Specialty[] getSpecialties(TraitType traitType);

  void addSpecialtyListChangeListener(IChangeListener listener);
}
