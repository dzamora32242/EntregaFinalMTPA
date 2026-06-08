package com.mycompany.cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class PantallaPrincipal extends JFrame implements ActionListener, InterfazGrafica {

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

        areaChat = new JTextArea();
        areaChat.setEditable(false);
        areaChat.setLineWrap(true);
        add(new JScrollPane(areaChat), BorderLayout.CENTER);

        JPanel panelSur = new JPanel(new BorderLayout());
        txtEntrada = new JTextField();
        txtEntrada.addActionListener(this); 
        btnEnviar = new JButton("Enviar");
        btnEnviar.addActionListener(this);
        
        panelSur.add(txtEntrada, BorderLayout.CENTER);
        panelSur.add(btnEnviar, BorderLayout.EAST);
        add(panelSur, BorderLayout.SOUTH);
    }

    /**
     * Cambia la conexión del usuario del salón actual al nuevo salón especificado.
     * Se encarga de abandonar el canal previo y enviar la petición de unión al nuevo.
     * 
     * @param nuevoSalon El nombre del salón al que el usuario desea unirse.
     */
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

    /**
     * Maneja los eventos de interacción de la interfaz gráfica, como el envío de mensajes,
     * la solicitud de chats privados, la carga del historial o el cierre de sesión.
     * 
     * @param e El evento de acción generado por los componentes de la interfaz.
     */
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

                long lapsoTiempo = 0; 
                
                if (seleccion == 1) {
                    lapsoTiempo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000); 
                } else if (seleccion == 2) { 
                    lapsoTiempo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000); 
                }

                if (seleccion >= 0) {
                    areaChat.append(">> Solicitando mensajes anteriores al servidor...\n");
                    core.pedirHistorial(salonActivo, lapsoTiempo);
                }

            } else if (e.getSource() == btnLogout) {
                core.logout();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Procesa la respuesta del servidor al intentar unirse a un salón de chat.
     * Muestra un mensaje de bienvenida en el área de chat si tiene éxito, o un error en caso contrario.
     * 
     * @param exito true si la unión al salón fue aprobada, false si fue denegada.
     * @param respuesta El mensaje de confirmación o el motivo del error devuelto por el servidor.
     */
    @Override
    public void onUnirCanal(boolean exito, String respuesta) {
        if (exito) {
            areaChat.append("== Bienvenido al salón " + salonActivo + " ==\n");
            
        } else {
            areaChat.append("Error al entrar: " + respuesta + "\n");
        }
    }

    /**
     * Recibe un mensaje público enviado a un salón y lo muestra en el área de texto
     * si el cliente se encuentra actualmente en dicho salón.
     * 
     * @param usuario El nombre del usuario que envió el mensaje.
     * @param salon El salón de chat donde se publicó el mensaje.
     * @param contenido El texto del mensaje.
     */
    @Override
    public void onMensajeCanal(String usuario, String salon, String contenido) {
        if (this.salonActivo.equals(salon)) {
            areaChat.append("[" + usuario + "]: " + contenido + "\n");
        }
    }

    /**
     * Gestiona la recepción de un mensaje privado.
     * Crea una nueva ventana de chat privado si no existe, o actualiza la existente con el nuevo mensaje.
     * 
     * @param origen El nombre del usuario que envía el mensaje privado.
     * @param destino El destinatario del mensaje (este cliente).
     * @param contenido El texto del mensaje recibido.
     */
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

    /**
     * Procesa el historial de mensajes de un salón recibido desde el servidor y lo formatea en pantalla.
     * 
     * @param salon El salón al que pertenece el historial devuelto.
     * @param historial Una cadena con los mensajes concatenados devueltos por el servidor.
     */
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

    /**
     * Notifica en el área de chat que un nuevo usuario se ha unido al salón actual.
     * 
     * @param usuario El nombre del usuario que acaba de entrar.
     * @param salon El nombre del salón al que se ha unido.
     */
    @Override
    public void onNotifyJoin(String usuario, String salon) {
        if (this.salonActivo.equals(salon)) {
            areaChat.append(">>> " + usuario + " se unió al salón.\n");
        }
    }

    /**
     * Maneja la confirmación de cierre de sesión por parte del servidor,
     * mostrando un mensaje de despedida y finalizando la ejecución del cliente.
     * 
     * @param respuesta El mensaje de confirmación de cierre de sesión del servidor.
     */
    @Override
    public void onLogout(String respuesta) {
        JOptionPane.showMessageDialog(this, "Sesión cerrada.", "Adiós", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    /**
     * Actúa cuando se pierde de manera inesperada la conexión con el servidor.
     * Muestra una alerta de error y cierra la aplicación de forma segura.
     */
    @Override
    public void onDesconexion() {
        JOptionPane.showMessageDialog(this, "Conexión perdida.", "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    /**
     * Muestra los mensajes de error notificados por el sistema o el servidor.
     * Utiliza la interfaz de chat privado (simulando un mensaje del sistema) para hacerlos visibles al usuario.
     * 
     * @param msg La descripción del error a mostrar.
     */
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
