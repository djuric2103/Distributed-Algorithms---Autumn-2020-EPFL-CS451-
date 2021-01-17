package cs451.broadcast;

import cs451.message.Message;
import cs451.session.Session;
import cs451.proc.ProcessModel;
import java.util.logging.Logger;
import java.util.logging.Level;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author not-sure
 */
public class FifoReliableBroadcast implements BroadcastI {

    private ProcessModel p;
    private BroadcastI urb;
    private int next[];
    private Message pending[][];
    private int numberOfProcesses;
    private int numberOfMessages;
    private int lsn;
    private long limit;

    public FifoReliableBroadcast(ProcessModel p) {
        this.p = p;
        numberOfProcesses = Session.getInstance().getNumberOfProcesses();
        numberOfMessages = Session.getInstance().getNumberOfMessages();
        next = new int[numberOfProcesses];
        limit = Session.getLimit();
        pending = new Message[numberOfProcesses][numberOfMessages];
        urb = new UniformReliableBroadcast(this);
    }

    /**
     * fb broadcast message it never has more than limit messages that are still
     * not delivered in order to not overload network
     *
     * @param m
     */
    @Override
    public void broadcast(Message m) {
        m.setMessageID(lsn);

        while (lsn >= limit + next[p.getIdentity().getId()]) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(FifoReliableBroadcast.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        lsn++;
        urb.broadcast(m);
    }

    /**
     * fb delivers message
     *
     * @param m
     */
    @Override
    public synchronized void deliver(Message m) {
        pending[m.getOriginalSender()][m.getMessageID()] = m;

        while (next[m.getOriginalSender()] < pending[0].length && pending[m.getOriginalSender()][next[m.getOriginalSender()]] != null) {
            p.deliver(pending[m.getOriginalSender()][next[m.getOriginalSender()]]);
            next[m.getOriginalSender()]++;
        }
    }
}
