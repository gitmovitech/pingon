package cl.pingon;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import cl.pingon.Adapter.AdapterEquipo;
import cl.pingon.Model.ModelCliente;
import cl.pingon.Model.ModelEquipo;
import cl.pingon.Model.ModelProyecto;
import cl.pingon.SQLite.TblDocumentoDefinition;
import cl.pingon.SQLite.TblDocumentoHelper;

public class BorradoresActivity extends AppCompatActivity {

    ListView ListDetalle;
    Intent IntentDetalle;
    Intent IntentBorradores;
    ArrayAdapter<String> adapter;
    String SECCION;

    ArrayList<ModelCliente> Cliente;
    ArrayList<ModelProyecto> Proyecto;
    ArrayList<ModelEquipo> Equipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borradores);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.setTitle("Borradores");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);

        IntentBorradores = new Intent(getApplicationContext(), BorradoresActivity.class);
        IntentDetalle = new Intent(this, ReemplazoTabsActivity.class);
        TblDocumentoHelper Documentos = new TblDocumentoHelper(this);

        SECCION = getIntent().getStringExtra("SECCION");
        if(SECCION == null){
            SECCION = "CLIENTE";
        }
        ArrayList<String> List = new ArrayList<String>();
        Cursor cursor;
        ListDetalle = (ListView) findViewById(R.id.ListDetalle);

        switch(SECCION){
            case "CLIENTE":
                getSupportActionBar().setSubtitle("Seleccione cliente");
                Cliente = new ArrayList<ModelCliente>();
                cursor = Documentos.getDraftsGroupByCliente();
                String cliente;
                String cliente_id;
                while (cursor.moveToNext()){
                    cliente_id = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_ID_CLIENTE));
                    cliente = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE));
                    List.add(cliente);
                    Cliente.add(new ModelCliente(cliente_id, cliente));
                }
                cursor.close();
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, List);
                ListDetalle.setAdapter(adapter);
                break;
            case "PROYECTO":
                getSupportActionBar().setSubtitle("Seleccione proyecto u obra");
                Proyecto = new ArrayList<ModelProyecto>();
                Log.i("EXTRAS", getIntent().getExtras().toString());
                cursor = Documentos.getDraftsGroupByProyecto(Integer.parseInt(getIntent().getStringExtra("CLIENTE_ID")));
                String proyecto;
                String proyecto_id;
                while (cursor.moveToNext()){
                    proyecto_id = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_ID_PROYECTO));
                    proyecto = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_OBRA));
                    List.add(proyecto);
                    Proyecto.add(new ModelProyecto(proyecto_id, proyecto));
                }
                cursor.close();
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, List);
                ListDetalle.setAdapter(adapter);
                break;
            case "EQUIPO":
                getSupportActionBar().setSubtitle("Seleccione el equipo");
                Equipo = new ArrayList<ModelEquipo>();
                cursor = Documentos.getDraftsByProyecto(getIntent().getStringExtra("PROYECTO_ID"));
                String marca;
                String modelo;
                String serie;
                String FRM_ID;
                while (cursor.moveToNext()){
                    marca = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_MARCA_EQUIPO));
                    modelo = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO));
                    serie = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NUMERO_SERIE));
                    FRM_ID = cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.FRM_ID));
                    List.add(marca+" > "+modelo+" > "+serie);
                    Equipo.add(new ModelEquipo(marca, modelo, serie, FRM_ID));
                }
                cursor.close();
                ListDetalle.setAdapter(new AdapterEquipo(this, Equipo) {});
                break;
        }




        ListDetalle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch(SECCION) {
                    case "CLIENTE":
                        IntentBorradores.putExtra("SECCION", "PROYECTO");
                        IntentBorradores.putExtra("CLIENTE_ID", Cliente.get(i).getID());
                        IntentBorradores.putExtra("CLIENTE", Cliente.get(i).getNOMBRE());
                        startActivity(IntentBorradores);
                        break;
                    case "PROYECTO":
                        IntentBorradores.putExtras(getIntent().getExtras());
                        IntentBorradores.putExtra("SECCION", "EQUIPO");
                        IntentBorradores.putExtra("PROYECTO_ID", Proyecto.get(i).getID());
                        IntentBorradores.putExtra("PROYECTO", Proyecto.get(i).getNOMBRE());
                        startActivity(IntentBorradores);
                        break;
                    case "EQUIPO":
                        IntentDetalle.putExtras(getIntent().getExtras());
                        IntentDetalle.putExtra("MARCA", Equipo.get(i).getMarca());
                        IntentDetalle.putExtra("MODELO", Equipo.get(i).getModelo());
                        IntentDetalle.putExtra("SERIE", Equipo.get(i).getSerie());
                        IntentDetalle.putExtra("FRM_ID", Integer.parseInt(Equipo.get(i).getFRM_ID()));
                        startActivity(IntentDetalle);
                        break;
                }
            }
        });
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
