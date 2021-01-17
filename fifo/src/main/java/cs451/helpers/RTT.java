package cs451.helpers;

import cs451.session.Session;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Calculates Road Trip Time in similar way as TCP protocol
 *
 * @author not-sure
 */
public class RTT {

    private static RTT instance = new RTT();
    private double[] estimatedRTT;
    private double[] sampleRTT;
    private double[] devRTT;
    private double alpha = 0.125;
    private double beta = 0.25;

    private RTT() {
        int numOfProcesses = Session.getInstance().getNumberOfProcesses();
        estimatedRTT = new double[numOfProcesses];
        sampleRTT = new double[numOfProcesses];
        devRTT = new double[numOfProcesses];
    }

    public static RTT getInstance() {
        return instance;
    }

    /**
     * Count estimated time needed for receiving ack for message send to proces
     * with identificator id
     *
     * @param id
     * @return
     */
    public synchronized double getTimeoutInterval(int id) {
        if (sampleRTT[id] == 0) {
            return 500;
        }
        return estimatedRTT[id] + Math.max(100, 4 * devRTT[id]);
    }

    /**
     * updates estimatedRTT, sampleRTT, devRTT(deviation RTT)
     *
     * @param sample
     * @param id
     */
    public synchronized void updateTimeoutInterval(double sample, int id) {
        if (sampleRTT[id] == 0) {
            sampleRTT[id] = sample;
            estimatedRTT[id] = sample;
            devRTT[id] = sample/2;
        }else {
            sampleRTT[id] = sample;
            estimatedRTT[id] = (1 - alpha) * estimatedRTT[id] + alpha * sampleRTT[id];
            devRTT[id] = (1 - beta) * devRTT[id] + beta * Math.abs(sampleRTT[id] - estimatedRTT[id]);
        }
    }
}
