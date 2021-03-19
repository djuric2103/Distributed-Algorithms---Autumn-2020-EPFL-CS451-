package cs451.proc;

import java.util.ArrayList;
import java.util.List;

import cs451.session.Session;

/**
 *
 * @author not-sure
 */
public class Affected {

    public static Affected instance;
    //elements in list affecting[i] represent all processes affected by process pi
    private ArrayList<Integer>[] affecting;

    private Affected() {
        affecting = new ArrayList[Session.getNumberOfProcesses()];
        for (int i = 0; i < affecting.length; i++) {
            affecting[i] = new ArrayList<>();
        }
    }

    public static Affected getInstance() {
        return instance = instance == null ? new Affected() : instance;
    }

    public void addAffecting(int procAffecting, int procAffected) {
        affecting[procAffecting].add(procAffected);
    }

    public List<Integer> returnAffectedProcesses(int processAffecting) {
        return affecting[processAffecting];
    }
}
