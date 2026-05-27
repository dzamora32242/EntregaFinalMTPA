package com.mycompany.cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class PantallaPrincipal extends JFrame implements ActionListener, MensajeListener {

    private ClienteCore core;
    private String miUsuario;
    private String salonActivo = "";
    private HashMap<String, PantallaChatPrivado> ventanasPrivadas = new HashMap<>();

    private JTextArea areaChat;
    private JTextField txtEntrada;
    private JButton btnEnviar;
    private JButton btnLogout;
    private JButton btnPrivado;
    private JButton btnHistorial;
    private JList<String> listaSalones;
    private DefaultListModel<String> modeloSalones;

    public PantallaPrincipal(ClienteCore core, String miUsuario) {
        this.core = core;
        this.miUsuario = miUsuario;
        this.core.setListener(this); 

        setTitle("Canal de Comunicación MTPA - Usuario: " + miUsuario);
        setSize(700, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Norte (Botones de arriba)
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPrivado = new JButton("Nuevo Mensaje Privado");
        btnHistorial = new JButton("Cargar Historial");
        btnLogout = new JButton("Cerrar Sesión");
        btnPrivado.addActionListener(this);
        btnHistorial.addActionListener(this);
        btnLogout.addActionListener(this);
        panelNorte.add(btnPrivado);
        panelNorte.add(btnHistorial);
        panelNorte.add(btnLogout);
        add(panelNorte, BorderLayout.NORTH);

        // Oeste (Lista de salones)
        JPanel panelOeste = new JPanel(new BorderLayout());
        panelOeste.setBorder(BorderFactory.createTitledBorder("Salones"));
        
        modeloSalones = new DefaultListModel<>();
        modeloSalones.addElement("IA");
        modeloSalones.addElement("Deportes");
        modeloSalones.addElement("Therian");
        modeloSalones.addElement("Manga");
        modeloSalones.addElement("UEMC"); 
        
        listaSalones = new JList<>(modeloSalones);
        listaSalones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaSalones.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listaSalones.getSelectedValue() != null) {
                cambiarDeSalon(listaSalones.getSelectedValue());
            }
        });
        panelOeste.add(new JScrollPane(listaSalones), BorderLayout.CENTER);
        add(panelOeste, BorderLayout.WEST);

        // Centro (El texto del chat)
        areaChat = new JTextArea();
        areaChat.setEditable(false);
        areaChat.setLineWrap(true);
        add(new JScrollPane(areaChat), BorderLayout.CENTER);

        // Sur (Barra para escribir)
        JPanel panelSur = new JPanel(new BorderLayout());
        txtEntrada = new JTextField();
        txtEntrada.addActionListener(this); 
        btnEnviar = new JButton("Enviar");
        btnEnviar.addActionListener(this);
        
        panelSur.add(txtEntrada, BorderLayout.CENTER);
        panelSur.add(btnEnviar, BorderLayout.EAST);
        add(panelSur, BorderLayout.SOUTH);
    }

    private void cambiarDeSalon(String nuevoSalon) {
        try {
            if (!this.salonActivo.equals("")) {
                core.salirCanal(this.salonActivo);
            }
            this.salonActivo = nuevoSalon;
            areaChat.setText("Conectando al salón: " + nuevoSalon + "...\n");
            core.unirCanal(nuevoSalon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnEnviar || e.getSource() == txtEntrada) {
                String texto = txtEntrada.getText().trim();
                if (texto.isEmpty()) return;

                if (texto.length() > 190) {
                    onError("El mensaje excede los 190 caracteres permitidos (" + texto.length() + "/190).");
                    return;
                }

                if (salonActivo.equals("")) {
                    onError("Debe seleccionar un salón de chat en el panel izquierdo antes de escribir.");
                    return;
                }

                core.enviarMensajeCanal(salonActivo, texto);
                txtEntrada.setText("");

            } else if (e.getSource() == btnPrivado) {
                String destino = JOptionPane.showInputDialog(this, "¿Con quién quieres hablar?", "Chat Privado", JOptionPane.QUESTION_MESSAGE);
                if (destino != null && !destino.trim().isEmpty()) {
                    destino = destino.trim();
                    if (!ventanasPrivadas.containsKey(destino)) {
                        PantallaChatPrivado ventana = new PantallaChatPrivado(core, miUsuario, destino);
                        ventanasPrivadas.put(destino, ventana);
                        ventana.setVisible(true);
                    } else {
                        ventanasPrivadas.get(destino).toFront();
                    }
                }

            } else if (e.getSource() == btnHistorial) {
                if (salonActivo.equals("")) {
                    onError("Seleccione un salón de chat primero para cargar su historial.");
                    return;
                }

                String[] opciones = {"Todo el historial", "Última semana", "Último mes"};
                int seleccion = JOptionPane.showOptionDialog(this, 
                    "¿Desde cuándo deseas cargar los mensajes anteriores del salón " + salonActivo + "?", 
                    "Cargar Historial", 
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
                    null, opciones, opciones[0]);

                long timestamp = 0; 
                
                if (seleccion == 1) {
                    timestamp = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000); 
                } else if (seleccion == 2) { 
                    timestamp = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000); 
                }

                if (seleccion >= 0) {
                    areaChat.append(">> Solicitando mensajes anteriores al servidor...\n");
                    core.pedirHistorial(salonActivo, timestamp);
                }

            } else if (e.getSource() == btnLogout) {
                core.logout();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onUnirCanal(boolean exito, String respuesta) {
        if (exito) {
            areaChat.append("== Bienvenido al salón " + salonActivo + " ==\n");
            
        } else {
            areaChat.append("Error al entrar: " + respuesta + "\n");
        }
    }

    @Override
    public void onMensajeCanal(String usuario, String salon, String contenido) {
        if (this.salonActivo.equals(salon)) {
            areaChat.append("[" + usuario + "]: " + contenido + "\n");
        }
    }

    @Override
    public void onMensajePrivado(String origen, String destino, String contenido) {

        PantallaChatPrivado ventana = ventanasPrivadas.get(origen);
        if (ventana == null) {
            ventana = new PantallaChatPrivado(core, miUsuario, origen);
            ventanasPrivadas.put(origen, ventana);
            ventana.setVisible(true);
        }
        ventana.recibirMensaje(origen, contenido);
    }

    @Override
    public void onHistorial(String salon, String historial) {
        if (this.salonActivo.equals(salon)) {
            areaChat.append("--- Historial ---\n");
            String[] mensajes = historial.split("~");
            for (String msg : mensajes) {
                if (!msg.trim().isEmpty()) areaChat.append(msg + "\n");
            }
            areaChat.append("-----------------\n");
        }
    }

    @Override
    public void onNotifyJoin(String usuario, String salon) {
        if (this.salonActivo.equals(salon)) {
            areaChat.append(">>> " + usuario + " se unió al salón.\n");
        }
    }

    @Override
    public void onLogout(String respuesta) {
        JOptionPane.showMessageDialog(this, "Sesión cerrada.", "Adiós", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    @Override
    public void onDesconexion() {
        JOptionPane.showMessageDialog(this, "Conexión perdida.", "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    @Override 
    public void onError(String msg) { 
        onMensajePrivado("SISTEMA", miUsuario, "ERROR: " + msg);
    }
    @Override public void onRegistro(boolean ex, String r) {}
    @Override public void onLogin(boolean ex, String r) {}
    @Override public void onCanales(boolean ex, String r) {}
    @Override public void onSalirCanal(boolean ok, String m) {}
    @Override public void onHeartbeatAck() {}
}
