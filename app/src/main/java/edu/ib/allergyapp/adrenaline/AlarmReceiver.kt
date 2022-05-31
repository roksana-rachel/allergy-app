package edu.ib.allergyapp.adrenaline

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import edu.ib.allergyapp.R

class AlarmReceiver: BroadcastReceiver() {

    companion object{
        const val ID = "CHANEL_ID"
        const val chanelName = "CHANEL_NAME"
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelNotification: NotificationCompat.Builder =
            NotificationCompat.Builder(context, ID).setContentTitle("Adrenalina")
                .setContentText("Pora wymienić adrenalinę").setSmallIcon(R.drawable.notif_foreground)

        val channel = NotificationChannel(ID, chanelName, NotificationManager.IMPORTANCE_HIGH)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(channel)
            manager.notify(1, channelNotification.build())
        }
    }
}