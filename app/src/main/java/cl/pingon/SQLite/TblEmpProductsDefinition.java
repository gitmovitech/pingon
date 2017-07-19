package cl.pingon.SQLite;


import android.provider.BaseColumns;

public class TblEmpProductsDefinition {
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME ="emp_products";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String CODE = "code";
        public static final String YEAR = "year";
        public static final String BRAND_ID = "brand_id";
        public static final String PROJECT_ID = "project_id";
    }
}
