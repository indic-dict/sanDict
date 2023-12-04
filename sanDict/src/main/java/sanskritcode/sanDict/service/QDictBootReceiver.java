package sanskritcode.sanDict.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import sanskritcode.sanDict.R;
import sanskritcode.sanDict.utils.Utils.Def;

public class QDictBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferenceSettings = context.getSharedPreferences(Def.APP_NAME, Context.MODE_PRIVATE);
        boolean isCapture = preferenceSettings
                .getBoolean(context.getResources().getString(R.string.prefs_key_using_capture), false);
        if (isCapture) {
            Intent i = new Intent(context.getApplicationContext(), QDictService.class);
            context.startService(i);
        }
    }
}
