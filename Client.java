/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  this class holds a chat client
 * @author Naor, Michael, Elioshiv
 */
public class Client {
    
    public String clientName;
    public Socket connection;
    public ObjectInputStream input;
    public ObjectOutputStream output;
    
    /**
     * constructor
     * @param s Socket
     */
    public Client(Socket s) {
        try {
            this.connection = s;
            input = new ObjectInputStream(connection.getInputStream());
            output = new ObjectOutputStream(connection.getOutputStream());
            this.clientName = (String) input.readObject();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * close the client connection
     */
    public void close() {
        try {
            connection.close();
            input.close();
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
