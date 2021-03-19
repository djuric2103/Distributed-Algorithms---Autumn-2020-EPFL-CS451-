package cs451.links;

import cs451.Host;
import cs451.session.Session;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author not-sure
 */
public class ListeningThread extends Thread {

    private DatagramSocket ss;
    private PerfectPointToPointLinks links;
    private Host p;

    public ListeningThread(PerfectPointToPointLinks links) {
        this.links = links;
        p = Session.getInstance().getCurrentProcess();
        try {
            ss = new DatagramSocket(p.getPort(), p.getIP());
            Session.getInstance().setServerSocket(ss);
        } catch (SocketException ex) {
            Logger.getLogger(ListeningThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Listening for new messages
     */
    @Override
    public void run() {
        while (true) {
            try {
                byte[] receive = new byte[2048];
                DatagramPacket DpReceive = new DatagramPacket(receive, receive.length);
                ss.receive(DpReceive);
                DeliverEvent d = new DeliverEvent(p, links, receive);
                Session.getInstance().addThread(d);
                d.start();
            } catch (IOException ex) {
                Logger.getLogger(PerfectPointToPointLinks.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
