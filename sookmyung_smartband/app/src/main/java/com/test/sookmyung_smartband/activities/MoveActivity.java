package com.test.sookmyung_smartband.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.test.sookmyung_smartband.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MoveActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    TextView txt_step;
    TextView selectedText;
    String[] item;
    phpDown task;
    JSONArray result = null;
    ImageView img_st1;
    //Drawable img_st1, img_st2, img_st3, img_st4;
    String str_walk;
    String sfName = "myFile";
    TextView minWalk;

    ProgressBar progressBar1;
    int progress=0;
    int maxvalue=0;

    long start=0;
    long start_Move=0;
    int S;

    NotificationManager notificationManager;
    PendingIntent pendingNotificationIntent;
    Notification.Builder builder;

    private String step,state;

    protected void onCreate(Bundle savedInstanceState) {
            /*    notificationManager= (NotificationManager)MoveActivity.this.getSystemService(MoveActivity.this.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(MoveActivity.this.getApplicationContext(),MainActivity.class); //인텐트 생성.
        builder = new Notification.Builder(getApplicationContext());
*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("활동");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = new Intent(this.getIntent());

        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);

        minWalk = (TextView) findViewById(R.id.minWalk);

        SharedPreferences pref = getSharedPreferences(sfName, MODE_PRIVATE);
        pref.getString("walk","");
        str_walk = pref.getString("walk", ""); // 키값으로 꺼냄
        if(str_walk == null || str_walk.trim().equals("")){
            str_walk = "2000";
        }
        minWalk.setText("목표 걸음수: " + str_walk + " 걸음"); // EditText에 반영함


        maxvalue = Integer.parseInt(str_walk);
        progressBar1.setMax(maxvalue);

        txt_step = (TextView) findViewById(R.id.step);
        img_st1 = (ImageView)findViewById(R.id.st1);
        //img_st2 = ((ImageView)findViewById(R.id.st2)).getDrawable();
        //img_st3 = ((ImageView)findViewById(R.id.st3)).getDrawable();
        //img_st4 = ((ImageView)findViewById(R.id.st4)).getDrawable();
        //img_st1.setAlpha(30);
        //img_st2.setAlpha(30);
        //img_st3.setAlpha(30);
        //img_st4.setAlpha(30);

        task = new phpDown();

        TimerTask myTask = new TimerTask() {
            public void run() {
                task = new phpDown();
                task.execute("http://203.153.148.204/stepstate_show.php");
            }
        };
        Timer timer = new Timer();
        timer.schedule(myTask, 0, 3000);

        notificationManager= (NotificationManager)MoveActivity.this.getSystemService(MoveActivity.this.NOTIFICATION_SERVICE);
        Intent intent2 = new Intent(MoveActivity.this.getApplicationContext(),MainActivity.class); //인텐트 생성.
        //푸쉬를 눌렀을때 메인 화면으로 이동 됨
        builder = new Notification.Builder(getApplicationContext());
        intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);//현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를 없앤다.
        pendingNotificationIntent = PendingIntent.getActivity( MoveActivity.this,0, intent2,PendingIntent.FLAG_UPDATE_CURRENT);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?>adapterView,View view,int i, long l){
        selectedText.setText(item[i]);
        if(selectedText.getText().toString().equals("선택하세요")){
            selectedText.setText("");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView){
                 selectedText.setText("");
             }

    private class phpDown extends AsyncTask<String, Integer,String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try{
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                // 연결되었으면.
                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for(;;){
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if(line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch(Exception ex){
                ex.printStackTrace();
            }
            return jsonHtml.toString();

        }

        protected void onPostExecute(String str){
            //img_st1.setAlpha(30);
            //img_st2.setAlpha(30);
            //img_st3.setAlpha(30);
            //img_st4.setAlpha(30);
            try {
                JSONObject jsonObj = new JSONObject(str);
                result = jsonObj.getJSONArray("result");
                String step = result.getJSONObject(0).getString("step");
                txt_step.setText("현재 걸음수: " + step + " 걸음");
                String state = result.getJSONObject(0).getString("state");

                SharedPreferences pref = getSharedPreferences("setting", MODE_PRIVATE);
                SharedPreferences sf = getSharedPreferences(sfName, 0);

                SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
                String goal_step = sf.getString("walk", ""); // 키값으로 꺼냄
                if(goal_step == null || goal_step.trim().equals("")){
                    goal_step = "2000";
                }

                float Goal_step= Float.parseFloat(goal_step);//문자열을 Float로 변환

                float Step= Float.parseFloat(step);

                float percent= Step/Goal_step;

                String percent_s= String.valueOf(percent);
                editor.putString("percent", percent_s); // 입력
                editor.commit();

                progress = Integer.parseInt(step);
                progressBar1.setProgress(progress);

                int st =  Integer.parseInt(state);
                if(st == 0){
                    /*img_st1.setAlpha(255);
                    img_st2.setAlpha(30);
                    img_st3.setAlpha(30);
                    img_st4.setAlpha(30);*/
                    img_st1.setImageResource(R.drawable.a1);
                }
                else if(st == 1){
                    /*img_st2.setAlpha(255);
                    img_st1.setAlpha(30);
                    img_st3.setAlpha(30);
                    img_st4.setAlpha(30);*/
                    img_st1.setImageResource(R.drawable.a2);
                    if(pref.getBoolean("chk_4", false)) {
                        long now = System.currentTimeMillis() / 1000;
                        long how_pass_time = now - start;
                        if (how_pass_time >= 3) {//1초에 한번
                            builder.setSmallIcon(R.drawable.mainlogo).setTicker("똑띠알림").setWhen(System.currentTimeMillis())
                                    .setNumber(1).setContentTitle("낙상").setContentText("주의 단계입니다.")
                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);
                            notificationManager.notify(1, builder.build());
                            start = System.currentTimeMillis() / 1000;
                        }
                    }
                }
                else if(st == 2){
                    /*img_st3.setAlpha(255);
                    img_st1.setAlpha(30);
                    img_st2.setAlpha(30);
                    img_st4.setAlpha(30);*/
                    img_st1.setImageResource(R.drawable.a3);
                    if(pref.getBoolean("chk_5", false)) {
                        long now = System.currentTimeMillis()/1000;
                        long how_pass_time= now-start;
                        if (how_pass_time>=3) {//1초에 한번
                            builder.setSmallIcon(R.drawable.mainlogo).setTicker("똑띠알림").setWhen(System.currentTimeMillis())
                                    .setNumber(1).setContentTitle("낙상").setContentText("낙상 단계입니다.")
                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);
                            notificationManager.notify(1, builder.build());
                            start = System.currentTimeMillis()/1000;
                        }
                    }
                }
                else {
                    /*img_st4.setAlpha(255);
                    img_st1.setAlpha(30);
                    img_st2.setAlpha(30);
                    img_st3.setAlpha(30);*/
                    img_st1.setImageResource(R.drawable.a4);
                    if(pref.getBoolean("chk_6", false)) {
                        long now = System.currentTimeMillis()/1000;
                        long how_pass_time= now-start;
                        if (how_pass_time>=3) {//1초에 한번
                            builder.setSmallIcon(R.drawable.mainlogo).setTicker("똑띠알림").setWhen(System.currentTimeMillis())
                                    .setNumber(1).setContentTitle("낙상").setContentText("위험 단계입니다.")
                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);
                            notificationManager.notify(1, builder.build());
                            start = System.currentTimeMillis()/1000;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
