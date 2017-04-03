package cl.pingon.SQLite;


import android.provider.BaseColumns;

public class TblDocumentoDefinition {
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME ="tbl_documento";

        public static final String ID = "ID";
        public static final String DOC_ID = "DOC_ID";
        public static final String USU_ID = "USU_ID";
        public static final String FRM_ID = "FRM_ID";
        public static final String DOC_NOMBRE = "DOC_NOMBRE";
        public static final String DOC_FECHA_CREACION = "DOC_FECHA_CREACION";
        public static final String DOC_FECHA_MODIFICACION = "DOC_FECHA_MODIFICACION";
        public static final String DOC_PDF = "DOC_PDF";
        public static final String DOC_DECLARACION = "DOC_DECLARACION";
        public static final String DOC_EXT_EQUIPO = "DOC_EXT_EQUIPO";
        public static final String DOC_EXT_MARCA_EQUIPO = "DOC_EXT_MARCA_EQUIPO";
        public static final String DOC_EXT_NUMERO_SERIE = "DOC_EXT_NUMERO_SERIE";
        public static final String DOC_EXT_NOMBRE_CLIENTE = "DOC_EXT_NOMBRE_CLIENTE";
        public static final String DOC_EXT_OBRA = "DOC_EXT_OBRA";
        public static final String DOC_EXT_ID_CLIENTE = "DOC_EXT_ID_CLIENTE";
        public static final String DOC_EXT_ID_PROYECTO = "DOC_EXT_ID_PROYECTO";
        public static final String SEND_STATUS = "SEND_STATUS";
    }
}
