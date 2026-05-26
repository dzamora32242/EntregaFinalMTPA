package com.mycompany.cliente;

public interface MensajeListener {
    void onRegistro(boolean exito, String respuesta);
    void onLogin(boolean exito, String respuesta);
    void onLogout(String respuesta);
    void onCanales(boolean exito, String respuesta);
    void onUnirCanal(boolean exito, String respuesta);
    void onSalirCanal(boolean ok, String mensaje);
    void onMensajeCanal(String usuario, String salon, String contenido);
    void onMensajePrivado(String origen, String destino, String contenido);
    void onHistorial(String salon, String historial);
    void onNotifyJoin(String usuario, String salon);
    void onHeartbeatAck();
    void onError(String mensaje);
    void onDesconexion();
}
