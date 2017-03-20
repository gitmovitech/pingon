package cl.pingon.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import cl.pingon.Model.CustomLists;
import cl.pingon.R;

public abstract class AdapterCustomLists extends BaseAdapter {

    private Context context;
    private ArrayList<CustomLists> CustomLists;

    public AdapterCustomLists(Context context, ArrayList<CustomLists> CustomLists){
        this.context = context;
        this.CustomLists = CustomLists;
    }

    @Override
    public int getCount() {
        return CustomLists.size();
    }

    @Override
    public Object getItem(int position) {
        return CustomLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View ViewInflater;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewInflater = inflater.inflate(R.layout.item_fecha, null);
        } else {
            ViewInflater = convertView;
        }

        TextView label = (TextView) ViewInflater.findViewById(R.id.fecha_label);
        EditText input = (EditText) ViewInflater.findViewById(R.id.fecha_input);

        label.setText(CustomLists.get(position).getFecha().toString());
        //input.setText(CustomLists.get(position).getFecha());

        return ViewInflater;
    }
}
