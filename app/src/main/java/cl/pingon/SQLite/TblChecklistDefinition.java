package cl.pingon.SQLite;


import android.provider.BaseColumns;

public class TblChecklistDefinition {
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME ="tbl_checklist";

        public static final String FRM_ID = "FRM_ID";
        public static final String CHK_ID = "CHK_ID";
        public static final String CHK_NOMBRE = "CHK_NOMBRE";
        public static final String CAM_NOMBRE_INTERNO = "CAM_NOMBRE_INTERNO";
        public static final String CAM_NOMBRE_EXTERNO = "CAM_NOMBRE_EXTERNO";
        public static final String CAM_TIPO = "CAM_TIPO";
        public static final String CAM_POSICION = "CAM_POSICION";
        public static final String CAM_MANDATORIO = "CAM_MANDATORIO";
        public static final String CAM_VAL_DEFECTO = "CAM_VAL_DEFECTO";
        public static final String CAM_ID = "CAM_ID";
        public static final String CAM_PLACE_HOLDER = "CAM_PLACE_HOLDER";
        public static final String CUSTOM_LIST = "CUSTOM_LIST";
        public static final String ACTIVO = "ACTIVO";
    }
}
