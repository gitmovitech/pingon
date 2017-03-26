package cl.pingon;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import cl.pingon.Model.ModelEmpCompany;
import cl.pingon.SQLite.TblEmpCompanyDefinition;
import cl.pingon.SQLite.TblEmpCompanyHelper;

public class NuevoFormularioActivity extends AppCompatActivity {

    Spinner SpinnerClientes;
    Spinner SpinnerObras;
    Spinner SpinnerMarca;
    Spinner SpinnerEquipo;
    Spinner SpinnerSerie;
    Intent IntentInformes;

    private TblEmpCompanyHelper EmpCompany;
    private ModelEmpCompany Item;
    private ArrayList<ModelEmpCompany> ArrayListModelEmpCompany;

    private ArrayList<String> ListadoArrayListModelEmpCompany;
    private ArrayList<String> ListadoArrayListModelEmpProject;
    private ArrayList<String> ListadoArrayListModelEmpBrand;
    private ArrayList<String> ListadoArrayListModelEmpProduct;
    private ArrayList<String> ListadoArrayListModelEmpSerie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_formulario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        this.setTitle("Nuevo Informe");

        IntentInformes = new Intent(this, InformesActivity.class);

        //final String[] list = {"Constructora Belmar Y Ribba Limitada","Constructora Belmar Y Ribba Limitada","Constructora Belmar Y Ribba Limitada" };

        EmpCompany = new TblEmpCompanyHelper(this);
        Cursor CursorEmpCompany = EmpCompany.getAll();
        ArrayListModelEmpCompany = new ArrayList<ModelEmpCompany>();

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

        ArrayAdapter<String> ArrayAdapterEmpCompany = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListadoArrayListModelEmpCompany);
        ArrayAdapter<String> ArrayAdapterEmpProject = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListadoArrayListModelEmpProject);
        ArrayAdapter<String> ArrayAdapterEmpBrand = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListadoArrayListModelEmpBrand);
        ArrayAdapter<String> ArrayAdapterEmpProduct = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListadoArrayListModelEmpProduct);
        ArrayAdapter<String> ArrayAdapterEmpSerie = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListadoArrayListModelEmpSerie);


        SpinnerClientes = (Spinner) findViewById(R.id.SpinnerClientes);
        SpinnerObras = (Spinner) findViewById(R.id.SpinnerObras);
        SpinnerMarca = (Spinner) findViewById(R.id.SpinnerMarca);
        SpinnerEquipo = (Spinner) findViewById(R.id.SpinnerEquipo);
        SpinnerSerie = (Spinner) findViewById(R.id.SpinnerSerie);


        SpinnerClientes.setAdapter(ArrayAdapterEmpCompany);
        SpinnerObras.setAdapter(ArrayAdapterEmpProject);
        SpinnerMarca.setAdapter(ArrayAdapterEmpBrand);
        SpinnerEquipo.setAdapter(ArrayAdapterEmpProduct);
        SpinnerSerie.setAdapter(ArrayAdapterEmpSerie);


        SpinnerClientes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0){
                    getProjectsInSpinner(i);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(IntentInformes);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
    }

    private void getProjectsInSpinner(int Index){
        ModelEmpCompany Item = ArrayListModelEmpCompany.get(Index);
    }

}
