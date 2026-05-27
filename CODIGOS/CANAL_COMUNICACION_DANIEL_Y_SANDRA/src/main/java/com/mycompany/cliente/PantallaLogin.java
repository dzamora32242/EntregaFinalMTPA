package com.mycompany.cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PantallaLogin extends JFrame implements ActionListener, MensajeListener {

    private JTextField txtUsuario;
    private JTextField txtContrasena;
    private JButton btnLogin;
    private JButton btnRegistro;
    private ClienteCore core;

    public PantallaLogin(ClienteCore core) {
        this.core = core;
        this.core.setListener(this); // Le decimos al core que esta ventana le escucha

        setTitle("Mensajería MTPA - Autenticación");
        setSize(360, 200);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelForm = new JPanel(new GridLayout(2, 2, 8, 8));
        panelForm.add(new JLabel("  Nombre de Usuario:"));
        txtUsuario = new JTextField();
        panelForm.add(txtUsuario);

        panelForm.add(new JLabel("  Contraseña asignada:"));
        txtContrasena = new JTextField();
        panelForm.add(txtContrasena);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnLogin = new JButton("Iniciar Sesión");
        btnRegistro = new JButton("Registrarse");

        btnLogin.addActionListener(this);
        btnRegistro.addActionListener(this);

        panelBotones.add(btnLogin);
        panelBotones.add(btnRegistro);

        add(panelForm, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String usuario = txtUsuario.getText().trim();
        
        if (e.getSource() == btnLogin) {
            String pass = txtContrasena.getText().trim();
            if (usuario.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Rellene todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                core.login(usuario, pass);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
        } else if (e.getSource() == btnRegistro) {
            if (usuario.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Introduzca un nombre de usuario.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                core.registrar(usuario);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onRegistro(boolean exito, String respuesta) {
        if (exito) {
            JOptionPane.showMessageDialog(this, "Registrado. Su clave es: " + respuesta, "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
            txtContrasena.setText(respuesta); // Rellena la caja automáticamente
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + respuesta, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onLogin(boolean exito, String respuesta) {
        if (exito) {
            // ¡MAGIA! Ocultamos el login y abrimos el entorno principal
            PantallaPrincipal principal = new PantallaPrincipal(core, txtUsuario.getText().trim());
            principal.setVisible(true);
            this.dispose(); // Destruimos la ventana de login
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas: " + respuesta, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error del Sistema", JOptionPane.ERROR_MESSAGE);
    }

    // Métodos vacíos que no se usan en el Login
    @Override public void onLogout(String r) {}
    @Override public void onCanales(boolean e, String r) {}
    @Override public void onUnirCanal(boolean e, String r) {}
    @Override public void onSalirCanal(boolean ok, String m) {}
    @Override public void onMensajeCanal(String u, String s, String c) {}
    @Override public void onMensajePrivado(String o, String d, String c) {}
    @Override public void onHistorial(String s, String h) {}
    @Override public void onNotifyJoin(String u, String s) {}
    @Override public void onHeartbeatAck() {}
    @Override public void onDesconexion() {
        JOptionPane.showMessageDialog(this, "Desconectado del servidor.", "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }
}