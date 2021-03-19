package cs451.links;

import cs451.Host;
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
public class DeliverEvent extends Thread {

    private Host p;
    private PerfectPointToPointLinks links;
    private byte[] receive;
    private boolean[][][] ack;
    private boolean[][][] received;

    public DeliverEvent(Host p, PerfectPointToPointLinks links, byte[] receive) {
        this.p = p;
        this.links = links;
        this.receive = receive;
        this.ack = Session.getInstance().getAck();
        this.received = Session.getInstance().getReceived();
    }

    /**
     * checking whether or not message is ack and perform suitable action
     */
    @Override
    public void run() {
        Message m = Message.getMessage(receive);
        if (m.isAck()) {
            ack[m.getMessageID()][m.getOriginalSender()][m.getSender()] = true;
        } else {
            sendAck(m);
            if (!received[m.getMessageID()][m.getOriginalSender()][m.getSender()]) {
                received[m.getMessageID()][m.getOriginalSender()][m.getSender()] = true;
                links.deliver(m);
            }
        }
    }

    /**
     * sending ack after message is received
     *
     * @param message
     */
    private void sendAck(Message message) {
        Host sender = Session.getInstance().getAllProcesses().get(message.getSender());
        Message m = new Message(message.getOriginalSender(), p.getId(), message.getMessageID());
        m.setAck(true);
        byte[] byteMessage = m.getBytes();

        SendEvent s = new SendEvent(m, byteMessage, sender);
        Session.getInstance().addThread(s);
        s.start();

    }

}
