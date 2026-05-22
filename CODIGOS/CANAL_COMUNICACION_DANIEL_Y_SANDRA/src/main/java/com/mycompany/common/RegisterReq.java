package com.mycompany.common;

public class RegisterReq extends Mensaje {

    private String nombreUsuarioSolicitado;

    public RegisterReq(String nombreUsuarioSolicitado) {
        super(Primitiva.REGISTER_REQ);
        this.nombreUsuarioSolicitado = nombreUsuarioSolicitado;
    }

    public String getNombreUsuarioSolicitado() {
        return nombreUsuarioSolicitado;
    }

    @Override
    public String trasnformacionString() {
        return "REGISTER_REQ|" + nombreUsuarioSolicitado;
    }
}
