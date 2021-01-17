package cs451.session;

import cs451.Host;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author not-sure
 */
public class Session {

    private static Session instance;
    private List<Host> allProcesses;
    private static int numberOfProcesses;
    private static int numberOfMessages;
    private boolean[][][] ack;
    private boolean[][][] received;
    private DatagramSocket[] sockets;
    private Host currentProcess;
    private ConcurrentLinkedQueue<Thread> threads;
    private DatagramSocket ss;

    private Session() {
    }

    public static Session getInstance() {
        return instance = instance == null ? new Session() : instance;
    }

    public void init(){
        sockets = new DatagramSocket[numberOfProcesses];
        ack = new boolean[numberOfMessages][numberOfProcesses][numberOfProcesses];
        received = new boolean[numberOfMessages][numberOfProcesses][numberOfProcesses];
        threads = new ConcurrentLinkedQueue<>();
        for(int k = 0; k < numberOfProcesses; k++){
            try {
                sockets[k] = new DatagramSocket();
            } catch (SocketException ex) {
                Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setAllProcesses(List<Host> allProcesses) {
        this.allProcesses = allProcesses;
    }

    public List<Host> getAllProcesses() {
        return allProcesses;
    }

    public static int getNumberOfProcesses() {
        return numberOfProcesses;
    }

    public static void setNumberOfProcesses(int nOP) {
        numberOfProcesses = nOP;
    }

    //public void addProcess(Host p) {allProcesses.set(p.getId(), p);}

    public static int getNumberOfMessages() {
        return numberOfMessages;
    }

    public static void setNumberOfMessages(int nOM) {
        numberOfMessages = nOM;
    }

    public boolean[][][] getAck() {
        return ack;
    }

    public boolean[][][] getReceived() {
        return received;
    }

    public DatagramSocket[] getSockets() {
        return sockets;
    }

    public Host getCurrentProcess() {
        return currentProcess;
    }

    public void setCurrentProcess(int id) {
        this.currentProcess = allProcesses.get(id);
    }

    public void addThread(Thread t) {
        threads.add(t);
    }

    public void interruptAll() {
        for (Thread t : threads) {
            t.interrupt();
        }
    }

    public void closeAll() {
        for (DatagramSocket d : sockets) {
            d.close();
        }
        ss.close();
    }

    public static long getLimit() {
        return Math.max(3, Math.round(Math.floor(40.0/numberOfProcesses)));
    }

    public void setServerSocket(DatagramSocket ss) {
        this.ss = ss;
    }
}
