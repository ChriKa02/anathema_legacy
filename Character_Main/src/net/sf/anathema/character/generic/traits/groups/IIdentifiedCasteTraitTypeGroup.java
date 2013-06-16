package net.sf.anathema.character.generic.traits.groups;

import net.sf.anathema.character.generic.caste.ICasteType;
import net.sf.anathema.character.generic.traits.TraitType;

public interface IIdentifiedCasteTraitTypeGroup extends IIdentifiedTraitTypeGroup {

  ICasteType[] getTraitCasteTypes(TraitType type);
}