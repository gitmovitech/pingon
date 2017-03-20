package cl.pingon.Model;


import java.util.Date;

public class CustomLists {

    private int type;
    private Date fecha;

    public CustomLists(int type, Date fecha){
        this.type = type;
        this.fecha = fecha;
    }

    public void setType(int type){
        this.type = type;
    }

    public int getType(){
        return this.type;
    }

    public void setFecha(Date fecha){
        this.fecha = fecha;
    }

    public Date getFecha(){
        return this.fecha;
    }
}
