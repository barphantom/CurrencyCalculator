package com.example.currencycalculator.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RatesUpdater {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Runnable task;

    public RatesUpdater(Runnable task) {
        this.task = task;
    }

    public void startScheduler() {
        scheduler.scheduleAtFixedRate(task, 0, 10, TimeUnit.MINUTES);
    }

    public void stopScheduler() {
        scheduler.shutdown();
    }
}
