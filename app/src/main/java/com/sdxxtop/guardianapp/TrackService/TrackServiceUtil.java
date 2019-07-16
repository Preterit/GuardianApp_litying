package com.sdxxtop.guardianapp.TrackService;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.track.AMapTrackClient;
import com.amap.api.track.ErrorCode;
import com.amap.api.track.OnTrackLifecycleListener;
import com.amap.api.track.TrackParam;
import com.amap.api.track.query.entity.LocationMode;
import com.sdxxtop.guardianapp.R;
import com.sdxxtop.guardianapp.ui.activity.HomeActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * @author :  lwb
 * Date: 2019/7/11
 * Desc:
 */
public class TrackServiceUtil {
    private HomeActivity mActivity;
    private static final String CHANNEL_ID_SERVICE_RUNNING = "CHANNEL_ID_SERVICE_RUNNING";
    private AMapTrackClient aMapTrackClient;

    private OnTrackLifecycleListener onTrackListener = new SimpleOnTrackLifecycleListener() {
        @Override
        public void onBindServiceCallback(int status, String msg) {
            Log.w("TrackService", "onBindServiceCallback, status: " + status + ", msg: " + msg);
        }

        @Override
        public void onStartTrackCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.START_TRACK_SUCEE || status == ErrorCode.TrackListen.START_TRACK_SUCEE_NO_NETWORK) {
                // 成功启动
                Toast.makeText(mActivity, "启动服务成功", Toast.LENGTH_SHORT).show();
                if (aMapTrackClient != null) {
                    aMapTrackClient.setTrackId(mTrackId);
                    aMapTrackClient.startGather(onTrackListener);
                }
            } else if (status == ErrorCode.TrackListen.START_TRACK_ALREADY_STARTED) {
                // 已经启动
//                Toast.makeText(mActivity, "服务已经启动", Toast.LENGTH_SHORT).show();
            } else {
                Log.w("TrackService", "error onStartTrackCallback, status: " + status + ", msg: " + msg);
                Toast.makeText(mActivity,
                        "error onStartTrackCallback, status: " + status + ", msg: " + msg,
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onStopTrackCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.STOP_TRACK_SUCCE) {
                // 成功停止
//                Toast.makeText(mActivity, "停止服务成功", Toast.LENGTH_SHORT).show();
            } else {
                Log.w("TrackService", "error onStopTrackCallback, status: " + status + ", msg: " + msg);
                Toast.makeText(mActivity,
                        "error onStopTrackCallback, status: " + status + ", msg: " + msg,
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onStartGatherCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.START_GATHER_SUCEE) {
                Toast.makeText(mActivity, "定位采集开启成功", Toast.LENGTH_SHORT).show();
            } else if (status == ErrorCode.TrackListen.START_GATHER_ALREADY_STARTED) {
//                Toast.makeText(mActivity, "定位采集已经开启", Toast.LENGTH_SHORT).show();
            } else {
                Log.w("TrackService", "error onStartGatherCallback, status: " + status + ", msg: " + msg);
                Toast.makeText(mActivity,
                        "error onStartGatherCallback, status: " + status + ", msg: " + msg,
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onStopGatherCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.STOP_GATHER_SUCCE) {
//                Toast.makeText(mActivity, "定位采集停止成功", Toast.LENGTH_SHORT).show();
            } else {
                Log.w("TrackService", "error onStopGatherCallback, status: " + status + ", msg: " + msg);
                Toast.makeText(mActivity,
                        "error onStopGatherCallback, status: " + status + ", msg: " + msg,
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    private long mTrackId;

    public TrackServiceUtil(HomeActivity activity) {
        this.mActivity = activity;
        // 不要使用Activity作为Context传入
        aMapTrackClient = new AMapTrackClient(activity);
        aMapTrackClient.setInterval(2, 20);
        aMapTrackClient.setLocationMode(LocationMode.HIGHT_ACCURACY);
        aMapTrackClient.setCacheSize(300);
    }

    public void stsrtTrackService(long serviceId, long terminalId, long trackId) {
        mTrackId = trackId;
        TrackParam trackParam = new TrackParam(serviceId, terminalId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trackParam.setNotification(createNotification());
        }
        aMapTrackClient.startTrack(trackParam, onTrackListener);
    }

    /**
     * 在8.0以上手机，如果app切到后台，系统会限制定位相关接口调用频率
     * 可以在启动轨迹上报服务时提供一个通知，这样Service启动时会使用该通知成为前台Service，可以避免此限制
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private Notification createNotification() {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) mActivity.getSystemService(NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = "1";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "智慧罗庄";
            // 用户可以看到的通知渠道的描述
            String description = "notification description";
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_SERVICE_RUNNING, "app service", NotificationManager.IMPORTANCE_LOW);
            NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);


            nm.createNotificationChannel(channel);
            builder = new Notification.Builder(mActivity, CHANNEL_ID_SERVICE_RUNNING);
        } else {
            builder = new Notification.Builder(mActivity);
        }
        Intent nfIntent = new Intent(mActivity, HomeActivity.class);
        nfIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        builder.setContentIntent(PendingIntent.getActivity(mActivity, 0, nfIntent, 0))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(" 后台service ")
                .setContentText(" 后台定位service启动中 ");
        Notification notification = builder.build();
        return notification;
    }

}
