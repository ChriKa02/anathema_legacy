package net.sf.anathema.character.equipment.creation.presenter;

import net.sf.anathema.hero.health.HealthType;
import net.sf.anathema.lib.workflow.booleanvalue.BooleanValueModel;

public interface IArmourStatisticsModel extends IEquipmentStatisticsModel {

  IIntValueModel getBashingSoakModel();

  IIntValueModel getLethalSoakModel();

  IIntValueModel getBashingHardnessModel();

  IIntValueModel getLethalHardnessModel();

  /** Mobility penalty is modelled as negativ number. */
  IIntValueModel getMobilityPenaltyModel();

  IIntValueModel getFatigueModel();

  IIntValueModel getSoakModel(HealthType healthType);

  IIntValueModel getHardnessModel(HealthType healthType);

  IIntValueModel getAggravatedSoakModel();
  
  BooleanValueModel getSoakLinkModel();
}