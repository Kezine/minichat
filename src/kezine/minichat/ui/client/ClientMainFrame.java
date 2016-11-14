package kezine.minichat.ui.client;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import kezine.minichat.data.Message;
import kezine.minichat.data.Topic;
import kezine.minichat.data.User;
import kezine.minichat.events.ChatEvent;
import kezine.minichat.events.ChatEventListener;
import kezine.minichat.events.ThreadEventListener;
import kezine.minichat.work.BaseThread;
import kezine.minichat.work.client.ChatReceiver;
import kezine.minichat.work.client.ChatSender;
import org.apache.log4j.Logger;

/**
 *
 * @author Kezine
 */
public class ClientMainFrame extends javax.swing.JFrame implements ChatEventListener, ThreadEventListener {

    private Socket _Socket;
    
    private ChatReceiver _ChatReceiver;
    private ChatSender _ChatSender;
    private short _ConnexionState;
    boolean _RequestShutdown;
    long nom;
    protected final Logger logger = Logger.getLogger(this.getClass());
    public ClientMainFrame() {
        initComponents();
        _ChatReceiver = null;
        _ChatSender = null;
        jListReceptionArea.setModel(new DefaultListModel<String>());
        _ConnexionState = 0;
        jMenuItemDeconnexion.setEnabled(false);
        jButtonSend.setEnabled(false);
        setTitle("Disconnected");
        _RequestShutdown = false;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPaneChat = new javax.swing.JScrollPane();
        jListReceptionArea = new javax.swing.JList();
        jScrollPaneSendZone = new javax.swing.JScrollPane();
        jTextAreaSend = new javax.swing.JTextArea();
        jButtonSend = new javax.swing.JButton();
        jScrollPaneTopicUsers = new javax.swing.JScrollPane();
        jListTopicUser = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jLabelTopicName = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemConnexion = new javax.swing.JMenuItem();
        jMenuItemDeconnexion = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jScrollPaneChat.setViewportView(jListReceptionArea);

        jTextAreaSend.setColumns(20);
        jTextAreaSend.setRows(5);
        jTextAreaSend.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextAreaSendKeyReleased(evt);
            }
        });
        jScrollPaneSendZone.setViewportView(jTextAreaSend);

        jButtonSend.setText("Envoyer");
        jButtonSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendActionPerformed(evt);
            }
        });

        jScrollPaneTopicUsers.setViewportView(jListTopicUser);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Topic : ");

        jLabelTopicName.setText("topicName");

        jMenu1.setText("File");

        jMenuItemConnexion.setText("Connexion");
        jMenuItemConnexion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemConnexionActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemConnexion);

        jMenuItemDeconnexion.setText("Deconnexion");
        jMenuItemDeconnexion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDeconnexionActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemDeconnexion);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPaneChat, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                            .addComponent(jScrollPaneSendZone))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPaneTopicUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jButtonSend, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTopicName)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabelTopicName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPaneTopicUsers)
                    .addComponent(jScrollPaneChat, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPaneSendZone, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonSend, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemConnexionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConnexionActionPerformed
        disconnexion();
        if(_ConnexionState != 0)
            return;
        ConnexionDialog cd = new ConnexionDialog(this, true);
        cd.setVisible(true);
        if(cd.getChoix())
        {
            try 
            {
                nom = System.currentTimeMillis();
                _Socket = new Socket(cd.getAddress(), cd.getPort());

                OutputStream os = _Socket.getOutputStream();
                InputStream is = _Socket.getInputStream();

                _ChatReceiver = new ChatReceiver("ChatReceiver", is);
                _ChatSender = new ChatSender("ChatSender", os);
                _ChatSender.addThreadEventListener(this);
                _ChatReceiver.addThreadEventListener(this);
                _ChatReceiver.addChatEventListener(this);
                _ChatSender.start();
                _ChatReceiver.start();
                _ChatSender.sendMessage(new Message(Message.MessageType.CLIENT_LOGIN, "Objet", new User(nom+"", Color.red), new Topic()));
                jMenuItemDeconnexion.setEnabled(true);
                jButtonSend.setEnabled(true);
                setTitle("Connected to : " + _Socket.getInetAddress());
                _ConnexionState = 1;
               
            } 
            catch (IOException ex) 
            {
                String message = "Error while connecting : " + ex.getMessage();
                logger.warn(message);
                JOptionPane.showMessageDialog(this, message, "Connexion Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jMenuItemConnexionActionPerformed
    private void send()
    {
        if(_ChatSender != null)
        {
            _ChatSender.sendMessage(new Message(Message.MessageType.CHAT_MESSAGE, jTextAreaSend.getText(), new User(nom+"", Color.red), new Topic()));
            jTextAreaSend.setText("");
        }
    }
    private void jButtonSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendActionPerformed
        send();
    }//GEN-LAST:event_jButtonSendActionPerformed

    private void jTextAreaSendKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextAreaSendKeyReleased
        if(evt.getKeyCode() == KeyEvent.VK_ENTER)
            send();
    }//GEN-LAST:event_jTextAreaSendKeyReleased

    private void jMenuItemDeconnexionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDeconnexionActionPerformed
        disconnexion();
    }//GEN-LAST:event_jMenuItemDeconnexionActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if(_ConnexionState != 0)
        {
            setEnabled(false);
            _RequestShutdown = true;
            disconnexion();
        }
        else
            System.exit(0);
    }//GEN-LAST:event_formWindowClosing
    
    private void disconnexion()
    {
        if(_ConnexionState == 1)
        {
            setTitle("Disconnexion");
            _ChatSender.stopThread();
            _ChatReceiver.stopThread();
            _ConnexionState = 2;
        }        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonSend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelTopicName;
    private javax.swing.JList jListReceptionArea;
    private javax.swing.JList jListTopicUser;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemConnexion;
    private javax.swing.JMenuItem jMenuItemDeconnexion;
    private javax.swing.JScrollPane jScrollPaneChat;
    private javax.swing.JScrollPane jScrollPaneSendZone;
    private javax.swing.JScrollPane jScrollPaneTopicUsers;
    private javax.swing.JTextArea jTextAreaSend;
    // End of variables declaration//GEN-END:variables

    @Override
    public void processChatEvent(ChatEvent event) 
    {
        final ChatEvent levent = event;
        SwingUtilities.invokeLater(new Runnable() 
        {

            @Override
            public void run() {
                if(levent.getType() == ChatEvent.ChatEventType.MESSAGE)
                {
                    final Message message = (Message)levent.getComplement();
                    if(message.getType() == Message.MessageType.SERVER_BROADCAST)
                    {
                        DefaultListModel<String> model = ((DefaultListModel<String>)jListReceptionArea.getModel());
                        model.addElement("[Server]"+message.getMessage());
                    }
                    else if(message.getType() == Message.MessageType.CHAT_MESSAGE)
                    {
                        DefaultListModel<String> model = ((DefaultListModel<String>)jListReceptionArea.getModel());
                        model.addElement("["+message.getSender().getUsername()+"]"+message.getMessage());
                            
                    }
                    else if(message.getType() == Message.MessageType.PRIVATE_MESSAGE)
                    {
                        DefaultListModel<String> model = ((DefaultListModel<String>)jListReceptionArea.getModel());
                        model.addElement("["+message.getSender().getUsername()+"](Private)"+message.getMessage());
                    }
                    else if(message.getType() == Message.MessageType.SERVER_INFO)
                    {
                        System.out.println("ServerInfo");
                    }
                    else if(message.getType() == Message.MessageType.CLIENT_LOGIN)
                    {
                        System.out.println("Server responded to login : " + message.getMessage());
                    }
                }
            }
        });
        
    }

    @Override
    public void ThreadStatusChanged(Object source, BaseThread.ThreadStatus status) 
    {
        if(status == BaseThread.ThreadStatus.STOPPED || status == BaseThread.ThreadStatus.STOPPED_WITH_ERROR)
                ++_ConnexionState;
        if(_ConnexionState == 4)
        {
            logger.info("ConnexionState : " + _ConnexionState);
            SwingUtilities.invokeLater(new Runnable() 
            {
                @Override
                public void run() {
                    _ChatReceiver = null;
                    _ChatSender = null;
                    jMenuItemDeconnexion.setEnabled(false);
                    jButtonSend.setEnabled(false);
                    _ConnexionState = 0;
                    setTitle("Disconnected");
                    if(_RequestShutdown)
                        System.exit(0);
                }
            });
            
        }
        
    }
}
