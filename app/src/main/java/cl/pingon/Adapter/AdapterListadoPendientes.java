package cl.pingon.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cl.pingon.Model.ListadoPendientes;
import cl.pingon.R;


public class AdapterListadoPendientes extends BaseAdapter {

    private Context context;
    private ArrayList<ListadoPendientes> listado;

    public AdapterListadoPendientes(Context context, ArrayList<ListadoPendientes> listado){
        this.context = context;
        this.listado = listado;
    }

    @Override
    public int getCount() {
        return listado.size();
    }

    @Override
    public Object getItem(int position) {
        return listado.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layoutitem;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutitem = inflater.inflate(R.layout.item_listado_pendientes, null);
        } else {
            layoutitem = convertView;
        }

        TextView cliente = (TextView) layoutitem.findViewById(R.id.cliente);
        TextView obra = (TextView) layoutitem.findViewById(R.id.obra);
        TextView titulo = (TextView) layoutitem.findViewById(R.id.titulo);
        TextView subtitulo = (TextView) layoutitem.findViewById(R.id.subtitulo);
        TextView marca_equipo_serie = (TextView) layoutitem.findViewById(R.id.marca_equipo_serie);

        cliente.setText(listado.get(position).getCliente());
        obra.setText(listado.get(position).getObra());
        titulo.setText(listado.get(position).getFormulario_titulo());
        subtitulo.setText(listado.get(position).getFormulario_subtitulo());
        marca_equipo_serie.setText(listado.get(position).getMarca()+" / "+listado.get(position).getEquipo()+" / "+listado.get(position).getSerie());

        return layoutitem;
    }
}
