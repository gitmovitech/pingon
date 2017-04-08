package cl.pingon.Fields;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;
import cl.pingon.SQLite.TblListOptionsDefinition;
import cl.pingon.SQLite.TblListOptionsHelper;

public class FieldsLista {

    View view;

    public FieldsLista(Context context, LayoutInflater Inflater, ModelChecklistFields Fields){

        View view = Inflater.inflate(R.layout.item_select, null);
        TextView TextViewTitle = (TextView) view.findViewById(R.id.TextViewLabel);
        TextViewTitle.setHint(Fields.getCAM_NOMBRE_INTERNO());
        Spinner SpinnerSelect = (Spinner) view.findViewById(R.id.SpinnerSelect);

        ArrayList<String> Listado = new ArrayList<String>();

        Listado.add("Seleccione aquí");

        TblListOptionsHelper DBHelper = new TblListOptionsHelper(context);
        try {
            Cursor cursor = DBHelper.getAllByCamId(Fields.getCAM_ID());
            while(cursor.moveToNext()){
                Listado.add(cursor.getString(cursor.getColumnIndexOrThrow(TblListOptionsDefinition.Entry.OPC_VALOR)));
            }
            cursor.close();
        } catch (Exception e){
            Log.e("ERROR CAMPO VACIO", e.toString());
        }

        ArrayAdapter ListadoAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, Listado);
        SpinnerSelect.setAdapter(ListadoAdapter);

        try {
            Fields.setView(view);
        } catch (Exception e){
            Log.e("ERROR VIEW", e.toString());
        }

    }

    public View getView(){
        return this.view;
    }
}
