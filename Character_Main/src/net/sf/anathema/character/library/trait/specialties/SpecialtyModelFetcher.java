package net.sf.anathema.character.library.trait.specialties;

import net.sf.anathema.character.main.model.abilities.AbilityModelFetcher;
import net.sf.anathema.hero.model.Hero;

public class SpecialtyModelFetcher {

  public static ISpecialtiesConfiguration fetch(Hero hero) {
    return AbilityModelFetcher.fetch(hero).getSpecialtyConfiguration();
  }
}
