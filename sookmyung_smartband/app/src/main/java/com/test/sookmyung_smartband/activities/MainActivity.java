package com.test.sookmyung_smartband.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.test.sookmyung_smartband.R;
import com.test.sookmyung_smartband.adapter.PhoneBookAdapter;
import com.test.sookmyung_smartband.beans.AndroidPermissions;
import com.test.sookmyung_smartband.beans.PhoneBook;
import com.test.sookmyung_smartband.ui.OnInnerViewClickListener;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private PhoneBookAdapter phoneBookAdapter;
    String sfName = "myFile";
    int S;
    long start=0,start2=0,start3=0;
    String str_min = "", str_max = "", str_bpm = "",str_percent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);
        ImageButton btn_1 = (ImageButton) findViewById(R.id.locationBtn);
        btn_1.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                        startActivity(intent);
                    }
                });

        ImageButton btn2 = (ImageButton) findViewById(R.id.heartBtn);
        btn2.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, HeartActivity.class);
                        startActivity(intent);

                        NotificationManager notificationManager = (NotificationManager) MainActivity.this.getSystemService(MainActivity.this.NOTIFICATION_SERVICE);
                        Intent intent1 = new Intent(MainActivity.this.getApplicationContext(), MainActivity.class); //인텐트 생성.
                    }
                });

        ImageButton btn3 = (ImageButton) findViewById(R.id.moveBtn);
        btn3.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, MoveActivity.class);
                        startActivity(intent);
                    }
                });

        ImageButton btn4 = (ImageButton) findViewById(R.id.healthBtn);
        btn4.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, HealthActivity.class);
                        startActivity(intent);
                    }
                });

        phoneBookAdapter = new PhoneBookAdapter(this);
        phoneBookAdapter.setOnBtnPhoneClickListener(onCallClickListener);

        phoneBookAdapter.setOnBtnDeleteClickListener(new OnInnerViewClickListener() {
            @Override
            public void onItemClick(final String data) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                new Delete().from(PhoneBook.class).where("code = ?", data).execute();
                                loadData();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(phoneBookAdapter);

        TimerTask adTast = new TimerTask() {
            public void run() {
                popupHeart();
            }
        };
        Timer timer = new Timer();
        timer.schedule(adTast, 0, 5000); // 0초후 첫실행, 5초마다 계속실행

        TimerTask adTast3 = new TimerTask() {
            public void run() {
                popupLocation();
            }
        };
        Timer timer3 = new Timer();
        timer3.schedule(adTast3, 0, 5000); // 0초후 첫실행, 5초마다 계속실행

        TimerTask adTast2 = new TimerTask() {
            public void run() {
                popupMove();

            }
        };
        Timer timer2 = new Timer();
        timer2.schedule(adTast2, 0, 1000); // 0초후 첫실행, 1초마다 계속실행
    }

    private void popupLocation() {
        NotificationManager notificationManager_L = (NotificationManager) MainActivity.this.getSystemService(MainActivity.this.NOTIFICATION_SERVICE);
        Intent intent_L = new Intent(MainActivity.this.getApplicationContext(), NotiActivity.class); //인텐트 생성.
        Notification.Builder builder_L = new Notification.Builder(getApplicationContext());
        intent_L.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);//현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를 없앤다.
        PendingIntent pendingNotificationIntent_L = PendingIntent.getActivity(MainActivity.this, 0, intent_L, PendingIntent.FLAG_UPDATE_CURRENT);

        SharedPreferences pref = getSharedPreferences(sfName, MODE_PRIVATE);
        SharedPreferences pref2 = getSharedPreferences("setting", MODE_PRIVATE);

        String Dis = pref.getString("dis", "");//최소 거리 설정 가져옴
        if(Dis == null || Dis.trim().equals("")){
            Dis = "0";
        }
        double Distance=  Double.parseDouble(Dis);//문자열을 더블로
        String calDis = pref.getString("calDis", "");//집까지의 거리 가져옴
        if(calDis == null || calDis.trim().equals("")){
            calDis = "0";
        }
        double calDistance=Double.parseDouble(calDis);//문자열을 더블로

        if(calDistance>Distance) {//거리가 멀다면
            if (pref2.getBoolean("chk_3", false)) {//거리 알림에 체크가 되어있다면
                long now3 = System.currentTimeMillis() / 1000;
                long how_pass_time = now3 - start3;
                if (how_pass_time >= 5) {//5초에 한번

                    builder_L.setSmallIcon(R.drawable.mainlogo).setTicker("똑띠알림").setWhen(System.currentTimeMillis())
                            .setNumber(1).setContentTitle("거리").setContentText("거주지에서 멀리 떨어져있습니다.")
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent_L).setAutoCancel(true).setOngoing(true);
                    notificationManager_L.notify(1, builder_L.build());

                    start3 = System.currentTimeMillis()/1000;
                }
            }
        }
    }

    private void popupMove(){
        NotificationManager notificationManager= (NotificationManager)MainActivity.this.getSystemService(MainActivity.this.NOTIFICATION_SERVICE);
        Intent intent2 = new Intent(MainActivity.this.getApplicationContext(),NotiActivity.class); //인텐트 생성.
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);//현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를 없앤다.

        SharedPreferences pref = getSharedPreferences("setting", MODE_PRIVATE);
        str_percent = pref.getString("percent", ""); // 키값으로 꺼냄 활동량 퍼센트 가져오기
        if(str_percent == null || str_percent.trim().equals("")){
            str_percent = "0";
        }
        float percent= Float.parseFloat(str_percent);//문자열을 Float로 변환

        if (pref.getBoolean("chk_1", false)) {//활동량 알람이 체그 되어있다면
            //s는 알람 한번만 울리게 하려고
            if (percent < 0.2) {
                S = 0;
            } else if (percent < 0.4 && percent >= 0.2 && S != 1) {
                PendingIntent pendingNotificationIntent = PendingIntent.getActivity( MainActivity.this,0, intent2,PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setSmallIcon(R.drawable.mainlogo).setTicker("똑띠알림").setWhen(System.currentTimeMillis())
                        .setNumber(1).setContentTitle("활동량").setContentText("목표 걸음수의 20%를 달성하였습니다.")
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);
                notificationManager.notify(1, builder.build());
                S = 1;
            } else if (percent < 0.6 && percent >= 0.4 && S != 2) {
                PendingIntent pendingNotificationIntent = PendingIntent.getActivity( MainActivity.this,0, intent2,PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setSmallIcon(R.drawable.mainlogo).setTicker("똑띠알림").setWhen(System.currentTimeMillis())
                        .setNumber(1).setContentTitle("활동량").setContentText("목표 걸음수의 40%를 달성하였습니다.")
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);
                notificationManager.notify(1, builder.build());
                S = 2;
            } else if (percent < 0.8 && percent >= 0.6 && S != 3) {
                PendingIntent pendingNotificationIntent = PendingIntent.getActivity( MainActivity.this,0, intent2,PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setSmallIcon(R.drawable.mainlogo).setTicker("똑띠알림").setWhen(System.currentTimeMillis())
                        .setNumber(1).setContentTitle("활동량").setContentText("목표 걸음수의 60%를 달성하였습니다.")
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);
                notificationManager.notify(1, builder.build());
                S = 3;
            } else if (percent < 1 && percent >= 0.8 && S != 4) {
                //&& S!=4){
                PendingIntent pendingNotificationIntent = PendingIntent.getActivity( MainActivity.this,0, intent2,PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setSmallIcon(R.drawable.mainlogo).setTicker("똑띠알림").setWhen(System.currentTimeMillis())
                        .setNumber(1).setContentTitle("활동량").setContentText("목표 걸음수의 80%를 달성하였습니다.")
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);
                notificationManager.notify(1, builder.build());
                S = 4;
            } else if (percent == 1 && S != 5) {
                PendingIntent pendingNotificationIntent = PendingIntent.getActivity( MainActivity.this,0, intent2,PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setSmallIcon(R.drawable.mainlogo).setTicker("똑띠알림").setWhen(System.currentTimeMillis())
                        .setNumber(1).setContentTitle("활동량").setContentText("목표 걸음수를 달성하였습니다.")
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);
                notificationManager.notify(1, builder.build());
                S = 5;// 한번만 울리고 알람 안울리게!
            }
        }
    }

    private void popupHeart() {

        NotificationManager notificationManager_H = (NotificationManager) MainActivity.this.getSystemService(MainActivity.this.NOTIFICATION_SERVICE);
        Intent intent_H = new Intent(MainActivity.this.getApplicationContext(), NotiActivity.class); //인텐트 생성.
        Notification.Builder builder_H = new Notification.Builder(getApplicationContext());
        intent_H.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);//현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를 없앤다.
        PendingIntent pendingNotificationIntent_H = PendingIntent.getActivity(MainActivity.this, 0, intent_H, PendingIntent.FLAG_UPDATE_CURRENT);

        SharedPreferences pref = getSharedPreferences("setting", MODE_PRIVATE);
        str_bpm = pref.getString("bpm", ""); // 키값으로 꺼냄 활동량 퍼센트 가져오기

        SharedPreferences pref2 = getSharedPreferences(sfName, MODE_PRIVATE);
        pref2.getString("min", "");
        str_min = pref2.getString("min", ""); // 키값으로 꺼냄
        if(str_min == null || str_min.trim().equals("")){
            str_min = "65";
        }

        pref2.getString("max", "");
        str_max = pref2.getString("max", ""); // 키값으로 꺼냄
        if(str_max == null || str_max.trim().equals("")){
            str_max = "100";
        }

        if (pref.getBoolean("chk_2", false)) {
            if (Integer.parseInt(str_bpm) < Integer.parseInt(str_min)) {
                long now = System.currentTimeMillis() / 1000;
                long how_pass_time = now - start2;
                if (how_pass_time >= 5) {//5초에 한번
                    builder_H.setSmallIcon(R.drawable.mainlogo).setTicker("똑띠알림").setWhen(System.currentTimeMillis())
                            //->알람시간
                            .setNumber(1).setContentTitle("심박수").setContentText("심박수가 낮습니다.")
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent_H).setAutoCancel(true).setOngoing(true);
                    notificationManager_H.notify(1, builder_H.build());
                    start = System.currentTimeMillis() / 1000;
                }
            } else if (Integer.parseInt(str_bpm) > Integer.parseInt(str_max)) {
                long now = System.currentTimeMillis() / 1000;
                long how_pass_time = now - start2;
                if (how_pass_time >= 5) {//5초에 한번*/
                    builder_H.setSmallIcon(R.drawable.mainlogo).setTicker("똑띠알림").setWhen(System.currentTimeMillis())
                            .setNumber(1).setContentTitle("심박수").setContentText("심박수가 높습니다.")
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent_H).setAutoCancel(true).setOngoing(true);
                    notificationManager_H.notify(1, builder_H.build());
                    start = System.currentTimeMillis() / 1000;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<PhoneBook> list = new Select()
                .from(PhoneBook.class)
                .orderBy("name ASC")
                .execute();
        phoneBookAdapter.removeAll();
        for(int i=0; i<list.size(); i++) {
            phoneBookAdapter.add(list.get(i));
        }
        phoneBookAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add :
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    OnInnerViewClickListener onCallClickListener = new OnInnerViewClickListener() {
        @Override
        public void onItemClick(String data) {
            if(ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                call(data);
            }else {
                if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.CALL_PHONE)) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            AndroidPermissions.CALL_PHONE.ordinal());
                }else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            AndroidPermissions.CALL_PHONE.ordinal());
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == AndroidPermissions.PERMISSION_READ_EXTERNAL_STORAGE.ordinal()) {
            if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }else {
                Snackbar.make(toolbar, "PERMISSION_READ_EXTERNAL_STORAGE 퍼미션이 거부되었습니다", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void call(String data) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + data));
            startActivity(intent);
        }catch(SecurityException e) {
            e.printStackTrace();
        }
    }

}
