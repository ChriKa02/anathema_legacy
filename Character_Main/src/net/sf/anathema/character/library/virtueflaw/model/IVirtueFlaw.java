package net.sf.anathema.character.library.virtueflaw.model;

import net.sf.anathema.character.generic.traits.TraitType;
import net.sf.anathema.character.library.trait.Trait;
import net.sf.anathema.lib.control.IChangeListener;
import net.sf.anathema.lib.workflow.textualdescription.ITextualDescription;

public interface IVirtueFlaw {
  TraitType getRoot();

  void setRoot(TraitType root);

  ITextualDescription getName();

  boolean isFlawComplete();

  Trait getLimitTrait();

  void addRootChangeListener(IChangeListener listener);
}