package io.kettil.fluxtest;

import lombok.Value;

@Value
public class Price {
    private long timestamp;
    private double value;
}
