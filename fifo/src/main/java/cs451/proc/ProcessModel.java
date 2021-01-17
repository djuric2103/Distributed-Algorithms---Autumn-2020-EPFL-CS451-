package cs451.proc;

import cs451.Host;
import cs451.message.Message;
import cs451.helpers.Log;
import cs451.session.Session;
import cs451.broadcast.BroadcastI;
import cs451.broadcast.FifoReliableBroadcast;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author not-sure
 */
public class ProcessModel {

    private BroadcastI fb_lcb;
    private Log log;
    private Host identity;
    private boolean stop = false;
    private int minSize, writeen = 0;

    public ProcessModel(Host identity, String logPath) {
        this.identity = identity;
        // if (method.equals("FIFO")) {
        fb_lcb = new FifoReliableBroadcast(this);
        //} else {
        //    fb_lcb = new LocalizedCasualBroadcast(this);
        //}
        log = new Log(logPath);
        minSize = (Session.getNumberOfProcesses() / 2 + 1) * Session.getNumberOfMessages();
    }

    public Host getIdentity() {
        return identity;
    }

    public void setIdentity(Host identity) {
        this.identity = identity;
    }

    /**
     * Sends all Messages and add it to log
     */
    public void run() {
        for (int i = 1; i <= Session.getInstance().getNumberOfMessages(); i++) {
            if(stop) return;
            Message m = new Message();
            m.setOriginalSender(identity.getId());
            //m.setMessageID(i - 1);
            log.addToLog("b " + i);
            fb_lcb.broadcast(m);
        }
        while(writeen < minSize){
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(FifoReliableBroadcast.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void stop() {
        stop = true;
    }

    /**
     * Add delivered message to log
     *
     * @param m
     */
    public synchronized void deliver(Message m) {
        ++writeen;
        log.addToLog("d " + (m.getOriginalSender() + 1) + " " + (m.getMessageID() + 1));
    }

    public void writeOutput() {
        log.writeToFile();
    }
}
