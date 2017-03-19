package cl.pingon.SQLite;

import android.provider.BaseColumns;

public class TblAreaNegocioDefinition {
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME ="tbl_area_negocio";

        public static final String ARN_ID = "ARN_ID";
        public static final String ARN_NOMBRE = "ARN_NOMBRE";
        public static final String ACTIVO = "activo";
    }
}
