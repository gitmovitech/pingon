package cl.pingon.SQLite;


import android.provider.BaseColumns;

public class TblRegistroDefinition {
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME ="tbl_registro";

        public static final String DOC_ID = "DOC_ID";
        public static final String LOCAL_DOC_ID = "LOCAL_DOC_ID";
        public static final String CAM_ID = "CAM_ID";
        public static final String FRM_ID = "FRM_ID";
        public static final String REG_ID = "REG_ID";
        public static final String CHK_ID = "CHK_ID";
        public static final String ID = "ID";
        public static final String REG_TIPO = "REG_TIPO";
        public static final String REG_VALOR = "REG_VALOR";
        public static final String SEND_STATUS = "SEND_STATUS";
        public static final String REG_METADATOS = "REG_METADATOS";
    }
}
