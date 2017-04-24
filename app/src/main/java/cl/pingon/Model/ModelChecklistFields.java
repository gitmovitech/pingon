package cl.pingon.Model;


import android.view.View;

public class ModelChecklistFields {
    private int CAM_ID;
    private int CAM_POSICION;
    private String CAM_NOMBRE_INTERNO;
    private String CAM_NOMBRE_EXTERNO;
    private String CAM_TIPO;
    private String CAM_MANDATORIO;
    private String CAM_VAL_DEFECTO;
    private String CAM_PLACE_HOLDER;
    private String VALUE;
    private String SISTEMA;

    private View view;

    public ModelChecklistFields(int CAM_ID, int CAM_POSICION, String CAM_NOMBRE_INTERNO, String CAM_NOMBRE_EXTERNO, String CAM_TIPO, String CAM_MANDATORIO, String CAM_VAL_DEFECTO, String CAM_PLACE_HOLDER){
        this.CAM_ID = CAM_ID;
        this.CAM_POSICION = CAM_POSICION;
        this.CAM_NOMBRE_INTERNO = CAM_NOMBRE_INTERNO;
        this.CAM_NOMBRE_EXTERNO = CAM_NOMBRE_EXTERNO;
        this.CAM_TIPO = CAM_TIPO;
        this.CAM_MANDATORIO = CAM_MANDATORIO;
        this.CAM_VAL_DEFECTO = CAM_VAL_DEFECTO;
        this.CAM_PLACE_HOLDER = CAM_PLACE_HOLDER;
        if(CAM_TIPO.contains("sistema")){
            this.SISTEMA = CAM_VAL_DEFECTO;
        } else{
            this.SISTEMA = "";
        }
    }

    public String getSISTEMA(){
        return this.SISTEMA;
    }

    public void setView(View view){
        this.view = view;
    }

    public View getView(){
        return view;
    }

    public void setValue(String value){
        this.VALUE = value;
    }

    public String getValue(){
        return VALUE;
    }

    public int getCAM_ID(){
        return this.CAM_ID;
    }

    public void setCAM_ID(int CAM_ID) {
        this.CAM_ID = CAM_ID;
    }

    public int getCAM_POSICION(){
        return this.CAM_POSICION;
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

    public void setCAM_VAL_DEFECTO(String CAM_VAL_DEFECTO) {
        this.CAM_VAL_DEFECTO = CAM_VAL_DEFECTO;
    }

    public String getCAM_PLACE_HOLDER(){
        return this.CAM_PLACE_HOLDER;
    }
}
