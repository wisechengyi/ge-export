package com.gradle.apiexport;

import java.time.Duration;
import java.time.Instant;


class Timer {

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Instant finishTime) {
        this.finishTime = finishTime;
    }

    private Instant startTime;
    private Instant finishTime;

    double durationInSec() {
        return Duration.between(startTime, finishTime).toMillis() / 1000;
    }

    @Override
    public String toString() {
        return "Timer{" +
                "startTime=" + startTime +
                ", finishTime=" + finishTime +
                '}';
    }
}
