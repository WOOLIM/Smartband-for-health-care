package com.test.sookmyung_smartband.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;

import com.test.sookmyung_smartband.R;



public class HealthActivity extends AppCompatActivity {

    private Toolbar toolbar;
    public TextView textView1;
    public TextView textView2;

    EditText et1;
    EditText et2;
    EditText et3;
    EditText et4;
    String sfName = "myFile";

    SharedPreferences setting;
    SharedPreferences.Editor editor_0;

    CheckBox check3;
    CheckBox check1;
    CheckBox check2;
    CheckBox check4;
    CheckBox check5;
    CheckBox check6;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("알림설정");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar); //툴바를 액션바와 같게 만들어 준다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = new Intent(HealthActivity.this, MainActivity.class);

        setting = getSharedPreferences("setting", 0);
        editor_0=setting.edit();


        Button bt1 = (Button) findViewById(R.id.button1);

        et1 = (EditText) findViewById(R.id.min_heartbeat);
        et2 = (EditText) findViewById(R.id.max_heartbeat);
        et3 = (EditText) findViewById(R.id.leastdis);
        et4 = (EditText) findViewById(R.id.leastwalk);
        check3= (CheckBox) findViewById(R.id.checkBox3);
        if(setting.getBoolean("chk_3", false)){
            //chk_3을 가져와라 없다면 false를 디폴트로
            //"chk_3"가 체크되어있다면 true=>if문 안으로 들어옴
            check3.setChecked(true);
        }
        check1= (CheckBox) findViewById(R.id.checkBox1);
        if(setting.getBoolean("chk_1", false)){
            check1.setChecked(true);
        }
        check2= (CheckBox) findViewById(R.id.checkBox2);
        if(setting.getBoolean("chk_2", false)){
            check2.setChecked(true);
        }
        check4= (CheckBox) findViewById(R.id.checkBox4);
        if(setting.getBoolean("chk_4", false)){
            check4.setChecked(true);
        }
        check5= (CheckBox) findViewById(R.id.checkBox5);
        if(setting.getBoolean("chk_5", false)){
            check5.setChecked(true);
        }
        check6= (CheckBox) findViewById(R.id.checkBox6);
        if(setting.getBoolean("chk_6", false)){
            check6.setChecked(true);
        }


        // 지난번 저장해놨던 사용자 입력값을 꺼내서 보여주기
        SharedPreferences sf = getSharedPreferences(sfName, 0);
        String str1 = sf.getString("min", ""); // 키값으로 꺼냄
        et1.setText(str1); // EditText에 반영함

        String str2 = sf.getString("max", ""); // 키값으로 꺼냄
        et2.setText(str2); // EditText에 반영함

        String str3 = sf.getString("dis", ""); // 키값으로 꺼냄
        et3.setText(str3); // EditText에 반영함

        String str4 = sf.getString("walk", ""); // 키값으로 꺼냄
        et4.setText(str4); // EditText에 반영함

        bt1.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0){

                EditText ed1 = (EditText)findViewById(R.id.min_heartbeat);

                String str1;
                str1 = ed1.getText().toString();

                EditText ed2 = (EditText)findViewById(R.id.max_heartbeat);

                String str2;
                str2 = ed2.getText().toString();

                EditText ed3 = (EditText)findViewById(R.id.leastdis);

                String str3;
                str3 = ed3.getText().toString();

                EditText ed4 = (EditText)findViewById(R.id.leastwalk);

                String str4;
                str4 = ed4.getText().toString();

                CheckBox check_3=(CheckBox)findViewById(R.id.checkBox3);
                CheckBox check_1=(CheckBox)findViewById(R.id.checkBox1);
                CheckBox check_2=(CheckBox)findViewById(R.id.checkBox2);
                CheckBox check_4=(CheckBox)findViewById(R.id.checkBox4);
                CheckBox check_5=(CheckBox)findViewById(R.id.checkBox5);
                CheckBox check_6=(CheckBox)findViewById(R.id.checkBox6);
                //지금 체크박스 상태 불러옴
                Boolean chk3 =check_3.isChecked();
                Boolean chk1 =check_1.isChecked();
                Boolean chk2 =check_2.isChecked();
                Boolean chk4 =check_4.isChecked();
                Boolean chk5 =check_5.isChecked();
                Boolean chk6 =check_6.isChecked();
                if(chk3 ==true){
                    editor_0.putBoolean("chk_3", true);
                    editor_0.commit();
                }else{
                    editor_0.putBoolean("chk_3", false);
                    editor_0.commit();
                }
                if(chk1 ==true){
                    editor_0.putBoolean("chk_1", true);
                    editor_0.commit();
                }else{
                    editor_0.putBoolean("chk_1", false);
                    editor_0.commit();
                }
                if(chk2 ==true){
                    editor_0.putBoolean("chk_2", true);
                    editor_0.commit();
                }else{
                    editor_0.putBoolean("chk_2", false);
                    editor_0.commit();
                }
                if(chk4 ==true){
                    editor_0.putBoolean("chk_4", true);
                    editor_0.commit();
                }else{
                    editor_0.putBoolean("chk_4", false);
                    editor_0.commit();
                }
                if(chk5 ==true){
                    editor_0.putBoolean("chk_5", true);
                    editor_0.commit();
                }else{
                    editor_0.putBoolean("chk_5", false);
                    editor_0.commit();
                }
                if(chk6 ==true){
                    editor_0.putBoolean("chk_6", true);
                    editor_0.commit();
                }else{
                    editor_0.putBoolean("chk_6", false);
                    editor_0.commit();
                }

                //체크가 되어있나확인


                Toast.makeText(getApplicationContext(), "저장이 완료되었습니다.", Toast.LENGTH_LONG).show();
            }

        });
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
    protected void onStop() {
        super.onStop();
        // Activity 가 종료되기 전에 저장한다
        // SharedPreferences 에 설정값(특별히 기억해야할 사용자 값)을 저장하기

        SharedPreferences pref = getSharedPreferences(sfName, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();//저장하려면 editor가 필요

        String str1 = et1.getText().toString(); // 사용자가 입력한 값
        editor.putString("min", str1); // 입력

        String str2 = et2.getText().toString(); // 사용자가 입력한 값
        editor.putString("max", str2); // 입력

        String str3 = et3.getText().toString(); // 사용자가 입력한 값
        editor.putString("dis", str3); // 입력

        String str4 = et4.getText().toString(); // 사용자가 입력한 값
        editor.putString("walk", str4); // 입력

        editor.commit(); // 파일에 최종 반영함
    }


}

