package cl.pingon.SQLite;


import android.provider.BaseColumns;

public class TblEmpProjectsDefinition {
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME ="emp_projects";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String COORDINATES = "coordinates";
        public static final String ADDRESS = "address";
        public static final String COMPANY_ID = "company_id";
    }
}
