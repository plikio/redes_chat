/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import cliente.bean.MensagemBean;
import cliente.bean.MensagemBean.Acao;
import cliente.service.ClienteSocket;

/**
 *
 * @author Plinio
 */
public class ClienteGUI extends javax.swing.JFrame {

    private Socket socket;
    private ClienteSocket cSocket;
    private MensagemBean msgBean;
    private Color azulBt = new Color (99,137,160);
    private Color desabilitado = new Color (240,240,240);
    
    
    /**
     * Creates new form ClienteGUI
     */
    public ClienteGUI() {
        initComponents();
    }
    
    
    private class AnalisaSocket implements Runnable{
        
        private ObjectInputStream entrada;

        public AnalisaSocket(Socket socket) {
            try {
                entrada = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ClienteGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
                
        @Override
        public void run() {
            MensagemBean msgBean = null;
            try {
                while ((msgBean = (MensagemBean) entrada.readObject()) != null){
                    Acao acao = msgBean.getAcao();
                    
                    switch (acao) {
                        case CONECTAR:
                            conectado(msgBean);
                            break;
                        case DESCONECTAR:
                            desconectado();
                            socket.close();
                            break;
                        case ENVIAR_PARA_UM:
                            receber(msgBean);
                            break;
                        case USUARIOS_ONLINE:
                            atualizarUsuariosOnline(msgBean);
                            break;
                        //case MUDAR_STATUS:
                        //    atualizarStatusUsuariosOnline(msgBean);
                        //    break;
                        default:
                            break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ClienteGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClienteGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private void conectado(MensagemBean msgBean){
        if(msgBean.getTexto().equals("impossível conexão")){
            this.tfNome.setText("");
            JOptionPane.showMessageDialog(this, "Impossível se conectar! \nTente novamente com outro nome.");
            return;
        }
        
        this.msgBean = msgBean;
        msgBean.setStatus(MensagemBean.Status.DISPONIVEL);
        btConectar.setEnabled(false);
        tfNome.setEditable(false);
        btSair.setEnabled(true);
        btSair.setBackground(azulBt);
        taEnviada.setEnabled(true);
        taRecebidas.setEnabled(true);
        btEnviar.setEnabled(true);
        btEnviar.setBackground(azulBt);
        btLimpar.setEnabled(true);
        btLimpar.setBackground(azulBt);
        porta.setEditable(false);
        status.setEnabled(true);
        status.getEditor().getEditorComponent().setBackground(Color.YELLOW);
        
        JOptionPane.showMessageDialog(this, "Conexão realizada com sucesso!");
    }
    
    private void desconectado(){
        
        btConectar.setEnabled(true);
        tfNome.setEditable(true);
        btSair.setEnabled(false);
        taEnviada.setEnabled(false);
        taRecebidas.setEnabled(false);
        btEnviar.setEnabled(false);
        btLimpar.setEnabled(false);
        porta.setEditable(true);
        status.setEnabled(false);
        taRecebidas.setText("");
        taEnviada.setText("");
        listaOnlines.setModel(new DefaultListModel());
        
        JOptionPane.showMessageDialog(this, "Você saiu do chat!");
        
    }
    
    private void receber(MensagemBean msgBean){
        taRecebidas.append(msgBean.getNome()+ ": " + msgBean.getTexto() +"\n");
    }
    
    private void atualizarUsuariosOnline(MensagemBean msgBean){
        Set<String> nomes = msgBean.getUsuariosOnline();
        nomes.remove((String) msgBean.getNome());
        String[] arrayNomes = (String[]) nomes.toArray(new String[nomes.size()]);
        
        listaOnlines.setListData(arrayNomes);
        listaOnlines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaOnlines.setLayoutOrientation(JList.VERTICAL);
    }
    
    private void atualizarStatusUsuariosOnline(MensagemBean msgBean){
        listaOnlines.setCellRenderer(new CorStatus());
        atualizarUsuariosOnline(msgBean);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        tfNome = new javax.swing.JTextField();
        btConectar = new javax.swing.JButton();
        btSair = new javax.swing.JButton();
        porta = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        listaOnlines = new javax.swing.JList<>();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taRecebidas = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        btEnviar = new javax.swing.JButton();
        btLimpar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        taEnviada = new javax.swing.JTextArea();
        status = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat Client");
        setBackground(new java.awt.Color(52, 72, 84));

        jPanel1.setBackground(new java.awt.Color(52, 72, 84));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Login", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(69, 163, 218))); // NOI18N

        tfNome.setToolTipText("Nome de Usuário");
        tfNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfNomeActionPerformed(evt);
            }
        });

        btConectar.setBackground(new java.awt.Color(99, 137, 160));
        btConectar.setText("Conectar");
        btConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btConectarActionPerformed(evt);
            }
        });

        btSair.setText("Sair");
        btSair.setEnabled(false);
        btSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSairActionPerformed(evt);
            }
        });

        porta.setToolTipText("Porta");
        porta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tfNome, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(porta, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                .addComponent(btConectar, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btSair, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(tfNome))
                    .addComponent(btConectar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btSair, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(porta, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(52, 72, 84));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Online", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(69, 163, 218))); // NOI18N

        jScrollPane3.setViewportView(listaOnlines);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(52, 72, 84));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chat", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(69, 163, 218))); // NOI18N

        taRecebidas.setEditable(false);
        taRecebidas.setColumns(20);
        taRecebidas.setRows(5);
        taRecebidas.setEnabled(false);
        jScrollPane1.setViewportView(taRecebidas);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(52, 72, 84));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Mensagem", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13), new java.awt.Color(69, 163, 218))); // NOI18N

        btEnviar.setText("Enviar");
        btEnviar.setEnabled(false);
        btEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEnviarActionPerformed(evt);
            }
        });

        btLimpar.setText("Limpar");
        btLimpar.setEnabled(false);
        btLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLimparActionPerformed(evt);
            }
        });

        taEnviada.setColumns(20);
        taEnviada.setRows(5);
        taEnviada.setEnabled(false);
        jScrollPane2.setViewportView(taEnviada);

        status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Disponível", "Ocupado", "Ausente" }));
        status.setEnabled(false);
        status.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                statusItemStateChanged(evt);
            }
        });
        status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btLimpar, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                    .addComponent(btEnviar, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                    .addComponent(status, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(status, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    private void tfNomeActionPerformed(java.awt.event.ActionEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void btConectarActionPerformed(java.awt.event.ActionEvent evt) {                                           
        String nome = tfNome.getText();
        String port = porta.getText();
        if (!nome.isEmpty() && !port.isEmpty()){
            
            try {
            msgBean = new MensagemBean();
            msgBean.setAcao(Acao.CONECTAR);
            msgBean.setNome(nome);
            
            cSocket = new ClienteSocket();
            socket = cSocket.conectar(Integer.parseInt(port));

            new Thread(new AnalisaSocket(socket)).start();

            cSocket.enviar(msgBean);
            }   catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Por favor, digite um valor válido.\nUtilize apenas números.");
                desconectado();
            }    
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.");
        }
    }                                          

    private void btSairActionPerformed(java.awt.event.ActionEvent evt) {                                       
        MensagemBean msgBean = new MensagemBean();
        msgBean.setNome(this.msgBean.getNome());
        msgBean.setAcao(Acao.DESCONECTAR);
        cSocket.enviar(msgBean);
        desconectado();
    }                                      

    private void btLimparActionPerformed(java.awt.event.ActionEvent evt) {                                         
        taEnviada.setText("");
    }                                        

    private void btEnviarActionPerformed(java.awt.event.ActionEvent evt) {                                         
        String texto = taEnviada.getText();
        String nome = msgBean.getNome();
        msgBean = new MensagemBean();
        
        if(listaOnlines.getSelectedIndex() >= 0){
            msgBean.setNomePrivado(listaOnlines.getSelectedValue());
            msgBean.setAcao(Acao.ENVIAR_PARA_UM);
            listaOnlines.clearSelection();
        } else {
            msgBean.setAcao(Acao.ENVIAR_PARA_TODOS);
        }
        
        if(!texto.isEmpty()){
            msgBean.setNome(nome);
            msgBean.setTexto(texto);
            
            taRecebidas.append("Você: " + texto + "\n");
            
            cSocket.enviar(msgBean);
        }
        
        taEnviada.setText("");
    }                                        

    private void portaActionPerformed(java.awt.event.ActionEvent evt) {                                      
        // TODO add your handling code here:
    }                                     

    private void statusActionPerformed(java.awt.event.ActionEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void statusItemStateChanged(java.awt.event.ItemEvent evt) {                                        
        /*
        if(evt.getStateChange() == ItemEvent.SELECTED){
            int selecao = status.getSelectedIndex();
            switch(selecao){
                case 0:
                    msgBean.setStatus(MensagemBean.Status.DISPONIVEL);
                    cSocket.enviar(msgBean);
                    break;
                case 1:
                    msgBean.setStatus(MensagemBean.Status.OCUPADO);
                    cSocket.enviar(msgBean);
                    break;
                case 2:
                    msgBean.setStatus(MensagemBean.Status.AUSENTE);
                    cSocket.enviar(msgBean);
                    break;
            }
        }
        */
    }                                       

    // Variables declaration - do not modify                     
    private javax.swing.JButton btConectar;
    private javax.swing.JButton btEnviar;
    private javax.swing.JButton btLimpar;
    private javax.swing.JButton btSair;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList<String> listaOnlines;
    private javax.swing.JTextField porta;
    private javax.swing.JComboBox<String> status;
    private javax.swing.JTextArea taEnviada;
    private javax.swing.JTextArea taRecebidas;
    private javax.swing.JTextField tfNome;
    // End of variables declaration                   

class CorStatus <String> extends JLabel implements ListCellRenderer {

    public CorStatus() {
        super();
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        
        System.out.println("teste");
        
        switch (msgBean.getStatus()) {
            case DISPONIVEL:
                setForeground(Color.green);
                break;
            case OCUPADO:
                setForeground(Color.yellow);
                break;
            case AUSENTE:
                setForeground(Color.red);
            default:
                break;
        }        
        return this;
    }

}

}

