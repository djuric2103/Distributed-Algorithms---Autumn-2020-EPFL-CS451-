package cs451.broadcast;

import java.util.List;

import cs451.Host;
import cs451.message.Message;
import cs451.proc.Affected;
import cs451.proc.ProcessModel;
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
public class LocalizedCasualBroadcast implements BroadcastI {

    private ProcessModel p;
    private BroadcastI urb;
    private int[] V;
    private int lsn;
    private int numberOfProcesses;
    private int numberOfMessages;
    private long limit;
    private Message[][] pending;
    private List<Integer> current_affected_by;

    public LocalizedCasualBroadcast(ProcessModel p) {
        this.p = p;
        urb = new UniformReliableBroadcast(this);
        numberOfProcesses = Session.getInstance().getNumberOfProcesses();
        numberOfMessages = Session.getInstance().getNumberOfMessages();
        V = new int[numberOfProcesses];
        lsn = 0;
        limit = Session.getLimit();
        //System.out.println(limit);
        pending = new Message[numberOfProcesses][numberOfMessages];
        current_affected_by = Session.getInstance().getAllProcesses().get(p.getIdentity().getId()).getAffected();
    }

    /**
     * LCB broadcast message
     *
     * @param message
     */
    @Override
    public void broadcast(Message message) {
        //int[] W = V.clone();
        //W[p.getIdentity().getId()] = lsn++;
        message.setMessageID(lsn);
        int[] W = new int[current_affected_by.size()];
        for(int i = 0; i < W.length; ++i){
            W[i] = V[current_affected_by.get(i)];
        }
        W[0] = lsn++;

        message.setClock(W);

        while (lsn >= limit + V[p.getIdentity().getId()]) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                //Logger.getLogger(LocalizedCasualBroadcast.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        urb.broadcast(message);
    }

    /**
     * *
     * urb delivers message and checking is there any message which can be LCB
     * delivered starting with checking messages from original sender of just
     * delivered one
     **
     */
    @Override
    public synchronized void deliver(Message m) {
        pending[m.getOriginalSender()][m.getMessageID()] = m;
        checkPending(m.getOriginalSender());
    }

    /**
     * checking for delivering messages which original sender is procPending,
     * and if some of messages is delivered then will recursively check all
     * processes which are affected by him
     *
     * @param procPending
     */
    private void checkPending(int procPending) {
        boolean del = false;
        for (int i = V[procPending]; i < numberOfMessages && pending[procPending][i] != null && checkClock(pending[procPending][i]); ++i) {
            del = true;
            p.deliver(pending[procPending][i]);
            ++V[procPending];
        }
        if (del) {
            for (Integer forChecking : Affected.getInstance().returnAffectedProcesses(procPending)) {
                checkPending(forChecking);
            }
        }
    }

    /**
     * *
     * checking clock of message which is urb delivered with clock or process,
     * and it checks only for processes that affecting original sender of
     * message
     **
     */
    private boolean checkClock(Message m) {
        Host origSend = Session.getInstance().getAllProcesses().get(m.getOriginalSender());
        List<Integer> origSendAffectedBy = origSend.getAffected();
        int[] messageClock = m.getClock();
        for(int i = 0; i < origSendAffectedBy.size(); ++i){
            if(messageClock[i] > V[origSendAffectedBy.get(i)])
                return false;
        }
        return true;
        /*
        Host origSend = Session.getInstance().getAllProcesses().get(m.getOriginalSender());
        for (int affectedBy : origSend.getAffected()) {
            if (m.getClock()[affectedBy] > V[affectedBy]) {
                return false;
            }
        }
        return true;*/
    }
}