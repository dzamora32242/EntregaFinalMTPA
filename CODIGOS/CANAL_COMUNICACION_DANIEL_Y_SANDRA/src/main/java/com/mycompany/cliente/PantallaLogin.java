package com.mycompany.cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PantallaLogin extends JFrame implements ActionListener, InterfazGrafica {

    private JTextField txtUsuario;
    private JTextField txtContrasena;
    private JButton btnLogin;
    private JButton btnRegistro;
    private ClienteCore core;

    public PantallaLogin(ClienteCore core) {
        this.core = core;
        this.core.setListener(this);

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

    /**
     * Maneja los eventos de Iniciar Sesión y Registrarse
     * Lee los campos de texto y delega la acción pertinente
     * 
     * @param e Evento de acción generado por la interacción del usuario con los botones.
     */
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

    /**
     * Procesa la respuesta del servidor tras una solicitud de registro.
     * Muestra un mensaje de éxito con la contraseña asignada o un cuadro de error si falló.
     * 
     * @param exito true si el registro se completó correctamente, false en caso contrario.
     * @param respuesta La contraseña generada o el mensaje de error devuelto por el servidor.
     */
    @Override
    public void onRegistro(boolean exito, String respuesta) {
        if (exito) {
            JOptionPane.showMessageDialog(this, "Registrado. Su clave es: " + respuesta, "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
            txtContrasena.setText(respuesta);
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + respuesta, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Procesa la respuesta del servidor tras un intento de inicio de sesión.
     * Si es exitoso, cierra la ventana actual y abre la pantalla principal. En caso de error, muestra un aviso.
     * 
     * @param exito true si el inicio de sesión fue validado, false si hubo un error de credenciales.
     * @param respuesta Mensaje informativo o de error devuelto por el servidor.
     */
    @Override
    public void onLogin(boolean exito, String respuesta) {
        if (exito) {
            
            PantallaPrincipal principal = new PantallaPrincipal(core, txtUsuario.getText().trim());
            principal.setVisible(true);
            this.dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas: " + respuesta, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Muestra un cuadro de diálogo al usuario cuando ocurre un error reportado por el servidor.
     * 
     * @param mensaje El texto descriptivo del error a mostrar en pantalla.
     */
    @Override
    public void onError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error del Sistema", JOptionPane.ERROR_MESSAGE);
    }

   
    @Override public void onLogout(String r) {}
    @Override public void onCanales(boolean e, String r) {}
    @Override public void onUnirCanal(boolean e, String r) {}
    @Override public void onSalirCanal(boolean ok, String m) {}
    @Override public void onMensajeCanal(String u, String s, String c) {}
    @Override public void onMensajePrivado(String o, String d, String c) {}
    @Override public void onHistorial(String s, String h) {}
    @Override public void onNotifyJoin(String u, String s) {}
    @Override public void onHeartbeatAck() {}

    /**
     * Maneja la pérdida de conexión inesperada con el servidor.
     * Notifica al usuario mediante una alerta y finaliza la ejecución del cliente.
     */
    @Override public void onDesconexion() {
        JOptionPane.showMessageDialog(this, "Desconectado del servidor.", "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }
}