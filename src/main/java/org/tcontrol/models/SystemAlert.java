package org.tcontrol.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public final class SystemAlert {
    final long timestamp;
    final String message;
}
