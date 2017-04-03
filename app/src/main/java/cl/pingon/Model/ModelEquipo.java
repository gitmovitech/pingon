package cl.pingon.Model;

public class ModelEquipo {

    String marca;
    String modelo;
    String serie;

    public ModelEquipo(String marca, String modelo, String serie){
        this.marca = marca;
        this.modelo = modelo;
        this.serie = serie;
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
}
