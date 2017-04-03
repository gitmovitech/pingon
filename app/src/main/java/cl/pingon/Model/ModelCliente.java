package cl.pingon.Model;


public class ModelCliente {
    String ID;
    String NOMBRE;

    public ModelCliente(String ID, String NOMBRE){
        this.ID = ID;
        this.NOMBRE = NOMBRE;
    }

    public String getID() {
        return ID;
    }

    public String getNOMBRE() {
        return NOMBRE;
    }
}
