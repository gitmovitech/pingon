package cl.pingon.Model;


public class ModelEmpBrands {
    int ID;
    String NAME;
    int PROJECT_ID;

    public ModelEmpBrands(int ID, String NAME, int PROJECT_ID){
        this.ID = ID;
        this.NAME = NAME;
        this.PROJECT_ID = PROJECT_ID;
    }

    public int getID(){
        return this.ID;
    }

    public String getName(){
        return this.NAME;
    }

    public int getProjectId(){
        return this.PROJECT_ID;
    }
}
