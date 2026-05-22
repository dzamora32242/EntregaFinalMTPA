package com.mycompany.common;

public class HistoryRes extends Mensaje {
    private String salon;
    

    public HistoryRes (String salon){
        super(Primitiva.HISTORY_RES);
        this.salon = salon;
    }


    @Override
    public String toEncodedString() {
        return "LOGIN_REQ|" + "|" + salon;
    }
    
}

