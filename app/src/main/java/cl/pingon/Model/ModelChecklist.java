package cl.pingon.Model;

public class ModelChecklist {
    private int FRM_ID;
    private int CHK_ID;
    private int CAM_ID;
    private int CAM_POSICION;
    private int CUSTOM_LIST;
    private int ACTIVO;
    private String CHK_NOMBRE;
    private String CAM_NOMBRE_INTERNO;
    private String CAM_NOMBRE_EXTERNO;
    private String CAM_TIPO;
    private String CAM_MANDATORIO;
    private String CAM_VAL_DEFECTO;
    private String CAM_PLACE_HOLDER;

    public ModelChecklist(int FRM_ID, int CHK_ID, int CAM_ID, int CAM_POSICION, int CUSTOM_LIST, int ACTIVO, String CHK_NOMBRE, String CAM_NOMBRE_INTERNO, String CAM_NOMBRE_EXTERNO, String CAM_TIPO, String CAM_MANDATORIO, String CAM_VAL_DEFECTO, String CAM_PLACE_HOLDER){
        this.FRM_ID = FRM_ID;
        this.CHK_ID = CHK_ID;
        this.CAM_ID = CAM_ID;
        this.CAM_POSICION = CAM_POSICION;
        this.CUSTOM_LIST = CUSTOM_LIST;
        this.ACTIVO = ACTIVO;
        this.CHK_NOMBRE = CHK_NOMBRE;
        this.CAM_NOMBRE_INTERNO = CAM_NOMBRE_INTERNO;
        this.CAM_NOMBRE_EXTERNO = CAM_NOMBRE_EXTERNO;
        this.CAM_TIPO = CAM_TIPO;
        this.CAM_MANDATORIO = CAM_MANDATORIO;
        this.CAM_VAL_DEFECTO = CAM_VAL_DEFECTO;
        this.CAM_PLACE_HOLDER = CAM_PLACE_HOLDER;
    }

    public int getFRM_ID(){
        return this.FRM_ID;
    }

    public int getCAM_ID(){
        return this.CAM_ID;
    }

    public int getCHK_ID(){
        return this.CHK_ID;
    }

    public int getCAM_POSICION(){
        return this.CAM_POSICION;
    }

    public int getCUSTOM_LIST(){
        return this.CUSTOM_LIST;
    }

    public int getACTIVO(){
        return this.ACTIVO;
    }

    public String getCHK_NOMBRE(){
        return this.CHK_NOMBRE;
    }

    public String getCAM_NOMBRE_INTERNO(){
        return this.CAM_NOMBRE_INTERNO;
    }

    public String getCAM_NOMBRE_EXTERNO(){
        return this.CAM_NOMBRE_EXTERNO;
    }

    public String getCAM_TIPO(){
        return this.CAM_TIPO;
    }

    public String getCAM_MANDATORIO(){
        return this.CAM_MANDATORIO;
    }

    public String getCAM_VAL_DEFECTO(){
        return this.CAM_VAL_DEFECTO;
    }

    public String getCAM_PLACE_HOLDER(){
        return this.CAM_PLACE_HOLDER;
    }
}
