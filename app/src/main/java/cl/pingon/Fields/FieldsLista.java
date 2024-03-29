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
import cl.pingon.SQLite.TblListasGeneralesHelper;
import cl.pingon.SQLite.TblListasGeneralesItemsDefinition;
import cl.pingon.SQLite.TblListasGeneralesItemsHelper;

public class FieldsLista {

    View view;

    public FieldsLista(Context context, LayoutInflater Inflater, ModelChecklistFields Fields){

        this.view = Inflater.inflate(R.layout.item_select, null);
        ArrayList<String> Listado = new ArrayList<String>();
        Listado.add("Seleccione aquí");
        Spinner SpinnerSelect = (Spinner) view.findViewById(R.id.SpinnerSelect);

        int selectIndex = 0;
        int index = 0;
        String itemName = "";

        try{
            TextView TextViewTitle = (TextView) view.findViewById(R.id.TextViewLabel);
            TextViewTitle.setHint(Fields.getCAM_NOMBRE_INTERNO());

            if(Fields.getCAM_MANDATORIO().contains("S")){
                TextView label = (TextView) view.findViewById(R.id.label_obligatorio);
                label.setVisibility(view.VISIBLE);
            }
        } catch(Exception e){

        }

        if(Fields.getCUSTOM_LIST() > 0){
            TblListasGeneralesItemsHelper ListasGenerales = new TblListasGeneralesItemsHelper(context);
            Cursor cursor = ListasGenerales.getByListaId(Fields.getCUSTOM_LIST());

            while(cursor.moveToNext()){
                index++;
                itemName = cursor.getString(cursor.getColumnIndexOrThrow(TblListasGeneralesItemsDefinition.Entry.NAME));
                if(Fields.getCAM_VAL_DEFECTO().equals(itemName)){
                    selectIndex = index;
                }
                Listado.add(itemName);
            }

            cursor.close();
            ListasGenerales.close();
        } else {

            try {
                TblListOptionsHelper DBHelper = new TblListOptionsHelper(context);
                Cursor cursor = DBHelper.getAllByCamId(Fields.getCAM_ID());
                while (cursor.moveToNext()) {
                    index++;
                    itemName = cursor.getString(cursor.getColumnIndexOrThrow(TblListOptionsDefinition.Entry.OPC_VALOR));
                    if(Fields.getCAM_VAL_DEFECTO().equals(itemName)){
                        selectIndex = index;
                    }
                    Listado.add(itemName);
                }
                cursor.close();
                DBHelper.close();
            } catch (Exception e) {
                Log.e("ERROR CAMPO VACIO", e.toString());
            }

        }

        ArrayAdapter ListadoAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, Listado);
        SpinnerSelect.setAdapter(ListadoAdapter);

        if(selectIndex > 0){
            SpinnerSelect.setSelection(selectIndex);
        }

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
