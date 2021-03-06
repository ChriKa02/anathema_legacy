package net.sf.anathema.hero.specialities;

import net.sf.anathema.hero.traits.model.DefaultTrait;
import net.sf.anathema.hero.traits.model.FriendlyValueChangeChecker;
import net.sf.anathema.hero.traits.model.FavorableState;
import net.sf.anathema.hero.traits.model.IFavorableStateChangedListener;
import net.sf.anathema.hero.traits.model.IncrementChecker;
import net.sf.anathema.hero.traits.model.TraitRules;
import net.sf.anathema.hero.specialties.Specialty;
import net.sf.anathema.hero.specialties.ISubTraitContainer;
import net.sf.anathema.hero.traits.model.context.CreationTraitValueStrategy;
import net.sf.anathema.hero.traits.model.context.ExperiencedTraitValueStrategy;
import net.sf.anathema.hero.traits.model.context.ProxyTraitValueStrategy;
import net.sf.anathema.hero.traits.model.types.AbilityType;
import net.sf.anathema.hero.traits.model.types.OtherTraitType;
import net.sf.anathema.hero.concept.CasteType;
import net.sf.anathema.hero.dummy.DummyCasteType;
import net.sf.anathema.hero.dummy.DummyHero;
import net.sf.anathema.hero.dummy.models.DummyHeroConcept;
import net.sf.anathema.hero.dummy.models.DummySpiritualTraitModel;
import net.sf.anathema.hero.dummy.models.DummyTraitModel;
import net.sf.anathema.hero.model.Hero;
import net.sf.anathema.hero.specialties.model.SpecialtiesContainer;
import net.sf.anathema.hero.traits.model.trait.TraitRulesImpl;
import net.sf.anathema.hero.traits.template.TraitTemplate;
import net.sf.anathema.hero.traits.template.TraitTemplateFactory;
import net.sf.anathema.lib.control.IntValueChangedListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FavorableTraitTest {

  private IncrementChecker incrementChecker = Mockito.mock(IncrementChecker.class);
  private IFavorableStateChangedListener abilityStateListener = Mockito.mock(IFavorableStateChangedListener.class);
  private ProxyTraitValueStrategy valueStrategy;
  private DefaultTrait trait;
  private DummyHero dummyHero = new DummyHero();

  @Before
  public void createTrait() throws Exception {
    this.valueStrategy = new ProxyTraitValueStrategy(new CreationTraitValueStrategy());
    DummyTraitModel traits = new DummyTraitModel();
    traits.valueStrategy = valueStrategy;
    DummySpiritualTraitModel otherTraitModel = new DummySpiritualTraitModel();
    dummyHero.addModel(otherTraitModel);
    dummyHero.addModel(new DummyHeroConcept());
    dummyHero.addModel(traits);
    otherTraitModel.getTrait(OtherTraitType.Essence).setCurrentValue(2);
    this.trait = createObjectUnderTest(dummyHero);
  }

  @Test
  public void testSetAbilityToFavored() throws Exception {
    allowOneFavoredIncrement();
    trait.getFavorization().addFavorableStateChangedListener(abilityStateListener);
    assertEquals(0, trait.getCreationValue());
    trait.getFavorization().setFavorableState(FavorableState.Favored);
    verify(abilityStateListener).favorableStateChanged(FavorableState.Favored);
    assertEquals(1, trait.getCreationValue());
  }

  private void allowOneFavoredIncrement() {
    when(incrementChecker.isValidIncrement(1)).thenReturn(true);
  }

  @Test
  public void testSetAbiltyToFavoredUnallowed() throws Exception {
    when(incrementChecker.isValidIncrement(1)).thenReturn(false);
    trait.getFavorization().setFavorableState(FavorableState.Favored);
    assertSame(FavorableState.Default, trait.getFavorization().getFavorableState());
    assertEquals(0, trait.getCreationValue());
  }

  @Test
  public void testSetFavoredAbiltyCreationValueBelow1() throws Exception {
    allowOneFavoredIncrement();
    trait.getFavorization().setFavorableState(FavorableState.Favored);
    assertTrue(trait.getFavorization().isFavored());
    trait.setCurrentValue(0);
    assertEquals(1, trait.getCreationValue());
  }

  @Test
  public void testCasteAbilityNotSetToFavored() throws Exception {
    trait.getFavorization().setFavorableState(FavorableState.Caste);
    trait.getFavorization().addFavorableStateChangedListener(abilityStateListener);
    trait.getFavorization().setFavorableState(FavorableState.Favored);
    assertSame(FavorableState.Caste, trait.getFavorization().getFavorableState());
    verifyZeroInteractions(abilityStateListener);
  }

  @Test
  public void testExceedCreationValueMaximum() throws Exception {
    trait.setCurrentValue(6);
    assertEquals(5, trait.getCreationValue());
  }

  @Test
  public void testUnderrunCreationValueMinimum() throws Exception {
    trait.setCurrentValue(-1);
    assertEquals(0, trait.getCreationValue());
  }

  private DefaultTrait createObjectUnderTest(Hero hero) {
    TraitTemplate archeryTemplate = TraitTemplateFactory.createEssenceLimitedTemplate(0);
    TraitRules rules = new TraitRulesImpl(AbilityType.Archery, archeryTemplate, hero);
    return new DefaultTrait(hero, rules, new CasteType[]{new DummyCasteType()}, new FriendlyValueChangeChecker(), incrementChecker);
  }

  @Test
  public void creationValueIsLowerBoundForExperiencedValue() throws Exception {
    trait.setCurrentValue(2);
    valueStrategy.setStrategy(new ExperiencedTraitValueStrategy());
    trait.setCurrentValue(3);
    final int[] holder = new int[1];
    trait.addCurrentValueListener(new IntValueChangedListener() {
      @Override
      public void valueChanged(int newValue) {
        holder[0] = newValue;
      }
    });
    trait.setCurrentValue(0);
    assertEquals(2, holder[0]);
  }

  @Test
  public void testSetValueTo6OnExperiencedCharacterWithoutHighEssence() throws Exception {
    valueStrategy.setStrategy(new ExperiencedTraitValueStrategy());
    trait.setCurrentValue(6);
    assertEquals(5, trait.getCurrentValue());
  }

  @Test
  public void testExperienceSpecialtyCount() throws Exception {
    ISubTraitContainer container = new SpecialtiesContainer(trait.getType(), dummyHero);
    Specialty specialty = container.addSubTrait("TestSpecialty");
    specialty.setCreationValue(1);
    valueStrategy.setStrategy(new ExperiencedTraitValueStrategy());
    specialty.setExperiencedValue(2);
    assertEquals(2, specialty.getCurrentValue());
    assertEquals(1, container.getCreationDotTotal());
    assertEquals(1, container.getExperienceDotTotal());
  }

  @Test
  public void testCreationSpecialtyDuringExperienced() throws Exception {
    ISubTraitContainer container = new SpecialtiesContainer(trait.getType(), dummyHero);
    Specialty specialty = container.addSubTrait("TestSpecialty");
    specialty.setCreationValue(2);
    assertEquals(2, specialty.getCreationValue());
    assertEquals(-1, specialty.getExperiencedValue());
    assertEquals(2, specialty.getCurrentValue());
    assertEquals(2, container.getCreationDotTotal());
    assertEquals(0, container.getExperienceDotTotal());
  }
}