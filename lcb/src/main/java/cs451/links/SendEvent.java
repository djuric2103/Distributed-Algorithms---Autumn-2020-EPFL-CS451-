package cs451.links;

import cs451.Host;
import cs451.message.Message;
import cs451.helpers.RTT;
import cs451.session.Session;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author not-sure
 */
public class SendEvent extends Thread {

    private Message message;
    private byte[] byteMessage;
    private Host pm;
    private DatagramSocket[] sockets = Session.getInstance().getSockets();
    private boolean[][][] ack = Session.getInstance().getAck();
    private long timeoutInterval;

    public SendEvent(Message m, byte[] byteMessage, Host pm) {
        this.message = m;
        this.byteMessage = byteMessage;
        this.pm = pm;
    }

    @Override
    public void run() {
        if (message.isAck()) {
            sendAck();
        } else {
            send();
        }
    }

    /**
     * Sends ack for received message
     */
    private void sendAck() {
        try {
            DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, pm.getIP(), pm.getPort());

            sockets[pm.getId()].send(packet);
        } catch (SocketException ex) {
            //Logger.getLogger(PerfectPointToPointLinks.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //Logger.getLogger(PerfectPointToPointLinks.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sending message while it does not received ack Resending of messages is
     * determined on the time estimated in RTT.java
     */
    private void send() {
        try {
            if (sockets[pm.getId()] == null) {
                sockets[pm.getId()] = new DatagramSocket();
            }

            DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, pm.getIP(), pm.getPort());

            long timeoutInterval = Math.round(RTT.getInstance().getTimeoutInterval(pm.getId()));
            long currentTimeout = 0;
            double duration = 0;

            while (duration == currentTimeout) {
                currentTimeout = Math.min(Math.max(timeoutInterval, 2 * currentTimeout), 1000);
                sockets[pm.getId()].send(packet);
                duration = waitForACK(currentTimeout);
            }
            /*
                RTT is updated only when message is sent only once
             */
            if (currentTimeout == timeoutInterval) {
                RTT.getInstance().updateTimeoutInterval(duration, pm.getId());
            }

        } catch (IOException ex) {
            Logger.getLogger(ListeningThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * waits for ACK for timeout milisecounds if ack is received it will return
     * time after which is received otherwise it will return timeout interval
     *
     * @param timeout
     * @return
     */
    private double waitForACK(double timeout) {
        long startTime = System.nanoTime();
        long endTime = System.nanoTime();

        while (!ack[message.getMessageID()][message.getOriginalSender()][pm.getId()] && getMilisecounds(startTime, endTime) < timeout) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                //Logger.getLogger(SendEvent.class.getName()).log(Level.SEVERE, null, ex);
            }
            endTime = System.nanoTime();
        }

        return Math.min(getMilisecounds(startTime, endTime), timeout);
    }

    /**
     * returns time passed between startTime and endTime in milisecound
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private double getMilisecounds(long startTime, long endTime) {
        return (endTime - startTime) / 1000000;
    }

    /**
     * @return the m
     */
    public Message getM() {
        return message;
    }

    /**
     * @param m the m to set
     */
    public void setM(Message m) {
        this.message = m;
    }

    /**
     * @return the byteMessage
     */
    public byte[] getByteMessage() {
        return byteMessage;
    }

    /**
     * @param byteMessage the byteMessage to set
     */
    public void setByteMessage(byte[] byteMessage) {
        this.byteMessage = byteMessage;
    }

    /**
     * @return the pm
     */
    public Host getPm() {
        return pm;
    }

    /**
     * @param pm the pm to set
     */
    public void setPm(Host pm) {
        this.pm = pm;
    }

}
