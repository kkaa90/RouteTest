package com.e.routetest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.util.TimeUtils;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    private NotifyRepository notifyRepository;


    public void showDataMessage(String msgTitle, String msgContent) {
        NotifyAppDatabase notifyDb = NotifyAppDatabase.getInstance(this);
        new InsertAsyncTask(notifyDb.notifyRepository()).execute(new Notify(1,msgTitle,msgContent));
        Intent intent = new Intent(this,LoadingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {

            String channelId = "test push";
            String channelName = msgTitle;
            String channelDescription = msgContent;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelDescription);
            //각종 채널에 대한 설정
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300});
            notificationManager.createNotificationChannel(channel);
            //channel이 등록된 builder
            notificationBuilder = new NotificationCompat.Builder(this, channelId);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }
        notificationBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(msgTitle)
                .setAutoCancel(true)
                .setContentText(msgContent)
                .setContentIntent(pendingIntent);
        int localTime = LocalTime.now().getSecond();
        notificationManager.notify(localTime,notificationBuilder.build());

    }

    /**
     * 수신받은 메시지를 Toast로 보여줌
     * @param msgTitle
     * @param msgContent
     */
    public void showNotificationMessage(String msgTitle, String msgContent) {
        NotifyAppDatabase notifyDb = NotifyAppDatabase.getInstance(this);
        Log.i("### noti msgTitle : ", msgTitle);
        Log.i("### noti msgContent : ", msgContent);
        String toastText = String.format("[Notification 메시지] title: %s => content: %s", msgTitle, msgContent);
        Looper.prepare();
        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
        new InsertAsyncTask(notifyDb.notifyRepository()).execute(new Notify(1,msgTitle,msgContent));
        Looper.loop();

    }

    /**
     * 메시지 수신받는 메소드
     * @param msg
     */
    @Override
    public void onMessageReceived(RemoteMessage msg) {
        Log.i("### msg : ", msg.toString());
        if (msg.getData().isEmpty()) {
            showNotificationMessage(msg.getNotification().getTitle(), msg.getNotification().getBody());  // Notification으로 받을 때
        } else {
            showDataMessage(msg.getData().get("title"), msg.getData().get("content"));  // Data로 받을 때
        }
    }
    public static class InsertAsyncTask extends AsyncTask<Notify, Void, Void> {
        private NotifyRepository mNotifyRepository;

        public InsertAsyncTask(NotifyRepository notifyRepository){
            this.mNotifyRepository = notifyRepository;
        }
        @Override
        protected Void doInBackground(Notify... notifies){

            mNotifyRepository.insert(notifies[0]);
            return null;
        }
    }
}
