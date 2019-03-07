package fr.stevenhry.maskinterface.util;

import javafx.application.Platform;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;

public class TimeCalculator {

    private long start, end;
    private final Thread timeCalculatorThread;

    public TimeCalculator(){
        timeCalculatorThread = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) { e.printStackTrace(); }
                end = System.currentTimeMillis();
            }
        });
    }

    public void start(){
        start = System.currentTimeMillis();
        timeCalculatorThread.start();
    }

    public void stop(){
        end = System.currentTimeMillis();
        timeCalculatorThread.stop();
    }

    public long getCurrentPassedTime(){
        long passed = end - start;
        return passed > 0 ? passed : 1;
    }

    public Thread startProcess(Text timeLabel, Thread calculationThread){
        Thread thread = new Thread(() -> {
            calculationThread.start();
            while (calculationThread.isAlive()) {
                Platform.runLater(() -> {
                    timeLabel.setText(new SimpleDateFormat("mm:ss:SSS").format(getCurrentPassedTime()));
                });
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
        });
        thread.start();
        return thread;
    }
}
