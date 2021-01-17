package cs451.broadcast;

import cs451.Host;
import cs451.message.Message;
import cs451.session.Session;
import cs451.links.PerfectPointToPointLinks;

import java.util.List;

/**
 *
 * @author not-sure
 */
public class BestEffortBroadcast implements BroadcastI {

    Host p;
    private BroadcastI urb;
    private PerfectPointToPointLinks link;

    public BestEffortBroadcast(BroadcastI urb) {
        this.p = Session.getInstance().getCurrentProcess();
        this.urb = urb;
        link = new PerfectPointToPointLinks(this);
    }

    /**
     * beb broadcasts message
     *
     * @param message
     */
    @Override
    public void broadcast(Message message) {
        message.setSender(p.getId());
        byte[] byteMessage = message.getBytes();
        List<Host> process = Session.getInstance().getAllProcesses();
        urb.deliver(message);
        for (int i = 0; i < process.size(); i++) {
            if (process.get(i).getId() != p.getId()) {
                link.send(message, byteMessage, process.get(i));
            }
        }
    }

    /**
     *
     * delivers message
     *
     * @param m
     */
    @Override
    public void deliver(Message m) {
        urb.deliver(m);
    }
}
