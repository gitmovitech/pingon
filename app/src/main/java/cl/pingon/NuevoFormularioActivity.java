package cl.pingon;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import cl.pingon.Libraries.TimerUtils;
import cl.pingon.Model.ModelEmpBrands;
import cl.pingon.Model.ModelEmpCompany;
import cl.pingon.Model.ModelEmpProducts;
import cl.pingon.Model.ModelEmpProjects;
import cl.pingon.SQLite.TblEmpBrandsDefinition;
import cl.pingon.SQLite.TblEmpBrandsHelper;
import cl.pingon.SQLite.TblEmpCompanyDefinition;
import cl.pingon.SQLite.TblEmpCompanyHelper;
import cl.pingon.SQLite.TblEmpProductsDefinition;
import cl.pingon.SQLite.TblEmpProductsHelper;
import cl.pingon.SQLite.TblEmpProjectsDefinition;
import cl.pingon.SQLite.TblEmpProjectsHelper;

public class NuevoFormularioActivity extends AppCompatActivity {

    AutoCompleteTextView AutocompleteClientes;
    Spinner SpinnerObras;
    Spinner SpinnerMarca;
    Spinner SpinnerEquipo;
    Spinner SpinnerSerie;
    Intent IntentInformes;
    FloatingActionButton fab;

    private TblEmpCompanyHelper EmpCompany;
    private TblEmpProjectsHelper EmpProject;
    private TblEmpBrandsHelper EmpBrand;
    private TblEmpProductsHelper EmpProduct;

    private ModelEmpCompany Item;

    private ArrayList<ModelEmpCompany> ArrayListModelEmpCompany;
    private ArrayList<ModelEmpProjects> ArrayListModelEmpProjects;
    private ArrayList<ModelEmpBrands> ArrayListModelEmpBrands;
    private ArrayList<ModelEmpProducts> ArrayListModelEmpProducts;
    private ArrayList<ModelEmpProducts> ArrayListModelEmpSerie;

    private ArrayList<String> ListadoArrayListModelEmpCompany;
    private ArrayList<String> ListadoArrayListModelEmpProject;
    private ArrayList<String> ListadoArrayListModelEmpBrand;
    private ArrayList<String> ListadoArrayListModelEmpProduct;
    private ArrayList<String> ListadoArrayListModelEmpSerie;

    ArrayAdapter<String> ArrayAdapterEmpCompany;
    ArrayAdapter<String> ArrayAdapterEmpProject;
    ArrayAdapter<String> ArrayAdapterEmpBrand;
    ArrayAdapter<String> ArrayAdapterEmpProduct;
    ArrayAdapter<String> ArrayAdapterEmpSerie;

    Integer COMPANY_ID;
    Integer PROYECTO_ID;

    static Activity activity;

    Menu MenuButton;
    int ActivateSendButton = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_formulario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        activity = this;

        this.setTitle("Nuevo Informe");

        IntentInformes = new Intent(this, InformesActivity.class);

        EmpCompany = new TblEmpCompanyHelper(this);
        EmpProject = new TblEmpProjectsHelper(this);
        EmpBrand = new TblEmpBrandsHelper(this);
        EmpProduct = new TblEmpProductsHelper(this);

        Cursor CursorEmpCompany = EmpCompany.getAll();
        ArrayListModelEmpCompany = new ArrayList<ModelEmpCompany>();
        ArrayListModelEmpProjects = new ArrayList<ModelEmpProjects>();
        ArrayListModelEmpBrands = new ArrayList<ModelEmpBrands>();
        ArrayListModelEmpProducts = new ArrayList<ModelEmpProducts>();
        ArrayListModelEmpSerie = new ArrayList<ModelEmpProducts>();

        ListadoArrayListModelEmpCompany = new ArrayList<String>();
        ListadoArrayListModelEmpProject = new ArrayList<String>();
        ListadoArrayListModelEmpBrand = new ArrayList<String>();
        ListadoArrayListModelEmpProduct = new ArrayList<String>();
        ListadoArrayListModelEmpSerie = new ArrayList<String>();

        int Index = 0;

        ListadoArrayListModelEmpCompany.add(Index, "Seleccione Cliente");
        ListadoArrayListModelEmpProject.add(Index,"Seleccione Obra");
        ListadoArrayListModelEmpBrand.add(Index, "Seleccione Marca");
        ListadoArrayListModelEmpProduct.add(Index, "Seleccione Equipo");
        ListadoArrayListModelEmpSerie.add(Index, "Seleccione Serie");

        ArrayListModelEmpCompany.add(Index, new ModelEmpCompany(0, null, null));
        ArrayListModelEmpProjects.add(Index, new ModelEmpProjects(0, null, null, null, 0));
        ArrayListModelEmpBrands.add(Index, new ModelEmpBrands(0, null, 0));
        ArrayListModelEmpProducts.add(Index, new ModelEmpProducts(0, null, null, null, 0));
        ArrayListModelEmpSerie.add(Index, new ModelEmpProducts(0, null, null, null, 0));


        /**
         * OBTIENE LOS CLIENTES PARA AGREGARLOS AL AutoCompleteTextView
         */
        int RowValueId;
        String RowValueName;
        String RowValueRut;
        while(CursorEmpCompany.moveToNext()) {
            Index++;
            RowValueId = CursorEmpCompany.getInt(CursorEmpCompany.getColumnIndexOrThrow(TblEmpCompanyDefinition.Entry.ID));
            RowValueName = CursorEmpCompany.getString(CursorEmpCompany.getColumnIndexOrThrow(TblEmpCompanyDefinition.Entry.NAME));
            RowValueRut = CursorEmpCompany.getString(CursorEmpCompany.getColumnIndexOrThrow(TblEmpCompanyDefinition.Entry.RUT));
            Item = new ModelEmpCompany(RowValueId, RowValueName, RowValueRut);
            ArrayListModelEmpCompany.add(Index, Item);
            ListadoArrayListModelEmpCompany.add(Index, RowValueName);
        }

        ArrayAdapterEmpCompany = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListadoArrayListModelEmpCompany);
        ArrayAdapterEmpProject = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListadoArrayListModelEmpProject);
        ArrayAdapterEmpBrand = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListadoArrayListModelEmpBrand);
        ArrayAdapterEmpProduct = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListadoArrayListModelEmpProduct);
        ArrayAdapterEmpSerie = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListadoArrayListModelEmpSerie);


        AutocompleteClientes = (AutoCompleteTextView) findViewById(R.id.AutocompleteClientes);
        SpinnerObras = (Spinner) findViewById(R.id.SpinnerObras);
        SpinnerMarca = (Spinner) findViewById(R.id.SpinnerMarca);
        SpinnerEquipo = (Spinner) findViewById(R.id.SpinnerEquipo);
        SpinnerSerie = (Spinner) findViewById(R.id.SpinnerSerie);


        AutocompleteClientes.setAdapter(ArrayAdapterEmpCompany);
        SpinnerObras.setAdapter(ArrayAdapterEmpProject);
        SpinnerMarca.setAdapter(ArrayAdapterEmpBrand);
        SpinnerEquipo.setAdapter(ArrayAdapterEmpProduct);
        SpinnerSerie.setAdapter(ArrayAdapterEmpSerie);


        AutocompleteClientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int index = getIndexFromClients(adapterView.getItemAtPosition(i).toString());
                SpinnerObras.setSelection(0);
                SpinnerMarca.setSelection(0);
                SpinnerEquipo.setSelection(0);
                SpinnerSerie.setSelection(0);
                getProjectsInSpinner(index);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });
        SpinnerObras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0){
                    SpinnerMarca.setSelection(0);
                    SpinnerEquipo.setSelection(0);
                    SpinnerSerie.setSelection(0);
                    getBrandInSpinner(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        SpinnerMarca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0){
                    SpinnerEquipo.setSelection(0);
                    SpinnerSerie.setSelection(0);
                    getProductInSpinner(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        SpinnerSerie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0){
                    for(int m = 0; m < MenuButton.size(); m++){
                        if(MenuButton.getItem(m).getItemId() == R.id.Next){
                            MenuButton.getItem(m).setVisible(true);
                            break;
                        }
                    }
                } else {
                    for(int m = 0; m < MenuButton.size(); m++){
                        if(MenuButton.getItem(m).getItemId() == R.id.Next){
                            MenuButton.getItem(m).setVisible(false);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_nuevo_informe, menu);
        MenuButton = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Next:
                IntentInformes.putExtra("DOC_EXT_ID_CLIENTE", COMPANY_ID);
                IntentInformes.putExtra("DOC_EXT_NOMBRE_CLIENTE", AutocompleteClientes.getText().toString());
                IntentInformes.putExtra("DOC_EXT_ID_PROYECTO", PROYECTO_ID);
                IntentInformes.putExtra("DOC_EXT_OBRA", SpinnerObras.getSelectedItem().toString());
                IntentInformes.putExtra("DOC_EXT_EQUIPO", SpinnerEquipo.getSelectedItem().toString());
                IntentInformes.putExtra("DOC_EXT_MARCA_EQUIPO", SpinnerMarca.getSelectedItem().toString());
                IntentInformes.putExtra("DOC_EXT_NUMERO_SERIE", SpinnerSerie.getSelectedItem().toString());

                startActivity(IntentInformes);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int getIndexFromClients(String clientname){
        int index = 0;
        for(int x = 0; x < ListadoArrayListModelEmpCompany.size(); x++){
            if(clientname.contains(ListadoArrayListModelEmpCompany.get(x))){
                index = x;
            }
        }
        return index;
    }


    /**
     * OBTIENE LOS PROYECTOS/OBRAS SEGUN CLIENTE Y LOS AGREGA AL SPINNER
     * @param Index
     */
    private void getProjectsInSpinner(int Index){
        ModelEmpCompany Item = ArrayListModelEmpCompany.get(Index);
        COMPANY_ID = Item.getID();
        Cursor cursor = EmpProject.getByCompanyId(Item.getID());
        int RowValueId;
        String RowValueName;
        String RowValueCoordinates;
        String RowValueAddress;
        int RowValueCompanyId;

        ListadoArrayListModelEmpProject.clear();
        Index = 0;
        ListadoArrayListModelEmpProject.add(Index,"Seleccione Obra");
        SpinnerObras.setSelection(0);

        ModelEmpProjects ItemEmpProjects;

        while(cursor.moveToNext()) {
            Index++;
            RowValueId = cursor.getInt(cursor.getColumnIndexOrThrow(TblEmpProjectsDefinition.Entry.ID));
            RowValueName = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpProjectsDefinition.Entry.NAME));
            RowValueCoordinates = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpProjectsDefinition.Entry.COORDINATES));
            RowValueAddress = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpProjectsDefinition.Entry.ADDRESS));
            RowValueCompanyId = cursor.getInt(cursor.getColumnIndexOrThrow(TblEmpProjectsDefinition.Entry.COMPANY_ID));
            Log.d("CURSOR", RowValueName);
            ItemEmpProjects = new ModelEmpProjects(RowValueId, RowValueName, RowValueCoordinates, RowValueAddress, RowValueCompanyId);

            ArrayListModelEmpProjects.add(Index, ItemEmpProjects);
            ListadoArrayListModelEmpProject.add(Index, RowValueName);
        }
    }


    /**
     * OBTIENE LAS MARCAS SEGUN PROYECTO Y LOS AGREGA AL SPINNER
     * @param Index
     */
    private void getBrandInSpinner(int Index){
        ModelEmpProjects Item = ArrayListModelEmpProjects.get(Index);
        PROYECTO_ID = Item.getID();
        Cursor cursor = EmpBrand.getByProjectId(Item.getID());
        int RowValueId;
        String RowValueName;
        int RowValueProjectId;

        ListadoArrayListModelEmpBrand.clear();
        Index = 0;
        ListadoArrayListModelEmpBrand.add(Index,"Seleccione Marca");
        SpinnerMarca.setSelection(0);

        ModelEmpBrands ItemEmpBrands;

        while(cursor.moveToNext()) {
            Index++;
            RowValueId = cursor.getInt(cursor.getColumnIndexOrThrow(TblEmpBrandsDefinition.Entry.ID));
            RowValueName = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpBrandsDefinition.Entry.NAME));
            RowValueProjectId = cursor.getInt(cursor.getColumnIndexOrThrow(TblEmpBrandsDefinition.Entry.PROJECT_ID));
            Log.d("CURSOR", RowValueName);
            ItemEmpBrands = new ModelEmpBrands(RowValueId, RowValueName, RowValueProjectId);

            ArrayListModelEmpBrands.add(Index, ItemEmpBrands);
            ListadoArrayListModelEmpBrand.add(Index, RowValueName);
        }
    }


    /**
     * OBTIENE LOS EQUIPOS Y SERIES SEGUN MARCAS Y LAS AGREGA A LOS SPINNERS
     * @param Index
     */
    private void getProductInSpinner(int Index){
        ModelEmpBrands Item = ArrayListModelEmpBrands.get(Index);
        Cursor cursor = EmpProduct.getByBrandId(Item.getID());
        int RowValueId;
        String RowValueName;
        String RowValueCode;
        String RowValueYear;
        int RowValueBrandId;

        ListadoArrayListModelEmpProduct.clear();
        ListadoArrayListModelEmpSerie.clear();
        Index = 0;
        ListadoArrayListModelEmpProduct.add(Index,"Seleccione Equipo");
        SpinnerEquipo.setSelection(0);
        ListadoArrayListModelEmpSerie.add(Index, "Seleccion Serie");
        SpinnerSerie.setSelection(0);

        ModelEmpProducts ItemEmpProducts;

        while(cursor.moveToNext()) {
            Index++;
            RowValueId = cursor.getInt(cursor.getColumnIndexOrThrow(TblEmpProductsDefinition.Entry.ID));
            RowValueName = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpProductsDefinition.Entry.NAME));
            RowValueCode = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpProductsDefinition.Entry.CODE));
            RowValueYear = cursor.getString(cursor.getColumnIndexOrThrow(TblEmpProductsDefinition.Entry.YEAR));
            RowValueBrandId = cursor.getInt(cursor.getColumnIndexOrThrow(TblEmpProductsDefinition.Entry.BRAND_ID));
            Log.d("CURSOR", RowValueName);
            ItemEmpProducts = new ModelEmpProducts(RowValueId, RowValueName, RowValueCode, RowValueYear, RowValueBrandId);

            ArrayListModelEmpProducts.add(Index, ItemEmpProducts);
            ArrayListModelEmpSerie.add(Index, ItemEmpProducts);
            ListadoArrayListModelEmpProduct.add(Index, RowValueName);
            ListadoArrayListModelEmpSerie.add(Index, RowValueCode);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
    }

}
