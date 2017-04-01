package cl.pingon.Adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.R;

public abstract class AdapterChecklist extends BaseAdapter {

    private Context context;
    private ArrayList<ModelChecklistFields> ChecklistFields;
    private int contador = 0;

    public AdapterChecklist(Context context, ArrayList<ModelChecklistFields> ChecklistFields){
        this.ChecklistFields = ChecklistFields;
        this.context = context;
    }

    @Override
    public int getCount() {
        return ChecklistFields.size();
    }

    @Override
    public Object getItem(int i) {
        return ChecklistFields.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.d("CAM_TIPO", ChecklistFields.get(contador).getCAM_TIPO());
        contador++;
        View ItemFechaView;

        if(view == null) {
            LayoutInflater Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ItemFechaView = Inflater.inflate(R.layout.item_fecha, null);
        } else {
            ItemFechaView = view;
        }

        return ItemFechaView;
    }
}
