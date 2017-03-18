package cl.pingon.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import java.util.ArrayList;

import cl.pingon.Model.Informes;

public abstract class AdapterInformes extends BaseAdapter {

    private Context context;
    private ArrayList<Informes> informes;

    public AdapterInformes(Context context, ArrayList<Informes> persons) {
        this.context = context;
        this.informes = persons;
    }

    @Override
    public int getCount() {
        return informes.size();
    }

    @Override
    public Object getItem(int position) {
        return informes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TwoLineListItem twoLineListItem;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            twoLineListItem = (TwoLineListItem) inflater.inflate(
                    android.R.layout.simple_list_item_2, null);
        } else {
            twoLineListItem = (TwoLineListItem) convertView;
        }

        TextView text1 = twoLineListItem.getText1();
        TextView text2 = twoLineListItem.getText2();

        text1.setText(informes.get(position).getTitle());
        text2.setText("" + informes.get(position).getSubtitle());

        return twoLineListItem;
    }
}
