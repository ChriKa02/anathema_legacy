package net.sf.anathema.framework.module;

import net.sf.anathema.framework.IApplicationModel;
import net.sf.anathema.framework.item.IItemType;
import net.sf.anathema.framework.presenter.IItemViewFactory;
import net.sf.anathema.lib.registry.IRegistry;
import net.sf.anathema.lib.resources.Resources;

public abstract class AbstractItemTypeConfiguration implements IItemTypeConfiguration {

  private final IItemType type;

  public AbstractItemTypeConfiguration(IItemType type) {
    this.type = type;
  }

  @Override
  public final void registerViewFactory(IApplicationModel anathemaModel, Resources resources) {
    IRegistry<IItemType, IItemViewFactory> viewFactoryRegistry = anathemaModel.getViewFactoryRegistry();
    viewFactoryRegistry.register(type, createItemViewFactory(anathemaModel, resources));
  }

  @Override
  public final IItemType getItemType() {
    return type;
  }

  protected abstract IItemViewFactory createItemViewFactory(IApplicationModel anathemaModel, Resources resources);
}