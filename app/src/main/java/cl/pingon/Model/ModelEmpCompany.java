package cl.pingon.Model;

public class ModelEmpCompany {
    int ID;
    String NAME;
    String RUT;

    public ModelEmpCompany(int ID, String NAME, String RUT){
        this.ID = ID;
        this.NAME = NAME;
        this.RUT = RUT;
    }

    public int getID(){
        return this.ID;
    }

    public String getName(){
        return this.NAME;
    }

    public String getRUT(){
        return this.RUT;
    }
}
