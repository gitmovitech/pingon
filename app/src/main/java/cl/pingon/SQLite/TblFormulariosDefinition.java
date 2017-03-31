package cl.pingon.SQLite;


import android.provider.BaseColumns;

public class TblFormulariosDefinition {
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME ="tbl_formularios";

        public static final String ARN_ID = "ARN_ID";
        public static final String ARN_NOMBRE = "ARN_NOMBRE";
        public static final String FRM_ID = "FRM_ID";
        public static final String FRM_NOMBRE = "FRM_NOMBRE";
    }
}
