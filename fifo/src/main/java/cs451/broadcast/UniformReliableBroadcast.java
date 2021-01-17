package cs451.broadcast;

import cs451.message.Message;
import cs451.session.Session;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author not-sure
 */
public class UniformReliableBroadcast implements BroadcastI {

    private BroadcastI fb;
    private BroadcastI beb;
    private boolean pending[][];
    private boolean delivered[][];
    private int numberOfMessages;
    private int numberOfProcesses;
    private int ackNum[][];
    private boolean ack[][][];

    public UniformReliableBroadcast(BroadcastI fb) {
        this.fb = fb;
        beb = new BestEffortBroadcast(this);
        numberOfMessages = Session.getInstance().getNumberOfMessages();
        numberOfProcesses = Session.getInstance().getNumberOfProcesses();
        pending = new boolean[numberOfMessages][numberOfProcesses];
        delivered = new boolean[numberOfMessages][numberOfProcesses];
        ack = new boolean[numberOfMessages][numberOfProcesses][numberOfProcesses];
        ackNum = new int[numberOfMessages][numberOfProcesses];
    }

    /**
     * urb broadcast Message
     *
     * @param m
     */
    @Override
    public void broadcast(Message m) {
        pending[m.getMessageID()][m.getOriginalSender()] = true;
        beb.broadcast(m);
    }

    /**
     * beb delivers message checking using method check whether or not message
     * can be urb delivered
     *
     * @param m
     */
    @Override
    public void deliver(Message m) {
        if (!ack[m.getMessageID()][m.getOriginalSender()][m.getSender()]) {
            ack[m.getMessageID()][m.getOriginalSender()][m.getSender()] = true;
            ackNum[m.getMessageID()][m.getOriginalSender()]++;
            check(m);
        }
        if (!pending[m.getMessageID()][m.getOriginalSender()]) {
            pending[m.getMessageID()][m.getOriginalSender()] = true;
            beb.broadcast(m);
        }
    }

    private void check(Message m) {
        if (pending[m.getMessageID()][m.getOriginalSender()] && candeliver(m) && !delivered[m.getMessageID()][m.getOriginalSender()]) {
            delivered[m.getMessageID()][m.getOriginalSender()] = true;
            fb.deliver(m);
        }
    }

    private boolean candeliver(Message m) {
        return ackNum[m.getMessageID()][m.getOriginalSender()] > numberOfProcesses / 2;
    }
}
