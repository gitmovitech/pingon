package cl.pingon.SQLite;

import android.provider.BaseColumns;

public class TblListOptionsDefinition {
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME ="tbl_list_options";

        public static final String FRM_ID = "FRM_ID";
        public static final String CAM_ID = "CAM_ID";
        public static final String OPC_ID = "OPC_ID";
        public static final String OPC_VALOR = "OPC_VALOR";
        public static final String OPC_NOMBRE = "OPC_NOMBRE";
    }
}
