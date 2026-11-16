package org.tcontrol.service;

import org.tcontrol.models.SystemAlert;

import java.util.Collection;

public interface SystemAlertsAggregator {
    void addAlert(String message);
    void addAlert(long timestamp, String message);
    Collection<SystemAlert> alerts();
    void setStatisticsManager(IStatisticsManager statisticsManager);
}
