package net.sf.anathema.character.presenter;

import net.sf.anathema.character.generic.traits.types.OtherTraitType;
import net.sf.anathema.character.library.trait.ITrait;
import net.sf.anathema.character.library.trait.presenter.AbstractTraitPresenter;
import net.sf.anathema.character.model.traits.ICoreTraitConfiguration;
import net.sf.anathema.character.model.traits.essence.IEssencePoolConfiguration;
import net.sf.anathema.character.model.traits.essence.IPoolValueListener;
import net.sf.anathema.character.view.IBasicAdvantageView;
import net.sf.anathema.framework.value.IIntValueView;
import net.sf.anathema.lib.resources.IResources;
import net.sf.anathema.lib.workflow.labelledvalue.ILabelledValueView;

public class EssenceConfigurationPresenter extends AbstractTraitPresenter implements IAdvantageSubPresenter {

  private final IBasicAdvantageView view;
  private final IEssencePoolConfiguration essence;
  private final IResources resources;
  private final ICoreTraitConfiguration traitConfiguration;

  public EssenceConfigurationPresenter(
      IResources resources,
      final IEssencePoolConfiguration essence,
      final ICoreTraitConfiguration traitConfiguration,
      final IBasicAdvantageView view) {
    this.resources = resources;
    this.essence = essence;
    this.traitConfiguration = traitConfiguration;
    this.view = view;
  }

  public void init() {
    ITrait essenceTrait = traitConfiguration.getTrait(OtherTraitType.Essence);
    IIntValueView essenceView = view.addEssenceView(resources.getString("Essence.Name"), //$NON-NLS-1$
        essenceTrait.getCurrentValue(),
        essenceTrait.getMaximalValue());
    if (essence.isEssenceUser()) {
      final ILabelledValueView<String> personalView = view.addPoolView(
          resources.getString("EssencePool.Name.Personal"), essence.getPersonalPool()); //$NON-NLS-1$      
      if (essence.hasPeripheralPool()) {
        final ILabelledValueView<String> peripheralView = view.addPoolView(
            resources.getString("EssencePool.Name.Peripheral"), essence.getPeripheralPool()); //$NON-NLS-1$
        essence.addPoolListener(new IPoolValueListener() {
          public void poolsChanged() {
            personalView.setValue(essence.getPersonalPool());
            peripheralView.setValue(essence.getPeripheralPool());
          }
        });
      }
      else {
        essence.addPoolListener(new IPoolValueListener() {
          public void poolsChanged() {
            personalView.setValue(essence.getPersonalPool());
          }
        });
      }
    }
    addModelValueListener(essenceTrait, essenceView);
    addViewValueListener(essenceView, essenceTrait);
  }
}