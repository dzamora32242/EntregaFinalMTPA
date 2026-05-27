package com.mycompany.cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PantallaChatPrivado extends JFrame implements ActionListener {

    private ClienteCore core;
    private String miUsuario;
    private String usuarioDestino;

    private JTextArea areaPrivada;
    private JTextField txtEntrada;
    private JButton btnEnviar;

    public PantallaChatPrivado(ClienteCore core, String miUsuario, String usuarioDestino) {
        this.core = core;
        this.miUsuario = miUsuario;
        this.usuarioDestino = usuarioDestino;

        setTitle("Privado con: " + usuarioDestino);
        setSize(400, 320);
        // DISPOSE_ON_CLOSE asegura que solo se cierre esta ventana, no el programa
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        areaPrivada = new JTextArea();
        areaPrivada.setEditable(false);
        add(new JScrollPane(areaPrivada), BorderLayout.CENTER);

        JPanel panelSur = new JPanel(new BorderLayout());
        txtEntrada = new JTextField();
        txtEntrada.addActionListener(this);
        btnEnviar = new JButton("Enviar");
        btnEnviar.addActionListener(this);

        panelSur.add(txtEntrada, BorderLayout.CENTER);
        panelSur.add(btnEnviar, BorderLayout.EAST);
        add(panelSur, BorderLayout.SOUTH);

        areaPrivada.append("Charla privada con " + usuarioDestino + "...\n");

        // Cuando la cierras, borra su contenido
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                areaPrivada.setText(""); 
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String texto = txtEntrada.getText().trim();
            if (texto.isEmpty()) return;

            core.enviarMensajePrivado(usuarioDestino, texto);
            areaPrivada.append("[" + miUsuario + " -> " + usuarioDestino + "]: " + texto + "\n");
            txtEntrada.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void recibirMensaje(String origen, String contenido) {
        areaPrivada.append("[" + origen + "]: " + contenido + "\n");
    }
}
