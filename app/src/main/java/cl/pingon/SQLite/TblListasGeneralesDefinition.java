package cl.pingon.SQLite;

import android.provider.BaseColumns;

public class TblListasGeneralesDefinition {
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME ="tbl_listas_generales";

        public static final String ID = "id";
        public static final String NAME = "name";
    }
}
