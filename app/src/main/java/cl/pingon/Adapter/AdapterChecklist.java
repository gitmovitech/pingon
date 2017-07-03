package cl.pingon.Adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

import cl.pingon.Fields.FieldsBinario;
import cl.pingon.Fields.FieldsEmail;
import cl.pingon.Fields.FieldsFecha;
import cl.pingon.Fields.FieldsHora;
import cl.pingon.Fields.FieldsLista;
import cl.pingon.Fields.FieldsMoneda;
import cl.pingon.Fields.FieldsNumeroEntero;
import cl.pingon.Fields.FieldsSistema;
import cl.pingon.Fields.FieldsSliderbar;
import cl.pingon.Fields.FieldsText;
import cl.pingon.Fields.FieldsTitle;
import cl.pingon.InformesDetallesActivity;
import cl.pingon.Libraries.DrawSign;
import cl.pingon.Libraries.ImageUtils;
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
    Integer FRM_ID;

    int Timer = 1000;

    public AdapterChecklist(Context context, ArrayList<ModelChecklistFields> ChecklistFields, InformesDetallesActivity InformesDetallesActivity, Integer FRM_ID){
        this.ChecklistFields = ChecklistFields;
        this.context = context;
        this.InformesDetallesActivity = InformesDetallesActivity;
        this.FRM_ID = FRM_ID;
        ImageItems = new ArrayList<ModelImage>();
        IntentSign = new Intent(context, SignDrawActivity.class);
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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
        return i;
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
                case "rut_responsable":
                case "responsable":
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
                case "hora_entrada":
                case "hora_salida":
                    FieldsHora FieldsHora = new FieldsHora(context, Inflater, ChecklistFields.get(contador), ChecklistFields);
                    ViewReturn = FieldsHora.getView();
                    break;
                case "foto":
                    ViewReturn = Foto(Inflater, ChecklistFields.get(contador), contador);
                    break;
                case "numero_entero":
                    FieldsNumeroEntero FieldsNumeroEntero = new FieldsNumeroEntero(Inflater, ChecklistFields.get(contador), ChecklistFields);
                    ViewReturn = FieldsNumeroEntero.getView();
                    break;
                case "hora_total_diaria":
                case "hora_total_semanal":
                case "hora_total_semanal_extra":
                case "sistema":
                    FieldsSistema FieldsSistema = new FieldsSistema(Inflater, ChecklistFields.get(contador), FRM_ID, context);
                    ViewReturn = FieldsSistema.getView();
                    break;
                case "moneda":
                    FieldsMoneda FieldsMoneda = new FieldsMoneda(Inflater, ChecklistFields.get(contador), ChecklistFields);
                    ViewReturn = FieldsMoneda.getView();
                    break;
                case "binario":
                case "hora_colacion":
                    FieldsBinario FieldsBinario = new FieldsBinario(Inflater, ChecklistFields.get(contador), ChecklistFields);
                    ViewReturn = FieldsBinario.getView();
                    break;
                case "lista":
                    FieldsLista FieldsLista = new FieldsLista(context, Inflater, ChecklistFields.get(contador));
                    ViewReturn = FieldsLista.getView();
                    break;
                case "slider_bar":
                    FieldsSliderbar FieldsSliderbar = new FieldsSliderbar(Inflater, ChecklistFields.get(contador));
                    ViewReturn = FieldsSliderbar.getView();
                    break;
                /*case "video":
                    ViewReturn = Video(Inflater, ChecklistFields.get(contador), contador);
                    break;
                /*default:
                    ViewReturn = Inflater.inflate(R.layout.item_empty, null);
                    break;*/
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

        try{
            if(Fields.getCAM_MANDATORIO().contains("S")){
                TextView label = (TextView) view.findViewById(R.id.label_obligatorio);
                label.setVisibility(view.VISIBLE);
            }
        } catch(Exception e){

        }

        if(Fields.getCAM_VAL_DEFECTO() != null){
            if(!Fields.getCAM_VAL_DEFECTO().isEmpty()) {
                ImageView ImageView = (ImageView) view.findViewById(R.id.ImageViewSign);
                DrawSign DrawSign = new DrawSign(Fields.getCAM_VAL_DEFECTO());
                DrawSign.DrawToImageView(ImageView);
            }
        }
        Button ButtonFirma = (Button) view.findViewById(R.id.item_firma);
        ButtonFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent IntentSign = new Intent(context, SignDrawActivity.class);
                IntentSign.putExtra("RowItemIndex", String.valueOf(RowItemIndex));
                InformesDetallesActivity.startActivityForResult(IntentSign, 10);
            }
        });
        Fields.setView(view);
        return view;
    }





    /**
     * CONSTRUCTOR DE VIDEO
     * @param Inflater
     * @param Fields
     * @param RowItemIndex
     * @return
     */
    VideoView VideoViewItem;
    LinearLayout LinearLayoutVideo;
    private View Video(LayoutInflater Inflater, ModelChecklistFields Fields, final int RowItemIndex){
        View ItemVideoView = Inflater.inflate(R.layout.item_video, null);

        TextViewTitle = (TextView) ItemVideoView.findViewById(R.id.TextViewLabel);
        TextViewTitle.setHint(Fields.getCAM_NOMBRE_INTERNO());

        VideoViewItem = (VideoView) ItemVideoView.findViewById(R.id.VideoViewItem);
        LinearLayoutVideo = (LinearLayout) ItemVideoView.findViewById(R.id.LinearLayoutVideo);
        Button ButtonVideoView = (Button) ItemVideoView.findViewById(R.id.ButtonVideoView);
        ButtonVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InformesDetallesActivity.dispatchTakeVideoIntent(RowItemIndex, FRM_ID);
            }
        });
        Button ButtonVideoPlay = (Button) ItemVideoView.findViewById(R.id.ButtonVideoPlay);
        ButtonVideoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoViewItem.start();
            }
        });
        Button ButtonVideoStop = (Button) ItemVideoView.findViewById(R.id.ButtonVideoStop);
        ButtonVideoStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoViewItem.pause();
            }
        });
        Button ButtonVideoRewind = (Button) ItemVideoView.findViewById(R.id.ButtonVideoRewind);
        ButtonVideoRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoViewItem.seekTo(0);
            }
        });

        if(Fields.getCAM_VAL_DEFECTO() != null){
            if(!Fields.getCAM_VAL_DEFECTO().isEmpty()) {
                try {
                    setVideoURI(Uri.parse(Fields.getCAM_VAL_DEFECTO()));
                    setLinearLayoutVideoVisibility();
                    setVideoViewItemVisibility();
                } catch(Exception e){}
            }
        }

        Fields.setView(ItemVideoView);
        return ItemVideoView;
    }
    public void setVideoURI(Uri videoUri){
        VideoViewItem.setVideoURI(videoUri);
    }
    public void setLinearLayoutVideoVisibility(){
        LinearLayoutVideo.setVisibility(View.VISIBLE);
    }
    public void setVideoViewItemVisibility(){
        VideoViewItem.setVisibility(View.VISIBLE);
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

        try{
            if(Fields.getCAM_MANDATORIO().contains("S")){
                TextView label = (TextView) view.findViewById(R.id.label_obligatorio);
                label.setVisibility(view.VISIBLE);
            }
        } catch(Exception e){}

        Button ButtonFoto = (Button) view.findViewById(R.id.item_foto);
        ButtonFoto.setText(Fields.getCAM_NOMBRE_INTERNO());
        ButtonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InformesDetallesActivity.setCameraIntentAction(RowItemIndex, FRM_ID);
            }
        });
        ImageButton ImageButtonFoto = (ImageButton) view.findViewById(R.id.ImageViewFoto);
        ImageButtonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InformesDetallesActivity.showPhoto(RowItemIndex, FRM_ID);
            }
        });

        ArrayList<ModelImage> ModelImageItems = new ArrayList<ModelImage>();
        ImageItems.add(new ModelImage(RowItemIndex, ButtonFoto, ImageButtonFoto));


        if(Fields.getCAM_VAL_DEFECTO() != null){
            if(!Fields.getCAM_VAL_DEFECTO().isEmpty()) {
                try {
                    ImageUtils img = new ImageUtils();
                    Bitmap ImageBitmapDecoded = img.ImageThumb(BitmapFactory.decodeFile(Fields.getCAM_VAL_DEFECTO()));
                    setImageButton(ImageBitmapDecoded, RowItemIndex);
                } catch(Exception e){}
            }
        }

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
