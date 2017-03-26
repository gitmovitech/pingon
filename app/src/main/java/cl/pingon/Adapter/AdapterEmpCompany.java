package cl.pingon.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import cl.pingon.Model.ModelEmpCompany;

public class AdapterEmpCompany extends BaseAdapter {
    private Context context;
    private ArrayList<ModelEmpCompany> list;

    public AdapterEmpCompany(Context context, ArrayList<ModelEmpCompany> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            ViewInflater = inflater.inflate(android.R.layout.simple_spinner_item, null);
        } else {
            ViewInflater = convertView;
        }


        return ViewInflater;
    }
}
