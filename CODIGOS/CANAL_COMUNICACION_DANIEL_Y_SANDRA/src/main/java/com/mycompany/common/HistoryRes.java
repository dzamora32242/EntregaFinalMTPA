package com.mycompany.common;

public class HistoryRes extends Mensaje {
    private String salon;
    private String historial;

    public HistoryRes(String salon, String historial) {
        super(Primitiva.HISTORY_RES);
        this.salon = salon;
        this.historial = historial;
    }

    public String getSalon() {
        return salon;
    }

    public String getHistorial() {
        return historial;
    }

    @Override
    public String trasnformacionString() {
        return "HISTORY_RES|" + salon + "|" + historial;
    }
}
