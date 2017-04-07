package cl.pingon.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cl.pingon.Model.ModelTabsItem;
import cl.pingon.R;

public class AdapterTabs extends BaseAdapter {
    private Context context;
    private ArrayList<ModelTabsItem> list;

    public AdapterTabs(Context context, ArrayList<ModelTabsItem> list){
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
            ViewInflater = inflater.inflate(R.layout.tab_items, null);
        } else {
            ViewInflater = convertView;
        }

        TextView title = (TextView) ViewInflater.findViewById(R.id.title);
        TextView totalString = (TextView) ViewInflater.findViewById(R.id.totalString);
        TextView obligatoriosString = (TextView) ViewInflater.findViewById(R.id.obligatoriosString);

        title.setText(list.get(position).getTitle());
        if(list.get(position).getCheck() == 1){
            title.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.check_circle,0);
        } else {
            title.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
        }
        totalString.setText(list.get(position).getTotal_string());
        obligatoriosString.setText(list.get(position).getObligatorios_string());

        return ViewInflater;
    }
}
