package com.mycompany.cliente;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;



public class Cliente 
        extends Thread
{
    private Socket sck;
    public static void main (String args[]) throws Exception
    {
        Cliente c = new Cliente();
        c.sck = new Socket("127.0.0.1", 5665);
        c.start();
        
        BufferedReader br = 
                new BufferedReader(new InputStreamReader(System.in));
        while (true)
        {
            System.out.print("Mensaje: ");
            String elMensaje = br.readLine();
            c.sck.getOutputStream().write(elMensaje.getBytes());
            System.out.println("\t\t\tEnviando mensaje...");
        }
        
    }
    
    @Override
    public void run() 
    {
        try{
            InputStream is = sck.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = null;
            int nb;
            while (true)
            {
                baos = new ByteArrayOutputStream();
                do{
                    nb = is.read(buffer);
                    baos.write(buffer, 0 , nb);
                }while (is.available()>0);
                System.out.println("\tRecibido: > " + new String(baos.toByteArray()));
            }
            
        }catch (Exception ex){
            
        }
        
    }
    
}
