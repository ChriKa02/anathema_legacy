package net.sf.anathema.character.presenter.charm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.anathema.character.generic.framework.additionaltemplate.listening.DedicatedCharacterChangeAdapter;
import net.sf.anathema.character.generic.framework.magic.stringbuilder.IMagicSourceStringBuilder;
import net.sf.anathema.character.generic.framework.magic.stringbuilder.MagicInfoStringBuilder;
import net.sf.anathema.character.generic.framework.magic.stringbuilder.MagicSourceStringBuilder;
import net.sf.anathema.character.generic.framework.magic.stringbuilder.ScreenDisplayInfoStringBuilder;
import net.sf.anathema.character.generic.framework.magic.view.IMagicViewListener;
import net.sf.anathema.character.generic.magic.ISpell;
import net.sf.anathema.character.generic.magic.spells.CircleType;
import net.sf.anathema.character.generic.template.ICharacterTemplate;
import net.sf.anathema.character.model.ICharacterStatistics;
import net.sf.anathema.character.model.ISpellConfiguration;
import net.sf.anathema.character.model.ISpellModelListener;
import net.sf.anathema.character.presenter.TabContent;
import net.sf.anathema.character.view.magic.IMagicViewFactory;
import net.sf.anathema.character.view.magic.ISpellView;
import net.sf.anathema.lib.compare.I18nedIdentificateSorter;
import net.sf.anathema.lib.control.objectvalue.IObjectValueChangedListener;
import net.sf.anathema.lib.resources.IResources;
import net.sf.anathema.lib.util.IIdentificate;
import net.sf.anathema.lib.util.Identificate;

public abstract class SpellPresenter implements IMagicSubPresenter {

  private final ISpellConfiguration spellConfiguration;
  private final ICharacterStatistics statistics;
  private final MagicInfoStringBuilder creator;
  private final ICharacterTemplate characterTemplate;
  private final IResources resources;
  private CircleType circle;
  private final IMagicSourceStringBuilder sourceStringBuilder;

  public SpellPresenter(ICharacterStatistics statistics, IResources resources) {
    this.statistics = statistics;
    this.resources = resources;
    this.creator = new ScreenDisplayInfoStringBuilder(resources);
    this.sourceStringBuilder = new MagicSourceStringBuilder(resources);
    this.spellConfiguration = statistics.getSpells();
    this.characterTemplate = statistics.getCharacterTemplate();
  }

  public TabContent init(IMagicViewFactory magicView) {
    final ISpellView view = magicView.createSpellView();
    IIdentificate[] circles;
    IIdentificate[] allowedCircles = getCircles();
    circles = new IIdentificate[allowedCircles.length + 1];
    circles[0] = new Identificate("AllCircles"); //$NON-NLS-1$
    System.arraycopy(allowedCircles, 0, circles, 1, allowedCircles.length);

    view.initGui(circles, new SpellViewProperties(resources, statistics));
    view.addSpellViewListener(new IMagicViewListener() {
      public void magicRemoved(Object[] removedSpells) {
        List<ISpell> spellList = new ArrayList<ISpell>();
        for (Object spellObject : removedSpells) {
          spellList.add((ISpell) spellObject);
        }
        spellConfiguration.removeSpells(spellList.toArray(new ISpell[spellList.size()]));
      }

      public void magicAdded(Object[] addedSpells) {
        List<ISpell> spellList = new ArrayList<ISpell>();
        for (Object spellObject : addedSpells) {
          spellList.add((ISpell) spellObject);
        }
        spellConfiguration.addSpells(spellList.toArray(new ISpell[0]));
      }
    });
    view.addCircleSelectionListener(new IObjectValueChangedListener() {
      public void valueChanged(Object oldValue, Object newValue) {
        if (newValue instanceof CircleType) {
          circle = (CircleType) newValue;
        }
        else {
          circle = null;
        }
        view.setAllSpells(getSpellsToShow());
      }
    });
    spellConfiguration.addSpellListener(new ISpellModelListener() {
      public void spellsChanged() {
        updateSpellListsInView(view);
      }
    });
    updateSpellListsInView(view);
    view.addSpellSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        updateSpellDetails((ISpell) ((JList) e.getSource()).getSelectedValue(), view);
      }
    });
    statistics.getCharacterContext().getCharacterListening().addChangeListener(new DedicatedCharacterChangeAdapter() {
      public void experiencedChanged(boolean experienced) {
        view.clearSelection();
      }
    });
    String header = resources.getString(getTabTitleResourceKey());
    return new TabContent(header, view);
  }

  protected abstract CircleType[] getCircles();

  protected abstract String getTabTitleResourceKey();

  private void updateSpellDetails(ISpell spell, ISpellView view) {
    if (spell == null) {
      return;
    }
    String name = resources.getString(spell.getId());
    String circleName = resources.getString(spell.getCircleType().getId());
    view.setSpellDetails(name, circleName, creator.createCostString(spell), sourceStringBuilder.createSourceString(
        spell,
        false));
  }

  private void updateSpellListsInView(final ISpellView spellView) {
    spellView.setLearnedSpells(getCircleFilteredSpellList(spellConfiguration.getLearnedSpells()).toArray(new ISpell[0]));
    spellView.setAllSpells(getSpellsToShow());
  }

  private ISpell[] getSpellsToShow() {
    List<ISpell> showSpells = new ArrayList<ISpell>();
    if (circle == null) {
      showSpells = getCircleFilteredSpellList(spellConfiguration.getAllSpells());
    }
    else {
      showSpells.addAll(Arrays.asList(spellConfiguration.getSpellsByCircle(circle)));
    }
    showSpells.removeAll(Arrays.asList(spellConfiguration.getLearnedSpells()));
    int count = showSpells.size();
    ISpell[] sortedSpells = new ISpell[count];
    sortedSpells = new I18nedIdentificateSorter<ISpell>().sortAscending(
        showSpells.toArray(new ISpell[count]),
        sortedSpells,
        resources);
    return sortedSpells;
  }

  private List<ISpell> getCircleFilteredSpellList(ISpell[] spells) {
    List<ISpell> spellList = new ArrayList<ISpell>();
    for (ISpell spell : spells) {
      for (CircleType type : getCircles()) {
        if (spell.getCircleType() == type) {
          spellList.add(spell);
        }
      }
    }
    return spellList;
  }

  protected ICharacterTemplate getCharacterTemplate() {
    return characterTemplate;
  }
}