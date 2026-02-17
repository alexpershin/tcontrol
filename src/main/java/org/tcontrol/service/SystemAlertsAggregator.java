package org.tcontrol.service;

import org.tcontrol.models.SystemAlert;

import java.util.List;

public interface SystemAlertsAggregator {
    void addAlert(String message);
    void addAlert(long timestamp, String message);
    List<SystemAlert> alerts();

    List<SystemAlert> actualAlerts();
}
