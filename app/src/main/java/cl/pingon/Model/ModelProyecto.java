package cl.pingon.Model;


public class ModelProyecto {
    String ID;
    String NOMBRE;

    public ModelProyecto(String ID, String NOMBRE){
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
