package com.mycompany.common;

public class NotifyJoin extends Mensaje {
    private String usuarioUnido;
    private String nombreSalon;

    public NotifyJoin(String usuarioUnido, String nombreSalon) {
        super(Primitiva.NOTIFY_JOIN);
        this.usuarioUnido = usuarioUnido;
        this.nombreSalon = nombreSalon;
    }

    public String getUsuarioUnido() {
        return usuarioUnido;
    }

    public String getNombreSalon() {
        return nombreSalon;
    }

    @Override
    public String trasnformacionString() {
        return "NOTIFY_JOIN|" + usuarioUnido + "|" + nombreSalon;
    }
}
