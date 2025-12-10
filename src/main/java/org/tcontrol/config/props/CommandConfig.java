package org.tcontrol.config.props;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CommandConfig {
    private String name;
    private List<String> thermostats;
}
