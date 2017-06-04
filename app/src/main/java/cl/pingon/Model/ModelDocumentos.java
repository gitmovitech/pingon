package cl.pingon.Model;

public class ModelDocumentos {

    Integer ID;
    Integer DOC_ID;
    Integer USU_ID;
    Integer FRM_ID;
    String DOC_NOMBRE;
    String DOC_FECHA_CREACION;
    String DOC_FECHA_MODIFICACION;
    String DOC_PDF;
    String DOC_DECLARACION;
    String DOC_EXT_EQUIPO;
    String DOC_EXT_MARCA_EQUIPO;
    String DOC_EXT_NUMERO_SERIE;
    String DOC_EXT_NOMBRE_CLIENTE;
    String DOC_EXT_OBRA;
    String DOC_EXT_ID_CLIENTE;
    String DOC_EXT_ID_PROYECTO;
    String SEND_STATUS;

    public ModelDocumentos(){

    }

    public Integer getID() {
        return ID;
    }

    public Integer getDOC_ID() {
        return DOC_ID;
    }

    public Integer getFRM_ID() {
        return FRM_ID;
    }

    public Integer getUSU_ID() {
        return USU_ID;
    }

    public String getDOC_DECLARACION() {
        return DOC_DECLARACION;
    }

    public String getDOC_FECHA_CREACION() {
        return DOC_FECHA_CREACION;
    }

    public String getDOC_NOMBRE() {
        return DOC_NOMBRE;
    }

    public String getDOC_FECHA_MODIFICACION() {
        return DOC_FECHA_MODIFICACION;
    }

    public void setDOC_ID(Integer DOC_ID) {
        this.DOC_ID = DOC_ID;
    }

    public void setDOC_FECHA_CREACION(String DOC_FECHA_CREACION) {
        this.DOC_FECHA_CREACION = DOC_FECHA_CREACION;
    }

    public String getDOC_PDF() {
        return DOC_PDF;
    }

    public void setDOC_NOMBRE(String DOC_NOMBRE) {
        this.DOC_NOMBRE = DOC_NOMBRE;
    }

    public void setFRM_ID(Integer FRM_ID) {
        this.FRM_ID = FRM_ID;
    }

    public String getDOC_EXT_EQUIPO() {
        return DOC_EXT_EQUIPO;
    }

    public String getDOC_EXT_MARCA_EQUIPO() {
        return DOC_EXT_MARCA_EQUIPO;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getDOC_EXT_ID_CLIENTE() {
        return DOC_EXT_ID_CLIENTE;
    }

    public void setUSU_ID(Integer USU_ID) {
        this.USU_ID = USU_ID;
    }

    public void setDOC_FECHA_MODIFICACION(String DOC_FECHA_MODIFICACION) {
        this.DOC_FECHA_MODIFICACION = DOC_FECHA_MODIFICACION;
    }

    public void setDOC_DECLARACION(String DOC_DECLARACION) {
        this.DOC_DECLARACION = DOC_DECLARACION;
    }

    public void setDOC_PDF(String DOC_PDF) {
        this.DOC_PDF = DOC_PDF;
    }

    public String getDOC_EXT_ID_PROYECTO() {
        return DOC_EXT_ID_PROYECTO;
    }

    public String getDOC_EXT_NOMBRE_CLIENTE() {
        return DOC_EXT_NOMBRE_CLIENTE;
    }

    public String getDOC_EXT_NUMERO_SERIE() {
        return DOC_EXT_NUMERO_SERIE;
    }

    public String getDOC_EXT_OBRA() {
        return DOC_EXT_OBRA;
    }

    public void setDOC_EXT_EQUIPO(String DOC_EXT_EQUIPO) {
        this.DOC_EXT_EQUIPO = DOC_EXT_EQUIPO;
    }

    public void setDOC_EXT_MARCA_EQUIPO(String DOC_EXT_MARCA_EQUIPO) {
        this.DOC_EXT_MARCA_EQUIPO = DOC_EXT_MARCA_EQUIPO;
    }

    public String getSEND_STATUS() {
        return SEND_STATUS;
    }

    public void setDOC_EXT_ID_CLIENTE(String DOC_EXT_ID_CLIENTE) {
        this.DOC_EXT_ID_CLIENTE = DOC_EXT_ID_CLIENTE;
    }

    public void setDOC_EXT_ID_PROYECTO(String DOC_EXT_ID_PROYECTO) {
        this.DOC_EXT_ID_PROYECTO = DOC_EXT_ID_PROYECTO;
    }

    public void setDOC_EXT_NOMBRE_CLIENTE(String DOC_EXT_NOMBRE_CLIENTE) {
        this.DOC_EXT_NOMBRE_CLIENTE = DOC_EXT_NOMBRE_CLIENTE;
    }

    public void setDOC_EXT_NUMERO_SERIE(String DOC_EXT_NUMERO_SERIE) {
        this.DOC_EXT_NUMERO_SERIE = DOC_EXT_NUMERO_SERIE;
    }

    public void setDOC_EXT_OBRA(String DOC_EXT_OBRA) {
        this.DOC_EXT_OBRA = DOC_EXT_OBRA;
    }

    public void setSEND_STATUS(String SEND_STATUS) {
        this.SEND_STATUS = SEND_STATUS;
    }
}
