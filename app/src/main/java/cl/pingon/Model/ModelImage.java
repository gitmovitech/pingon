package cl.pingon.Model;


import android.widget.Button;
import android.widget.ImageButton;

public class ModelImage {
    Button ButtonFoto;
    ImageButton ImageButtonFoto;
    int Index;

    public ModelImage(int Index, Button ButtonFoto, ImageButton ImageButtonFoto){
        this.Index = Index;
        this.ButtonFoto = ButtonFoto;
        this.ImageButtonFoto = ImageButtonFoto;
    }

    public int getIndex(){
        return Index;
    }

    public Button getButtonFoto(){
        return ButtonFoto;
    }

    public ImageButton getImageButtonFoto(){
        return ImageButtonFoto;
    }
}
