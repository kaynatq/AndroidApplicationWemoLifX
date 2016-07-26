package edu.usfca.kquayyum.wemoandlifxcontrol;

import java.util.Timer;
import java.util.TimerTask;


public class Reminder {
    Timer timer;

    public Reminder(LifXListener lifXListener, int seconds) {
        timer = new Timer();
        timer.schedule(new RemindTask(lifXListener), seconds*1000);
    }

    class RemindTask extends TimerTask {
        LifXListener lifXListener;
        RemindTask(LifXListener weMoListener) {
            this.lifXListener = weMoListener;
        }
        public void run() {
            lifXListener.terminate();
            timer.cancel(); //Terminate the timer thread
        }
    }
}