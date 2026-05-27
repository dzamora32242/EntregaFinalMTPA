package com.mycompany.cliente;

import javax.swing.SwingUtilities;

public class ClienteGrafico {
    
    public static void main(String[] args) {
        // Ejecutamos la interfaz gráfica en el hilo especial de Swing para evitar cuelgues
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Iniciamos el motor de red apuntando al servidor
                ClienteCore core = new ClienteCore("localhost", 5665);
                
                // 2. Creamos la primera ventana (Login) pasándole el motor
                PantallaLogin login = new PantallaLogin(core);
                
                // 3. La hacemos visible en pantalla
                login.setVisible(true);
                
            } catch (Exception e) {
                System.out.println("Error al arrancar el cliente gráfico: No se pudo conectar al servidor.");
                e.printStackTrace();
            }
        });
    }
}
