package cs451.broadcast;

import cs451.message.Message;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author not-sure
 */
public interface BroadcastI {

    void broadcast(Message message);

    void deliver(Message m);
}
