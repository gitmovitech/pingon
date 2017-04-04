package cl.pingon.Model;

public class ModelEquipo {

    String marca;
    String modelo;
    String serie;
    String FRM_ID;

    public ModelEquipo(String marca, String modelo, String serie, String FRM_ID){
        this.marca = marca;
        this.modelo = modelo;
        this.serie = serie;
        this.FRM_ID = FRM_ID;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getSerie() {
        return serie;
    }

    public String getFRM_ID() {
        return FRM_ID;
    }
}
