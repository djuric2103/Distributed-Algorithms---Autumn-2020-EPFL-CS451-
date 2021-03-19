package cs451;

import cs451.proc.Affected;
import cs451.proc.ProcessModel;
import cs451.session.Session;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    static ProcessModel p;
    private static void handleSignal() {
        //immediately stop network packet processing
        System.out.println("Immediately stopping network packet processing.");
        p.stop();
        try {
            Session.getInstance().interruptAll();
            Session.getInstance().closeAll();
        }catch (Exception e){
            e.printStackTrace();
        }
        //write/flush output file if necessary
        System.out.println("Writing output.");
        p.writeOutput();
    }

    private static void initSignalHandlers() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                handleSignal();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        Parser parser = new Parser(args);
        parser.parse();

        initSignalHandlers();
        System.out.println(parser.hasConfig());
        // example
        long pid = ProcessHandle.current().pid();
        System.out.println("My PID is " + pid + ".");
        System.out.println("Use 'kill -SIGINT " + pid + " ' or 'kill -SIGTERM " + pid + " ' to stop processing packets.");

        System.out.println("My id is " + parser.myId() + ".");
        System.out.println("List of hosts is:");
        for (Host host : parser.hosts()) {
            System.out.println((host.getId() + 1) + ", " + host.getIp() + ", " + host.getPort());
        }

        System.out.println("Barrier: " + parser.barrierIp() + ":" + parser.barrierPort());
        System.out.println("Signal: " + parser.signalIp() + ":" + parser.signalPort());
        System.out.println("Output: " + parser.output());
        // if config is defined; always check before parser.config()
        if (parser.hasConfig()) {
            System.out.println("Config: " + parser.config());
        }


        Coordinator coordinator = new Coordinator(parser.myId(), parser.barrierIp(), parser.barrierPort(), parser.signalIp(), parser.signalPort());

        System.out.println("Waiting for all processes for finish initialization");
        coordinator.waitOnBarrier();

        System.out.println("Broadcasting messages...");
        initSessionLCB(parser);
        p.run();

        Thread.sleep(300);
        System.out.println("Signaling end of broadcasting messages");
        coordinator.finishedBroadcasting();

        while (true) {
            // Sleep for 1 hour
            Thread.sleep(60 * 60 * 1000);
        }
    }

    private static void printAFF() {
        System.out.println("\n\n IS AFFECTED");
        for(Host h : Session.getInstance().getAllProcesses()){
            System.out.println(h.getId() + ": [" + h.getAffected().toString()+"]");
        }

        System.out.println("\n\n AFFECTING");
        for(int i = 0; i < Session.getInstance().getNumberOfProcesses(); ++i){
            System.out.println(Session.getInstance().getAllProcesses().get(i).getId() + ": {"+Affected.getInstance().returnAffectedProcesses(i)+"}");
        }
    }

    private static void initSessionLCB(Parser parser) {
        Session.getInstance().setNumberOfProcesses(parser.hosts().size());
        Session.getInstance().setAllProcesses(parser.hosts());
        Session.getInstance().setCurrentProcess(parser.myId() - 1);
        if(parser.hasConfig()){
            try (BufferedReader br = new BufferedReader(new FileReader(parser.config()))) {
                int numOfMessages = Integer.parseInt(br.readLine().trim());
                Session.setNumberOfMessages(numOfMessages);
                List<Host> allProcesses = Session.getInstance().getAllProcesses();
                for(int i = 0; i < allProcesses.size(); ++i){
                    String[] entry = br.readLine().split(" ");
                    List<Integer> affected = new ArrayList<>();
                    int curr = Integer.parseInt(entry[0]) - 1;
                    for (int j = 0; j < entry.length; ++j) {
                        affected.add(Integer.parseInt(entry[j]) - 1);
                        //affecting[j] affects i
                        Affected.getInstance().addAffecting(affected.get(j), curr);
                    }
                    //set to process i all processes that are affecting it
                    allProcesses.get(curr).setAffected(affected);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Config file is not correctly written");
        }
        Session.getInstance().init();
        //printAFF();

        p = new ProcessModel(Session.getInstance().getCurrentProcess(), parser.output());
    }

    private static void initSession(Parser parser) {
        Session.getInstance().setNumberOfProcesses(parser.hosts().size());
        Session.getInstance().setAllProcesses(parser.hosts());
        Session.getInstance().setCurrentProcess(parser.myId() - 1);
        if(parser.hasConfig()){
            try (BufferedReader br = new BufferedReader(new FileReader(parser.config()))) {
                int numOfMessages = Integer.parseInt(br.readLine().trim());
                Session.setNumberOfMessages(numOfMessages);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Config file is not correctly written");
        }
        Session.getInstance().init();
        p = new ProcessModel(Session.getInstance().getCurrentProcess(), parser.output());
    }
}
