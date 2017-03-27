/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import servidor.bean.MensagemBean;
import servidor.bean.MensagemBean.Acao;

/**
 *
 * @author Plinio
 */
public class ServidorSocket {
    private ServerSocket sSocket;
    private Socket socket;
    private Map<String, ObjectOutputStream> usuariosOnline = new HashMap<String, ObjectOutputStream>();

    public ServidorSocket(int porta) {
        try {
            sSocket = new ServerSocket(porta);
            
            System.out.println("Esperando por clientes...");
            
            while (true){
                socket = sSocket.accept();
   
                new Thread (new AnalisaSocket(socket)).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServidorSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private class AnalisaSocket  implements Runnable{

        private ObjectInputStream entrada;
        private ObjectOutputStream saida;

        public AnalisaSocket(Socket socket) {
            try {
                entrada = new ObjectInputStream(socket.getInputStream());
                saida = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
               
        @Override
        public void run() {
            MensagemBean msgBean = null;
            try {
                while ((msgBean = (MensagemBean) entrada.readObject()) != null) {
                    Acao acao = msgBean.getAcao();
                    
                    switch (acao) {
                        case CONECTAR:
                            boolean estaConectado = conectar(msgBean, saida);
                            if (estaConectado){
                                usuariosOnline.put(msgBean.getNome(), saida);
                                listarOnlines();
                            }
                            break;
                        case DESCONECTAR:
                            desconectar(msgBean, saida);
                            listarOnlines();
                            return;
                        case ENVIAR_PARA_UM:
                            enviarParaUm(msgBean);
                            break;
                        case ENVIAR_PARA_TODOS:
                            enviarParaTodos(msgBean);
                            break; 
                        //case MUDAR_STATUS:
                        //    listarOnlines();
                        //   break;
                        default:
                            break;
                    }
                }
            } catch (IOException ex) {
                MensagemBean mB = new MensagemBean();
                mB.setNome(msgBean.getNome());
                desconectar(mB, saida);
                listarOnlines();
                System.out.println(msgBean.getNome() + " saiu do chat.");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServidorSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
    public void fecharSocket(){
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServidorSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean conectar(MensagemBean msgBean, ObjectOutputStream saida){
        if (usuariosOnline.isEmpty()){
            msgBean.setTexto("conectado");
            enviar(msgBean, saida);
            return true;
        }
        
        if(usuariosOnline.containsKey(msgBean.getNome())){
            msgBean.setTexto("impossível conexão");
            enviar(msgBean, saida);
            return false;
        } else {
            msgBean.setTexto("conectado");
            enviar(msgBean, saida);
            return true;
        }
        
        /*
        for(Map.Entry<String, ObjectOutputStream> usuarioGuardado : usuariosOnline.entrySet()){
            if(usuarioGuardado.getKey().equals(msgBean.getNome())){
                msgBean.setTexto("impossível conexão");
                enviar(msgBean, saida);
                return false;
            } else {
                msgBean.setTexto("conectado");
                enviar(msgBean, saida);
                //msgBean.setTexto(" conectou.");
                //msgBean.setAcao(Acao.ENVIAR_PARA_UM);
                //enviarParaTodos(msgBean); 
                return true;
            }//teste
        }
        
        return false;
        */
    }
    
    private void desconectar(MensagemBean msgBean, ObjectOutputStream saida){
        usuariosOnline.remove(msgBean.getNome());
        msgBean.setTexto(" saiu.");
        msgBean.setAcao(Acao.ENVIAR_PARA_UM);
        enviarParaTodos(msgBean);        
        System.out.println("Usuário " + msgBean.getNome() + " saiu.");
    }
    
    public void desconectarTodos(MensagemBean msgBean){
        msgBean.setTexto("Servidor desconectado.");
        msgBean.setAcao(Acao.ENVIAR_PARA_UM);
        enviarParaTodos(msgBean);        
        
        for (Iterator<Map.Entry<String, ObjectOutputStream>> it = usuariosOnline.entrySet().iterator(); it.hasNext();){
            Map.Entry<String, ObjectOutputStream> entry = it.next();
            it.remove();
        }
    }
    
    private void enviar(MensagemBean msgBean, ObjectOutputStream saida){
        try {
            saida.writeObject(msgBean);
        } catch (IOException ex) {
            Logger.getLogger(ServidorSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void enviarParaUm(MensagemBean msgBean){
        for(Map.Entry<String, ObjectOutputStream> usuarioGuardado : usuariosOnline.entrySet()){
            if(usuarioGuardado.getKey().equals(msgBean.getNomePrivado())){
                 try {
                usuarioGuardado.getValue().writeObject(msgBean);
            } catch (IOException ex) {
                Logger.getLogger(ServidorSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        }
    }
    
    private void enviarParaTodos(MensagemBean msgBean){
        for(Map.Entry<String, ObjectOutputStream> usuarioGuardado : usuariosOnline.entrySet()){
            if(!usuarioGuardado.getKey().equals(msgBean.getNome())){
                msgBean.setAcao(Acao.ENVIAR_PARA_UM);
                try {
                    usuarioGuardado.getValue().writeObject(msgBean);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorSocket.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
        
    private void listarOnlines(){
        Set<String> nomes = new HashSet<String>();
        for(Map.Entry<String, ObjectOutputStream> usuarioGuardado : usuariosOnline.entrySet()){
            nomes.add(usuarioGuardado.getKey());
        }
        
        MensagemBean msgBean = new MensagemBean();
        msgBean.setAcao(Acao.USUARIOS_ONLINE);
        msgBean.setUsuariosOnline(nomes);
        for(Map.Entry<String, ObjectOutputStream> usuarioGuardado : usuariosOnline.entrySet()){
            msgBean.setNome(usuarioGuardado.getKey());
            try {
                usuarioGuardado.getValue().writeObject(msgBean);
            } catch (IOException ex) {
                Logger.getLogger(ServidorSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
      
}
