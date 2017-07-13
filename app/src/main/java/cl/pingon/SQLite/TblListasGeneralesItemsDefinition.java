package cl.pingon.SQLite;

import android.provider.BaseColumns;

public class TblListasGeneralesItemsDefinition {
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME ="tbl_listas_generales_items";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String ID_LISTA = "id_lista";
    }
}
