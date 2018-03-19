package cl.pingon.SQLite;

import android.provider.BaseColumns;

public class TblFeriadosDefinition {
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME ="feriados";

        public static final String ID = "id";
        public static final String DAY = "day";
        public static final String MONTH = "month";
        public static final String YEAR = "year";
    }
}