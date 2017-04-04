package cl.pingon.Model;

public class ModelRegistros {
    
    Integer DOC_ID;
    Integer LOCAL_DOC_ID;
    Integer CAM_ID;
    Integer FRM_ID;
    Integer REG_ID;
    String REG_TIPO;
    String REG_VALOR;
    String SEND_STATUS;
    String REG_METADATOS;

    public ModelRegistros(){

    }

    public void setDOC_ID(Integer DOC_ID) {
        this.DOC_ID = DOC_ID;
    }

    public Integer getDOC_ID() {
        return DOC_ID;
    }

    public Integer getCAM_ID() {
        return CAM_ID;
    }

    public Integer getFRM_ID() {
        return FRM_ID;
    }

    public Integer getLOCAL_DOC_ID() {
        return LOCAL_DOC_ID;
    }

    public Integer getREG_ID() {
        return REG_ID;
    }

    public String getREG_METADATOS() {
        return REG_METADATOS;
    }

    public String getREG_TIPO() {
        return REG_TIPO;
    }

    public String getREG_VALOR() {
        return REG_VALOR;
    }

    public String getSEND_STATUS() {
        return SEND_STATUS;
    }

    public void setCAM_ID(Integer CAM_ID) {
        this.CAM_ID = CAM_ID;
    }

    public void setFRM_ID(Integer FRM_ID) {
        this.FRM_ID = FRM_ID;
    }

    public void setLOCAL_DOC_ID(Integer LOCAL_DOC_ID) {
        this.LOCAL_DOC_ID = LOCAL_DOC_ID;
    }

    public void setREG_ID(Integer REG_ID) {
        this.REG_ID = REG_ID;
    }

    public void setREG_METADATOS(String REG_METADATOS) {
        this.REG_METADATOS = REG_METADATOS;
    }

    public void setREG_TIPO(String REG_TIPO) {
        this.REG_TIPO = REG_TIPO;
    }

    public void setREG_VALOR(String REG_VALOR) {
        this.REG_VALOR = REG_VALOR;
    }

    public void setSEND_STATUS(String SEND_STATUS) {
        this.SEND_STATUS = SEND_STATUS;
    }

}
