package cl.pingon.Model;

public class ModelChecklistSimple {
    private int CHK_ID;
    private String CHK_NOMBRE;

    public ModelChecklistSimple(int CHK_ID, String CHK_NOMBRE){
        this.CHK_ID = CHK_ID;
        this .CHK_NOMBRE = CHK_NOMBRE;
    }

    public int getCHK_ID(){
        return this.CHK_ID;
    }

    public String getCHK_NOMBRE(){
        return this.CHK_NOMBRE;
    }
}
