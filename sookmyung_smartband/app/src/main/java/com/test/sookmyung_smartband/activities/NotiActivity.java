package com.test.sookmyung_smartband.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.activeandroid.query.Select;
import com.test.sookmyung_smartband.R;
import com.test.sookmyung_smartband.adapter.PhoneBookAdapter;
import com.test.sookmyung_smartband.beans.PhoneBook;

import java.util.List;

public class NotiActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private PhoneBookAdapter phoneBookAdapter;
    String phone = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("알림");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar); //툴바를 액션바와 같게 만들어 준다.

        phoneBookAdapter = new PhoneBookAdapter(this);
        List<PhoneBook> list = new Select()
                .from(PhoneBook.class)
                .orderBy("name ASC")
                .execute();
        phoneBookAdapter.add(list.get(0));
        phone = phoneBookAdapter.getPhone(0);

        ImageButton button1 = (ImageButton)findViewById(R.id.imageButton1);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent1 = new Intent(NotiActivity.this, MainActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
            }
        });

        ImageButton button2 = (ImageButton)findViewById(R.id.imageButton2);
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent2 = new Intent(NotiActivity.this, HealthActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
            }
        });

        ImageButton button3 = (ImageButton)findViewById(R.id.imageButton3);
        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                try {
                    Intent intent3 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                    startActivity(intent3);
                }catch(SecurityException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
