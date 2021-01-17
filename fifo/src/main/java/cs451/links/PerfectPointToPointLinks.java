package cs451.links;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import cs451.Host;
import cs451.message.Message;
import cs451.session.Session;
import cs451.broadcast.BroadcastI;

/**
 *
 * @author not-sure
 */
public class PerfectPointToPointLinks {

    private BroadcastI beb;

    public PerfectPointToPointLinks(BroadcastI beb) {
        this.beb = beb;
        ListeningThread lt = new ListeningThread(this);
        Session.getInstance().addThread(lt);
        lt.start();
    }

    /**
     * Send Message
     *
     * @param message
     * @param byteMessage
     * @param pm
     */
    public void send(Message message, byte[] byteMessage, Host pm) {
        SendEvent s = new SendEvent(message, byteMessage, pm);
        Session.getInstance().addThread(s);
        s.start();
    }

    /**
     * received Message
     *
     * @param m
     */
    public synchronized void deliver(Message m) {
        beb.deliver(m);
    }
}
