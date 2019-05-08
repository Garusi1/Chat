/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat2;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * create a new server window
 *
 * @author Naor, Michael, Elioshiv
 */
public class chat_server_gui extends javax.swing.JFrame {

    boolean serverOn;
    Client c;
    public ServerSocket server;
    public ArrayList<Client> clients;
    lookForConnections Look;

    /**
     * constructor - Creates new form chat_server_gui
     */
    public chat_server_gui() {
        initComponents();
        serverOn = true;
        clients = new ArrayList<Client>();
        try {
            server = new ServerSocket(7777);
        } catch (IOException ex) {
            Logger.getLogger(chat_server_gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        Look = new lookForConnections();
        Look.start();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        serverText = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Server ");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        serverText.setEditable(false);
        serverText.setColumns(20);
        serverText.setRows(5);
        jScrollPane1.setViewportView(serverText);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
//        updateAllClients("<disconnect>");
        serverOn = false;
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(chat_server_gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(chat_server_gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(chat_server_gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(chat_server_gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new chat_server_gui().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea serverText;
    // End of variables declaration//GEN-END:variables

    /**
     * This class extends Thread to look for new messages all the time
     */
    public class lookForMessage extends Thread {

        Client c;

        /**
         * constructor
         *
         * @param c the client to look the message from
         */
        public lookForMessage(Client c) {
            this.c = c;
        }

        /**
         * look for message from this client
         */
        @Override
        public void run() {
            try {

                String msg;
                while ((msg = (String) c.input.readObject()) != null) {
                    if (msg.equals("get_online")) {
                        c.output.writeObject("<o>" + getOnline());
                    } else if (msg.equals("bye")) {
                        break;
                    } else if (msg.length() > 1) {
                        if (msg.substring(0, 2).equals("<p")) {
                            String n = msg.substring(2, msg.indexOf(">"));
                            String message = msg.substring(msg.indexOf(">") + 1);
                            updateClient(n, "private from " + c.clientName + ": " + message);
                        } else {
                            updateAllClients(c.clientName + ": " + msg);
                        }
                    } else {
                        updateAllClients(c.clientName + ": " + msg);
                    }
                }
                updateServer("" + c.clientName + " disconnected");
                if (clients != null) {
                    updateAllClients(c.clientName + " left");
                    clients.remove(c);
                }
                c.close();

            }
            catch (IOException ex) {
                Logger.getLogger(chat_server_gui.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(chat_server_gui.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * This class extends Thread to look for new connections while the server is
     * on
     */
    public class lookForConnections extends Thread {

        /**
         * look for new connections
         */
        @Override
        public void run() {
            updateServer("waiting for connections");
            while (serverOn) {

                try {
                    c = new Client(server.accept());
                    lookForMessage l = new lookForMessage(c);
                    l.start();
                    clients.add(c);//add the client to the array
                    updateServer(c.clientName + " is online");
                    updateAllClients(c.clientName + " is online");
                } catch (IOException ex) {
                    Logger.getLogger(chat_server_gui.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }

    }

    /**
     * send messages to all clients
     *
     * @param mes the message to send
     */
    public void updateAllClients(String mes) {
        for (Client c : clients) {
            try {
                c.output.writeObject(mes );
            } catch (IOException ex) {
                Logger.getLogger(chat_server_gui.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    /**
     * send messages to specific client
     *
     * @param name the client to send a message to
     * @param mes the message
     */
    public void updateClient(String name, String mes) {
        for (Client c : clients) {
            try {
                if (c.clientName.equals(name)) {
                    c.output.writeObject(mes + "\n");
                }
            } catch (IOException ex) {
                Logger.getLogger(chat_server_gui.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     *
     * @return a String with the names of online users
     */
    public String getOnline() {
        String online = "";
        int j = 0;
        for (Client client : clients) {
            if (j > 0) {
                online += "\n";
            }
            online += client.clientName;
            j++;
        }
        return online;
    }

    /**
     * update the server window
     *
     * @param mes the message
     */
    public void updateServer(String mes) {
        serverText.append(mes + "\n");
    }
}
