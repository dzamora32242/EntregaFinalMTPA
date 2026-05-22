
package com.mycompany.servidordifusion;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.mycompany.common.Mensaje;
import com.mycompany.common.SendChannelNotification;


public class ServidorDifusion 
        implements Runnable
{
    private ServerSocket servidor;
    private static ArrayList<ClienteDifusion> listaUsuarios = new ArrayList<>();
    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private ArrayList<Salon> salones = new ArrayList<>();
    private Thread t;


    public static ArrayList<ClienteDifusion> getListaUsuarios() {
        return listaUsuarios;
    }

    public ServidorDifusion() throws Exception
    {
        servidor = new ServerSocket(5665);
        t = new Thread(this);
        t.start();
    }
    
    public void run(){
        try {
            startListeningUsers();   
        } catch (Exception e) {
        }
    }
    
    public void startListeningUsers() throws Exception
    {
        while (true)
        {
            System.out.println("Esperando clientes....");
            Socket sck = servidor.accept(); 
            System.out.println("Un cliente conectado...");
            ClienteDifusion unCliente = new ClienteDifusion(sck, usuarios, salones);
            listaUsuarios.add(unCliente);
        }
    }
    
    public static void difusionMensaje(Mensaje mensaje)
    {
        for (ClienteDifusion unCliente : listaUsuarios) {
            try {
                unCliente.sendMessage(mensaje);
            } catch (Exception e) {
                System.out.println("Err. Difusion: " + e.toString());
            }
            
        }
    }
}
