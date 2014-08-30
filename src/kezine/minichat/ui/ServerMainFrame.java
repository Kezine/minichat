package kezine.minichat.ui;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import kezine.minichat.data.ServerInfos;
import kezine.minichat.data.Topic;
import kezine.minichat.data.User;
import kezine.minichat.events.ServerEventListener;
import kezine.minichat.tools.LoggerManager;
import kezine.minichat.work.BaseThread;
import kezine.minichat.work.server.ServerMonitor;


/**
 * Classe graphique principale pour l'application serveur
 * @author Kezine
 */
public class ServerMainFrame extends javax.swing.JFrame implements ServerEventListener {

    private ServerMonitor _ServerMonitor;
    private final ArrayList<Topic> [] _Topics;
    private final HashMap<User,String> [] _Users;
    private boolean shutdownRequest;
    public ServerMainFrame() {
        initComponents();
        _ServerMonitor = new ServerMonitor();
        LoggerManager.setLogTextPane(jTextPaneConsole);
        _ServerMonitor.addServerEventListener(this);
        jListTopics.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jListTopics.setModel(new DefaultListModel<Topic>());
        jListUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jListUsers.setModel(new DefaultListModel<User>());
        _Users = new HashMap[1];
        _Topics = new ArrayList[1];
        shutdownRequest = false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonServerState = new javax.swing.JButton();
        jLabelServerStatus = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButtonAddTopic = new javax.swing.JButton();
        jScrollPaneListTopic = new javax.swing.JScrollPane();
        jListTopics = new javax.swing.JList();
        jScrollPaneListUsers = new javax.swing.JScrollPane();
        jListUsers = new javax.swing.JList();
        jButtonSendMessage = new javax.swing.JButton();
        jButtonKick = new javax.swing.JButton();
        jScrollPaneConsole = new javax.swing.JScrollPane();
        jTextPaneConsole = new javax.swing.JTextPane();
        jButtonLock = new javax.swing.JButton();
        jButtonShowAll = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(500, 570));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jButtonServerState.setText("Start Server");
        jButtonServerState.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonServerStateActionPerformed(evt);
            }
        });

        jLabelServerStatus.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelServerStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelServerStatus.setText("Stopped");

        jLabel1.setText("Topic Management");

        jLabel2.setText("Topic's Users");

        jButtonAddTopic.setText("Add Topic");
        jButtonAddTopic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddTopicActionPerformed(evt);
            }
        });

        jListTopics.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListTopics.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListTopicsMouseClicked(evt);
            }
        });
        jScrollPaneListTopic.setViewportView(jListTopics);

        jListUsers.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListUsersMouseClicked(evt);
            }
        });
        jScrollPaneListUsers.setViewportView(jListUsers);

        jButtonSendMessage.setText("Send Message");
        jButtonSendMessage.setEnabled(false);
        jButtonSendMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendMessageActionPerformed(evt);
            }
        });

        jButtonKick.setText("Kick");
        jButtonKick.setEnabled(false);
        jButtonKick.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonKickActionPerformed(evt);
            }
        });

        jTextPaneConsole.setEditable(false);
        jScrollPaneConsole.setViewportView(jTextPaneConsole);

        jButtonLock.setText("Lock");
        jButtonLock.setEnabled(false);
        jButtonLock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLockActionPerformed(evt);
            }
        });

        jButtonShowAll.setText("Show All");
        jButtonShowAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonShowAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(48, 48, 48)
                                .addComponent(jButtonAddTopic))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel1)
                                            .addComponent(jScrollPaneListTopic, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jScrollPaneListUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel2)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButtonServerState, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(39, 39, 39)
                                        .addComponent(jLabelServerStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButtonLock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButtonSendMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButtonKick, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButtonShowAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(0, 9, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPaneConsole)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonServerState)
                    .addComponent(jLabelServerStatus)
                    .addComponent(jButtonLock))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonSendMessage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonKick)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonShowAll)
                        .addGap(0, 38, Short.MAX_VALUE))
                    .addComponent(jScrollPaneListUsers, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPaneListTopic, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAddTopic)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneConsole, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSendMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendMessageActionPerformed
        User user = (User)jListTopics.getSelectedValue();
        if(user != null)
        {
            
        }
    }//GEN-LAST:event_jButtonSendMessageActionPerformed

    private void jButtonKickActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonKickActionPerformed
        User user = (User)jListTopics.getSelectedValue();
        if(user != null)
        {
            
        }
    }//GEN-LAST:event_jButtonKickActionPerformed

    private void jButtonServerStateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonServerStateActionPerformed
        try 
        {
            if(jButtonServerState.getText().equals("Start Server"))
            {
                _ServerMonitor.startServer();
                jButtonServerState.setEnabled(false);
            }
            else
            {
                _Users[0] = null;
                initListFromData();
                toggleSeverDependentControl(false);
                jButtonServerState.setEnabled(false);
                _ServerMonitor.stopServer("None");                
            }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(ServerMainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonServerStateActionPerformed

    private void jButtonAddTopicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddTopicActionPerformed
        
    }//GEN-LAST:event_jButtonAddTopicActionPerformed

    private void jListTopicsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListTopicsMouseClicked
        Topic value = (Topic)jListTopics.getSelectedValue();
        if(SwingUtilities.isLeftMouseButton(evt))
        {
            if(value != null)
            {
                for(User user :  _Users[0].keySet())
                {
                     DefaultListModel<User> model = (DefaultListModel<User>)jListUsers.getModel();
                     model.removeAllElements();
                     if(_Users[0].get(user).equals(value))
                         model.addElement(user);
                }
            }
        }
        else if(SwingUtilities.isRightMouseButton(evt))
        {
            
        }
    }//GEN-LAST:event_jListTopicsMouseClicked

    private void jListUsersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListUsersMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jListUsersMouseClicked

    private void jButtonShowAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonShowAllActionPerformed
        initListFromData();
    }//GEN-LAST:event_jButtonShowAllActionPerformed

    private void jButtonLockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLockActionPerformed
        if(jButtonLock.getText().equals("Lock"))
        {
            _ServerMonitor.setServerLocked(true);
            jButtonLock.setText("Unlock");
        }
        else
        {
            _ServerMonitor.setServerLocked(false);
            jButtonLock.setText("Lock");
        }
    }//GEN-LAST:event_jButtonLockActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        shutdownRequest = true;
        if(!jButtonServerState.getText().equals("Start Server"))
        {
            _ServerMonitor.stopServer("Window closing");
            Closing dialog = new Closing(this, true);
            dialog.setAlwaysOnTop(true);
            _ServerMonitor.addServerEventListener(dialog);
            dialog.setVisible(true);
        }
        else
        {
            System.exit(0);
        }
        
    }//GEN-LAST:event_formWindowClosing
    public void initListFromData()
    {
        ((DefaultListModel<User>)jListUsers.getModel()).clear();
        ((DefaultListModel<Topic>)jListTopics.getModel()).clear();
        if(_Users[0] == null)
            return;
        HashSet<Topic> topics = new HashSet<>();
        for(User user :  _Users[0].keySet())
            ((DefaultListModel<User>)jListUsers.getModel()).addElement(user);
        for(Topic topic : _Topics[0])
            ((DefaultListModel<Topic>)jListTopics.getModel()).addElement(topic);
    }
    
    private void toggleSeverDependentControl(boolean isEnabled)
    {
        jButtonKick.setEnabled(isEnabled);
        jButtonSendMessage.setEnabled(isEnabled);
        jButtonLock.setEnabled(isEnabled);
        jButtonShowAll.setEnabled(isEnabled);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddTopic;
    private javax.swing.JButton jButtonKick;
    private javax.swing.JButton jButtonLock;
    private javax.swing.JButton jButtonSendMessage;
    private javax.swing.JButton jButtonServerState;
    private javax.swing.JButton jButtonShowAll;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelServerStatus;
    private javax.swing.JList jListTopics;
    private javax.swing.JList jListUsers;
    private javax.swing.JScrollPane jScrollPaneConsole;
    private javax.swing.JScrollPane jScrollPaneListTopic;
    private javax.swing.JScrollPane jScrollPaneListUsers;
    private javax.swing.JTextPane jTextPaneConsole;
    // End of variables declaration//GEN-END:variables
    
    @Override
    public void ServerDataChanged() 
    {
        final ServerInfos si = _ServerMonitor.getServerInfos();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() 
            { 
                _Users[0] = si._Users;
                _Topics[0] = si._Topics;
                initListFromData();
            }
        });
    }
    
    @Override
    public void ServerStateChanged(BaseThread.ThreadStatus status) 
    {
        final BaseThread.ThreadStatus ts = status;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() 
            {
                jLabelServerStatus.setText(ts.toString());
                if(ts == BaseThread.ThreadStatus.RUNNING)
                {
                    toggleSeverDependentControl(true);
                    jButtonServerState.setEnabled(true);
        
                    jButtonServerState.setText("Stop Server");
                    ServerInfos si = _ServerMonitor.getServerInfos();
                    _Users[0] = si._Users;
                    _Topics[0] = si._Topics;
                    
                    initListFromData();
                    
                }
                else if(ts == BaseThread.ThreadStatus.STOPPED_WITH_ERROR || ts == BaseThread.ThreadStatus.STOPPED)
                {
                    if(!shutdownRequest)
                    {
                        jButtonServerState.setEnabled(true);
                        jButtonServerState.setText("Start Server");
                    }
                }
            }
        });
    }
}
