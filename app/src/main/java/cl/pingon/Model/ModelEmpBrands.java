package cl.pingon.Model;


public class ModelEmpBrands {
    int ID;
    String NAME;

    public ModelEmpBrands(int ID, String NAME){
        this.ID = ID;
        this.NAME = NAME;
    }

    public int getID(){
        return this.ID;
    }

    public String getName(){
        return this.NAME;
    }
}
