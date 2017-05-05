package cl.pingon;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import cl.pingon.SQLite.TblDocumentoDefinition;
import cl.pingon.SQLite.TblDocumentoHelper;
import cl.pingon.SQLite.TblRegistroHelper;

public class SyncListenerService extends Service {

    TblDocumentoHelper Documentos;
    Integer Processing = 0;

    public SyncListenerService() {

        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                if(Processing == 0){
                    Sync();
                }
            }
        },0,60000);

    }

    private void Sync(){
        Processing = 1;
        Documentos = new TblDocumentoHelper(this);
        Cursor c = Documentos.getAllSync();
        if(c.getCount() > 0){
            if(detectInternet()){
                while(c.moveToNext()){
                    startService(new Intent(this, SyncService.class));
                    SyncService.startActionSync(this, c.getInt(c.getColumnIndexOrThrow(TblDocumentoDefinition.Entry.ID)));
                }
                Processing = 0;
            } else {
                Processing = 0;
            }
        } else {
            Processing = 0;
        }
        c.close();
    }

    private boolean detectInternet(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null){
            return activeNetwork.isConnected();
        } else{
            return false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
