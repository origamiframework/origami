package ru.origami.hibernate.utils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Retry {

    private long stopMaxAttempt;

    private long stopMaxDelay;

    private long wait;

    private long waitBeforeExecute;

    public Retry() {
        this.stopMaxAttempt = 10;
        this.stopMaxDelay = 30;
        this.wait = 5;
        this.waitBeforeExecute = 5;
    }

    @Override
    public String toString() {
        return String.format("attempt=%d, delay=%d, wait=%d", stopMaxAttempt, stopMaxDelay, wait);
    }
}
