package cl.pingon.Model;


public class ModelEmpProjects {
    int ID;
    String NAME;
    String COORDINATES;
    String ADDRESS;
    int COMPANY_ID;

    public ModelEmpProjects(int ID, String NAME, String COORDINATES, String ADDRESS, int COMPANY_ID){
        this.ID = ID;
        this.NAME = NAME;
        this.COORDINATES = COORDINATES;
        this.ADDRESS = ADDRESS;
        this.COMPANY_ID = COMPANY_ID;
    }

    public int getID(){
        return this.ID;
    }

    public String getName(){
        return this.NAME;
    }

    public String getCoordinates(){
        return this.COORDINATES;
    }

    public String getAddress(){
        return this.ADDRESS;
    }

    public int getCompanyId(){
        return this.COMPANY_ID;
    }
}
