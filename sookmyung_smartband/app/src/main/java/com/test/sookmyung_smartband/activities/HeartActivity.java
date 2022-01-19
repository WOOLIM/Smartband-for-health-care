package com.test.sookmyung_smartband.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.test.sookmyung_smartband.R;


public class HeartActivity extends AppCompatActivity {

    private Toolbar toolbar;
    TextView text_min;
    TextView text_max;
    TextView text_state;
    String sfName = "myFile";
    TextView txtView;
    //back task;
    String str_min, str_max;
    phpDown task;
    private String bpm = "0";

    NotificationManager notificationManager;
    PendingIntent pendingNotificationIntent;
    Notification.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("심박수");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar); //툴바를 액션바와 같게 만들어 준다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = new Intent(this.getIntent());

        text_min = (TextView) findViewById(R.id.textView1);
        text_max = (TextView) findViewById(R.id.textView2);
        txtView = (TextView)findViewById(R.id.txtView);
        text_state = (TextView) findViewById(R.id.textView3);

        SharedPreferences pref = getSharedPreferences(sfName, MODE_PRIVATE);
        pref.getString("min","");
        str_min = pref.getString("min", ""); // 키값으로 꺼냄
        if(str_min == null || str_min.trim().equals("")){
            str_min = "65";
        }
        text_min.setText(str_min); // EditText에 반영함

        pref.getString("max","");
        str_max = pref.getString("max", ""); // 키값으로 꺼냄
        if(str_max == null || str_max.trim().equals("")){
            str_max = "100";
        }
        text_max.setText(str_max); // EditText에 반영함

        task = new phpDown();

        TimerTask myTask = new TimerTask() {
            public void run() {
                task = new phpDown();
                task.execute("http://203.153.148.204/bpm_show.php");
            }
        };
        Timer timer = new Timer();
        timer.schedule(myTask, 0, 1000);
        notificationManager= (NotificationManager)HeartActivity.this.getSystemService(HeartActivity.this.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(HeartActivity.this.getApplicationContext(),MainActivity.class); //인텐트 생성.
        builder = new Notification.Builder(getApplicationContext());
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);//현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를 없앤다.
        pendingNotificationIntent = PendingIntent.getActivity( HeartActivity.this,0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);

    }

    //나가기 버튼
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class phpDown extends AsyncTask<String, Integer,String>{

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
            try {
                JSONObject jsonObj = new JSONObject(str);
                bpm = jsonObj.getString("heartbeat");
                txtView.setText(bpm);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SharedPreferences pref = getSharedPreferences("setting", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요
            String str1 =  bpm;// 사용자가 입력한 값
            editor.putString("bpm", str1);
            editor.commit();
            if(Integer.parseInt(bpm) < Integer.parseInt(str_min)){
                text_state.setText("심박수가 낮습니다.");
            }
            else if(Integer.parseInt(bpm) > Integer.parseInt(str_max)){
                text_state.setText("심박수가 높습니다.");
            }
            else{
                text_state.setText("정상 심박수 입니다.");
            }
        }

    }

}

