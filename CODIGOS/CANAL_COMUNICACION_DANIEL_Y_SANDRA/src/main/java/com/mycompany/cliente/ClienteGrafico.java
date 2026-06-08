package com.mycompany.cliente;

import javax.swing.SwingUtilities;

public class ClienteGrafico {
    
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                ClienteCore core = new ClienteCore("localhost", 5665);

                PantallaLogin login = new PantallaLogin(core);
                
                login.setVisible(true);
                
            } catch (Exception e) {
                System.out.println("Error al arrancar el cliente gráfico: No se pudo conectar al servidor.");
                e.printStackTrace();
            }
        });
    }
}
