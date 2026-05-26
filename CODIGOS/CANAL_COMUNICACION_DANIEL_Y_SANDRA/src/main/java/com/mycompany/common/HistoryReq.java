package com.mycompany.common;

public class HistoryReq extends Mensaje {
    private String nombreUsuario;
    private String salon;
    private long fecha_solicitada;

    public HistoryReq(String nombreUsuario, String salon, long fecha_solicitada) {
        super(Primitiva.HISTORY_REQ);
        this.nombreUsuario = nombreUsuario;
        this.salon = salon;
        this.fecha_solicitada = fecha_solicitada;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getSalon() {
        return salon;
    }

    public long getFecha_solicitada() {
        return fecha_solicitada;
    }

    @Override
    public String trasnformacionString() {
        return "HISTORY_REQ|" + nombreUsuario + "|" + salon + "|" + fecha_solicitada;
    }
}
