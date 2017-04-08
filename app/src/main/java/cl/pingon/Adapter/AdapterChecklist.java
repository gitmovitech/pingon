package cl.pingon.Adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import cl.pingon.Fields.FieldsBinario;
import cl.pingon.Fields.FieldsEmail;
import cl.pingon.Fields.FieldsFecha;
import cl.pingon.Fields.FieldsHora;
import cl.pingon.Fields.FieldsLista;
import cl.pingon.Fields.FieldsMoneda;
import cl.pingon.Fields.FieldsNumeroEntero;
import cl.pingon.Fields.FieldsSistema;
import cl.pingon.Fields.FieldsText;
import cl.pingon.Fields.FieldsTitle;
import cl.pingon.InformesDetallesActivity;
import cl.pingon.Model.ModelChecklistFields;
import cl.pingon.Model.ModelImage;
import cl.pingon.R;
import cl.pingon.SignDrawActivity;

public abstract class AdapterChecklist extends BaseAdapter {

    private Context context;
    private ArrayList<ModelChecklistFields> ChecklistFields;
    private int contador = 0;

    TextView TextViewTitle;

    InformesDetallesActivity InformesDetallesActivity;
    ArrayList<ModelImage> ImageItems;
    Intent IntentSign;

    public AdapterChecklist(Context context, ArrayList<ModelChecklistFields> ChecklistFields, InformesDetallesActivity InformesDetallesActivity){
        this.ChecklistFields = ChecklistFields;
        this.context = context;
        this.InformesDetallesActivity = InformesDetallesActivity;
        ImageItems = new ArrayList<ModelImage>();
        IntentSign = new Intent(context, SignDrawActivity.class);
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

        View ViewReturn;
        LayoutInflater Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null) {
            ViewReturn = Inflater.inflate(R.layout.item_empty, null);
        } else {
            ViewReturn = view;
        }

        if(contador < getCount()){

            switch(ChecklistFields.get(contador).getCAM_TIPO()){
                case "email":
                    FieldsEmail FieldsEmail = new FieldsEmail(Inflater, ChecklistFields.get(contador));
                    ViewReturn = FieldsEmail.getView();
                    break;
                case "texto":
                    FieldsText FieldsText = new FieldsText(Inflater, ChecklistFields.get(contador), contador);
                    ViewReturn = FieldsText.getView();
                    break;
                case "etiqueta":
                    FieldsTitle FieldsTitle = new FieldsTitle(Inflater, ChecklistFields.get(contador));
                    ViewReturn = FieldsTitle.getView();
                    break;
                case "firma":
                    ViewReturn = Firma(Inflater, ChecklistFields.get(contador), contador);
                    break;
                case "fecha":
                    FieldsFecha FieldsFecha = new FieldsFecha(context, Inflater, ChecklistFields.get(contador));
                    ViewReturn = FieldsFecha.getView();
                    break;
                case "hora":
                    FieldsHora FieldsHora = new FieldsHora(context, Inflater, ChecklistFields.get(contador));
                    ViewReturn = FieldsHora.getView();
                    break;
                case "foto":
                    ViewReturn = Foto(Inflater, ChecklistFields.get(contador), contador);
                    break;
                case "numero_entero":
                    FieldsNumeroEntero FieldsNumeroEntero = new FieldsNumeroEntero(Inflater, ChecklistFields.get(contador));
                    ViewReturn = FieldsNumeroEntero.getView();
                    break;
                case "sistema":
                    FieldsSistema FieldsSistema = new FieldsSistema(Inflater, ChecklistFields.get(contador));
                    ViewReturn = FieldsSistema.getView();
                    break;
                case "moneda":
                    FieldsMoneda FieldsMoneda = new FieldsMoneda(Inflater, ChecklistFields.get(contador));
                    ViewReturn = FieldsMoneda.getView();
                    break;
                case "binario":
                    FieldsBinario FieldsBinario = new FieldsBinario(Inflater, ChecklistFields.get(contador));
                    ViewReturn = FieldsBinario.getView();
                    break;
                case "lista":
                    FieldsLista FieldsLista = new FieldsLista(context, Inflater, ChecklistFields.get(contador));
                    ViewReturn = FieldsLista.getView();
                    break;
                case "video":
                    ViewReturn = Inflater.inflate(R.layout.item_video, null);
                    TextViewTitle = (TextView) ViewReturn.findViewById(R.id.TextViewLabel);
                    TextViewTitle.setHint(ChecklistFields.get(contador).getCAM_NOMBRE_INTERNO());
                    break;
                default:
                    ViewReturn = Inflater.inflate(R.layout.item_title, null);
                    Snackbar.make(view, ChecklistFields.get(contador).getCAM_TIPO(), Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    Log.d("CAM_TIPO", ChecklistFields.get(contador).getCAM_TIPO());
                    break;
            }

        }
        contador++;

        return ViewReturn;
    }

    public ArrayList<ModelChecklistFields> getChecklistData(){
        return ChecklistFields;
    }


    /**
     * CONSTRUCTOR DE FIRMA
     * @param Inflater
     * @param Fields
     * @param RowItemIndex
     * @return
     */
    private View Firma(LayoutInflater Inflater, ModelChecklistFields Fields, final int RowItemIndex){
        View view = Inflater.inflate(R.layout.item_firma, null);
        TextView TextViewLabel = (TextView) view.findViewById(R.id.TextViewLabel);
        TextViewLabel.setText(Fields.getCAM_NOMBRE_INTERNO());
        /*if(Fields.getCAM_VAL_DEFECTO() != null){
            ImageView ImageView = (ImageView) view.findViewById(R.id.ImageViewSign);
            String[] StringSign = Fields.getCAM_VAL_DEFECTO().split("<>");
            DrawSign DrawSign = new DrawSign(StringSign[0]);
            DrawSign.DrawToImageView(ImageView);
        }*/
        Button ButtonFirma = (Button) view.findViewById(R.id.item_firma);
        ButtonFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent IntentSign = new Intent(context, SignDrawActivity.class);
                IntentSign.putExtra("RowItemIndex", String.valueOf(RowItemIndex));
                InformesDetallesActivity.startActivityForResult(IntentSign, 99);
            }
        });
        Fields.setView(view);
        return view;
    }




    /**
     * CONSTRUCTOR DE FOTO
     * @param Inflater
     * @param Fields
     * @param RowItemIndex
     * @return
     */
    private View Foto(LayoutInflater Inflater, ModelChecklistFields Fields, final int RowItemIndex){
        View view = Inflater.inflate(R.layout.item_foto, null);

        Button ButtonFoto = (Button) view.findViewById(R.id.item_foto);
        ButtonFoto.setText(Fields.getCAM_NOMBRE_INTERNO());
        ButtonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InformesDetallesActivity.setCameraIntentAction(RowItemIndex);
            }
        });
        ImageButton ImageButtonFoto = (ImageButton) view.findViewById(R.id.ImageViewFoto);
        ImageButtonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InformesDetallesActivity.showPhoto(RowItemIndex);
            }
        });

        ArrayList<ModelImage> ModelImageItems = new ArrayList<ModelImage>();
        ImageItems.add(new ModelImage(RowItemIndex, ButtonFoto, ImageButtonFoto));
        Fields.setView(view);
        return view;
    }

    public void setImageButton(Bitmap ImageBitmapDecoded, int index){
        for(int x = 0; x < ImageItems.size(); x++){
            if(ImageItems.get(x).getIndex() == index){
                ImageItems.get(x).getImageButtonFoto().setImageBitmap(ImageBitmapDecoded);
                ImageItems.get(x).getImageButtonFoto().setVisibility(View.VISIBLE);
            }
        }
    }


}
