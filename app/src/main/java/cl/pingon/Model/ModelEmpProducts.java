package cl.pingon.Model;

public class ModelEmpProducts {
    int ID;
    String NAME;
    String CODE;
    String YEAR;
    int BRAND_ID;
    int PROJECT_ID;

    public ModelEmpProducts(int ID, String NAME, String CODE, String YEAR, int BRAND_ID, int PROJECT_ID){
        this.ID = ID;
        this.NAME = NAME;
        this.CODE = CODE;
        this.YEAR = YEAR;
        this.BRAND_ID = BRAND_ID;
        this.PROJECT_ID = PROJECT_ID;
    }

    public int getPROJECT_ID() {
        return PROJECT_ID;
    }

    public int getID(){
        return this.ID;
    }

    public String getName(){
        return this.NAME;
    }

    public String getCode(){
        return this.CODE;
    }

    public String getYear(){
        return this.YEAR;
    }

    public int getBrandId(){
        return this.BRAND_ID;
    }
}
