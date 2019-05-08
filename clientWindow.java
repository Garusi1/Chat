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
import java.net.SocketException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 * create a new clientWindow
 *
 * @author Naor, Michael, Elioshiv
 */
public class clientWindow extends javax.swing.JFrame {

    Socket connection;
    ObjectInputStream input;
    ObjectOutputStream output;
    boolean isAlive;
    String name;

    /**
     * constructor - Creates new form clientWindow
     */
    public clientWindow() {
        initComponents();
    }

    /**
     * This class extends Thread to look for new messages from the server
     */
    public class checkMessages extends Thread {

        /**
         * look for new messages from the server
         */
        @Override
        public void run() {
            while (isAlive) {

                try {
                    String msg = (String) input.readObject();
                    if (msg.length() > 2) {
                        if (msg.substring(0, 3).equals("<o>")) {
                            onlineClients.setText(msg.substring(3));
                        } else {
                            chatText.append(msg + "\n");
                        }
                    } else {
                        chatText.append(msg + "\n");
                    }

                } 
                catch(SocketException e){
                
                }
                catch (IOException ex) {
                    Logger.getLogger(clientWindow.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(clientWindow.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        chatText = new javax.swing.JTextArea();
        userName = new javax.swing.JTextField();
        connect = new javax.swing.JButton();
        userText = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        onlineClients = new javax.swing.JTextArea();
        sendTo = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        send = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        refresh = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        ip = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("client");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
       
        
        chatText.setColumns(20);
        chatText.setRows(5);
        jScrollPane1.setViewportView(chatText);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 41, 400, 230));
        getContentPane().add(userName, new org.netbeans.lib.awtextra.AbsoluteConstraints(97, 13, 83, -1));

        connect.setText("connect");
        connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectActionPerformed(evt);
            }
        });
        getContentPane().add(connect, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, 110, -1));

        userText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userTextActionPerformed(evt);
            }
        });
        getContentPane().add(userText, new org.netbeans.lib.awtextra.AbsoluteConstraints(103, 298, 340, 34));

        onlineClients.setEditable(false);
        onlineClients.setColumns(20);
        onlineClients.setRows(5);
        jScrollPane2.setViewportView(onlineClients);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(427, 42, 159, 230));
        getContentPane().add(sendTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 298, 83, 34));

        jLabel1.setText("Send To:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 273, 75, 19));

        send.setText("SEND");
        send.setEnabled(false);
        send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendActionPerformed(evt);
            }
        });
        getContentPane().add(send, new org.netbeans.lib.awtextra.AbsoluteConstraints(464, 298, 122, 34));

        jLabel3.setText("UserName:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 11, 75, 24));

        refresh.setText("refresh online list");
        refresh.setEnabled(false);
        refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshActionPerformed(evt);
            }
        });
        getContentPane().add(refresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(453, 12, -1, -1));

        jLabel2.setText("ip:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(198, 16, -1, -1));

        ip.setText("127.0.0.1");
        getContentPane().add(ip, new org.netbeans.lib.awtextra.AbsoluteConstraints(214, 13, 80, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents
    //when pressing the connect/disconnect button
    private void connectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectActionPerformed
        if (connect.getText().equals("connect")) {
            try {

                connection = new Socket(ip.getText(), 7777);
                output = new ObjectOutputStream(connection.getOutputStream());
                output.flush();
                input = new ObjectInputStream(connection.getInputStream());
                chatText.append("connected\n");
                sendMessage(userName.getText());

            } catch (IOException ex) {
                chatText.append("connection failed\n");
                return;
            }
            this.setTitle(userName.getText());
            isAlive = true;
            checkMessages check = new checkMessages();
            check.start();
            refresh.setEnabled(isAlive);
            send.setEnabled(isAlive);
            connect.setText("disconnect");
            userName.setEditable(!isAlive);
            name = userName.getText();
        } else {
            isAlive = false;
            connect.setText("connect");
            userName.setEditable(!isAlive);
//            sendMessage("bye");
            exit();
        }
    }//GEN-LAST:event_connectActionPerformed
    //when pressing enter at the user text field
    private void userTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userTextActionPerformed
        sendActionPerformed(evt);
    }//GEN-LAST:event_userTextActionPerformed
    //when pressing the send button
    private void sendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendActionPerformed
        if (sendTo.getText().equals("")) {
            sendMessage(userText.getText() + "");
        } else {
            sendMessage("<p" + sendTo.getText() + ">" + userText.getText() + "");
        }
        userText.setText("");
    }//GEN-LAST:event_sendActionPerformed
    //when pressing the refresh online list button
    private void refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshActionPerformed
        // TODO add your handling code here:
        sendMessage("get_online");
    }//GEN-LAST:event_refreshActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
                exit();
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
            java.util.logging.Logger.getLogger(clientWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(clientWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(clientWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(clientWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new clientWindow().setVisible(true);
            }
        });

    }

    /**
     * send messages to thr server
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        try {
            output.writeObject(message);
            output.flush();
        }
        catch(SocketException e){
            
        }
        catch (IOException ex) {
            Logger.getLogger(clientWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea chatText;
    private javax.swing.JButton connect;
    private javax.swing.JTextField ip;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea onlineClients;
    private javax.swing.JButton refresh;
    private javax.swing.JButton send;
    private javax.swing.JTextField sendTo;
    private javax.swing.JTextField userName;
    private javax.swing.JTextField userText;
    // End of variables declaration//GEN-END:variables
    
    /**
     * end the connection
     */
    public void exit() {
        try {

            if (connection != null) {
                sendMessage("bye");
                connection.close();
            }
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
            isAlive = false;
        }       
        catch(SocketException e){
        }
        catch (IOException ex) {
            Logger.getLogger(clientWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
