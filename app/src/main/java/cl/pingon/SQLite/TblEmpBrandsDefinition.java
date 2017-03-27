package cl.pingon.SQLite;


import android.provider.BaseColumns;

public class TblEmpBrandsDefinition {
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME ="emp_brands";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PROJECT_ID = "project_id";
    }
}
