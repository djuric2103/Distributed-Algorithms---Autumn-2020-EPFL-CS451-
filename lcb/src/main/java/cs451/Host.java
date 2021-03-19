package cs451;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class Host {

    private static final String IP_START_REGEX = "/";

    private int id;
    private String ip;
    private InetAddress IP;
    private int port = -1;
    //in array affected are all processes which affects this one
    private List<Integer> affected = new ArrayList<>();

    public boolean populate(String idString, String ipString, String portString) {
        try {
            id = Integer.parseInt(idString) - 1;

            String ipTest = InetAddress.getByName(ipString).toString();
            if (ipTest.startsWith(IP_START_REGEX)) {
                ip = ipTest.substring(1);
            } else {
                ip = InetAddress.getByName(ipTest.split(IP_START_REGEX)[0]).getHostAddress();
            }
            IP = InetAddress.getByName(ip);
            port = Integer.parseInt(portString);
            if (port <= 0) {
                System.err.println("Port in the hosts file must be a positive number!");
                return false;
            }
        } catch (NumberFormatException e) {
            if (port == -1) {
                System.err.println("Id in the hosts file must be a number!");
            } else {
                System.err.println("Port in the hosts file must be a number!");
            }
            return false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return true;
    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getIP() {
        return IP;
    }

    public List<Integer> getAffected() { return affected; }

    public void setAffected(List<Integer> affected) {
        this.affected = affected;
        //Collections.sort(this.affected);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Host)) return false;
        Host host = (Host) o;
        return id == host.id &&
                port == host.port &&
                Objects.equals(ip, host.ip);
    }
}
