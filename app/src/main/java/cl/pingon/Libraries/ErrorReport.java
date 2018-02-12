package cl.pingon.Libraries;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by victorvargas on 08-02-18.
 */

public class ErrorReport {

    Context context;
    public ErrorReport(Context context){
        this.context = context;
    }

    public void Send(String user, String error_message){
        String mailto = "mailto:informespingon@gmail.com" +
                "?cc=" + "vvargas@movitech.cl" +
                "&subject=" + Uri.encode("Reporte de Error PingonForm de " + user)+
                "&body=" + Uri.encode(error_message);

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));

        try {
            this.context.startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            //TODO: Handle case where no email app is available
        }
    }
}
