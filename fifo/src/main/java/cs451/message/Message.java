package cs451.message;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author not-sure
 */
public class Message implements Serializable {
    private int originalSender;
    private int sender;
    private int messageID;
    private boolean ack = false;
    private int[] clock;

    public Message() {
    }

    public Message(int originalSender, int sender, int messageID) {
        this.originalSender = originalSender;
        this.sender = sender;
        this.messageID = messageID;
    }

    /**
     * @return the originalSender
     */
    public int getOriginalSender() {
        return originalSender;
    }

    /**
     * @param originalSender the originalSender to set
     */
    public void setOriginalSender(int originalSender) {
        this.originalSender = originalSender;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    /**
     * @return the messageID
     */
    public int getMessageID() {
        return messageID;
    }

    /**
     * @param messageID the messageID to set
     */
    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Message other = (Message) obj;
        if (this.originalSender != other.originalSender) {
            return false;
        }
        if (this.sender != other.sender) {
            return false;
        }
        if (this.messageID != other.messageID) {
            return false;
        }
        if (this.ack != other.ack) {
            return false;
        }
        return true;
    }

    /**
     * convert Message in byte array and returns it
     *
     * @return byte[]
     */
    public byte[] getBytes() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutput oo;
        try {
            oo = new ObjectOutputStream(byteStream);
            oo.writeObject(this);
            oo.close();
        } catch (IOException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }

        return byteStream.toByteArray();
    }

    /**
     * reconstruct and return Message from byte array
     *
     * @param byteMessage
     * @return Message
     */
    public static Message getMessage(byte[] byteMessage) {
        Message m = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteMessage));
            m = (Message) in.readObject();
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }

    @Override
    public String toString() {
        return "Message{" + "originalSender=" + originalSender + ", sender=" + sender + ", messageID=" + messageID + ", ack=" + ack + ", clock=" + Arrays.toString(clock) + '}';
    }

    public int[] getClock() {
        return clock;
    }
    public void setClock(int[] clock) {
        this.clock = clock;
    }
}
