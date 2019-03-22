package fr.stevenhry.maskinterface.util;

import javafx.application.Platform;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;

public class TimeCalculator {

    private long start, end;
    private boolean run = true;
    private final Thread timeCalculatorThread;

    public TimeCalculator(){
        timeCalculatorThread = new Thread(() -> {
            while(run) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) { e.printStackTrace(); }
                end = System.currentTimeMillis();
            }
        });
    }

    /**
     * Starts the thread and defines start variable
     */
    public void start(){
        start = System.currentTimeMillis();
        timeCalculatorThread.start();
    }

    /**
     * Stops the thread and defines the end variable
     */
    public void stop(){
        run = false;
        end = System.currentTimeMillis();
    }

    /**
     * @return the current passed time in millisecond
     */
    public long getCurrentPassedTime(){
        long passed = end - start;
        return passed > 0 ? passed : 1l;
    }

    /**
     * @return a new Thread which refreshes the label time each millisecond while calculation is in progress
     */
    public Thread startProcess(Text timeLabel, Thread calculationThread){
        Thread thread = new Thread(() -> {
            calculationThread.start();
            while (calculationThread.isAlive()) {
                Platform.runLater(() -> {
                    String time = new SimpleDateFormat("mm:ss:SSS").format(getCurrentPassedTime());
                    timeLabel.setText(time);
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
