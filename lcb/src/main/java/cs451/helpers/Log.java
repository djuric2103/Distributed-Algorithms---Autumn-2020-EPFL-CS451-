package cs451.helpers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author not-sure
 */
public class Log {

    private ConcurrentLinkedDeque<String> log;
    private String path;

    public Log(String path) {
        log = new ConcurrentLinkedDeque<>();
        this.path = path;
    }

    /**
     * adding line to log
     *
     * @param m
     */
    public synchronized void addToLog(String m) {
        if (m != null && !m.isEmpty()) {
            log.add(m);
        }
    }

    /**
     * writing log of process in output file
     */
    public void writeToFile() {
        try (BufferedWriter f = new BufferedWriter(new FileWriter(path))) {
         //System.out.println(log.size());
            for (String m : log) {
                f.write(m + "\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getSize(){
        return log.size();
    }
}
