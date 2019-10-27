package br.com.boleto.fatec_ipi_noite_pdm_firebase_auth_firestore;

import android.widget.ImageView;

import java.util.Date;

class Mensagem implements Comparable <Mensagem> {

    @Override
    public int compareTo(Mensagem mensagem) {
        return this.date.compareTo(mensagem.date);
    }

    private String texto;
    private Date date;
    private String email;
    private boolean vMsg;


    public Mensagem () {

    }

    public Mensagem(String texto, Date date, String email, boolean vMsg) {
        this.texto = texto;
        this.date = date;
        this.email = email;
        this.vMsg = vMsg;
    }

    public String getTexto() {
        return texto;
    }

    public Date getDate() {
        return date;
    }

    public String getEmail() {
        return email;
    }

    public boolean getvMsg() {return vMsg;}



    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setvMsg(boolean vMsg) {
        this.vMsg = vMsg;
    }


}
