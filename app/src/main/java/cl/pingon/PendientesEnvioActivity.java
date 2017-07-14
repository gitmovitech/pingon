package cl.pingon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

import cl.pingon.Adapter.AdapterInformes;
import cl.pingon.Adapter.AdapterListadoPendientes;
import cl.pingon.Model.Informes;
import cl.pingon.Model.ListadoPendientes;
import cl.pingon.SQLite.TblDocumentoDefinition;
import cl.pingon.SQLite.TblDocumentoHelper;
import cl.pingon.SQLite.TblFormulariosDefinition;
import cl.pingon.SQLite.TblFormulariosHelper;

public class PendientesEnvioActivity extends AppCompatActivity {

    SharedPreferences session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendientes_envio);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.setTitle("Pendientes de envío");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);

        session = getSharedPreferences("session", Context.MODE_PRIVATE);

        final ArrayList<ListadoPendientes> ArrayListadoPendientes = new ArrayList<>();
        TblDocumentoHelper Documentos = new TblDocumentoHelper(this);
        TblFormulariosHelper Formularios = new TblFormulariosHelper(this);
        Cursor cursor = Documentos.getAllSync();
        Integer FRM_ID;
        final Integer ARN_ID = Integer.parseInt(session.getString("arn_id", ""));
        final Integer USU_ID = Integer.parseInt(session.getString("user_id", ""));
        Integer cantidad_registros = cursor.getCount();
        while(cursor.moveToNext()){
            FRM_ID = cursor.getInt(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.FRM_ID));
            Cursor c = Formularios.getByArnIdFrmId(ARN_ID,FRM_ID);
            c.moveToFirst();
            ArrayListadoPendientes.add(new ListadoPendientes(
                    cursor.getInt(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NOMBRE_CLIENTE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_OBRA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_MARCA_EQUIPO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_EQUIPO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.DOC_EXT_NUMERO_SERIE)),
                    c.getString(c.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.ARN_NOMBRE)),
                    c.getString(c.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.FRM_NOMBRE))
            ));
            c.close();
        }
        cursor.close();
        Documentos.close();

        AdapterListadoPendientes adapter = new AdapterListadoPendientes(this, ArrayListadoPendientes);
        ListView ListViewEnviados = (ListView) findViewById(R.id.ListDetalle);
        ListViewEnviados.setAdapter(adapter);

        ListViewEnviados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int p, long id) {

                TblFormulariosHelper Formularios = new TblFormulariosHelper(getApplicationContext());
                Cursor cursor = Formularios.getByArnId(Integer.parseInt(session.getString("arn_id", "")));
                cursor.moveToFirst();
                String ARN_NOMBRE = cursor.getString(cursor.getColumnIndexOrThrow(TblFormulariosDefinition.Entry.ARN_NOMBRE));
                cursor.close();

                String pdfPath = Environment.getExternalStorageDirectory() + "/Pingon/pdfs/";
                String pdfFile = pdfPath + ArrayListadoPendientes.get(p).getLocal_doc_id() + "__";
                pdfFile += ARN_NOMBRE + " - ";
                pdfFile += ArrayListadoPendientes.get(p).getCliente() + " - ";
                pdfFile += ArrayListadoPendientes.get(p).getObra() + " - ";
                pdfFile += ArrayListadoPendientes.get(p).getEquipo() + ".pdf";
                Log.d("PDFFILE", pdfFile);

                File file = new File(pdfFile);
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(target);
            }
        });

        if(cantidad_registros == 0){
            ListViewEnviados.setVisibility(View.GONE);
            ImageView iv = (ImageView) findViewById(R.id.NotFound);
            iv.setVisibility(View.VISIBLE);
            Snackbar.make(findViewById(R.id.ListDetalle), "No hay registros pendientes por enviar", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        }

        try{
            InformesTabsActivity.activity.finish();
        } catch (Exception e){}
        try{
            BorradoresActivity.activity.finish();
        } catch (Exception e){}
        try{
            InformesActivity.activity.finish();
        } catch (Exception e){}
        try{
            InformesActivity.activity.finish();
        } catch (Exception e){}
        try{
            NuevoFormularioActivity.activity.finish();
        } catch (Exception e){}

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
