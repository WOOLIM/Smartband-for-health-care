package com.test.sookmyung_smartband.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.test.sookmyung_smartband.R;
import com.test.sookmyung_smartband.beans.AndroidPermissions;
import com.test.sookmyung_smartband.beans.PhoneBook;

import org.bson.types.ObjectId;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;

public class AddActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button okBtn;

    private EditText etName;
    private EditText etPhoneNumber1;
    private EditText etPhoneNumber2;
    private EditText etPhoneNumber3;
    private String gender;
    private String year;
    private String month;
    private String day;
    private EditText etAddress;
    private Spinner genderSpinner;
    private Spinner yearSpinner;
    private Spinner monthSpinner;
    private Spinner daySpinner;

    private Button btnSelectPic;
    private ImageView ivUserPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("계정추가");

        okBtn = (Button) findViewById(R.id.ok_button);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePhoneBook();
            }
        });

        etName = (EditText)findViewById(R.id.etName);
        etPhoneNumber1 = (EditText)findViewById(R.id.etPhoneNumber1);
        etPhoneNumber2 = (EditText)findViewById(R.id.etPhoneNumber2);
        etPhoneNumber3 = (EditText)findViewById(R.id.etPhoneNumber3);
        etAddress = (EditText)findViewById(R.id.etAddress);

        // Spinner
        genderSpinner = (Spinner)findViewById(R.id.spinner_gender);
        ArrayAdapter genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        yearSpinner = (Spinner)findViewById(R.id.spinner_year);
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this, R.array.date_year, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        monthSpinner = (Spinner)findViewById(R.id.spinner_month);
        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(this, R.array.date_month, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        daySpinner = (Spinner)findViewById(R.id.spinner_day);
        ArrayAdapter dayAdapter = ArrayAdapter.createFromResource(this, R.array.date_day, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);

        //event listener
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = (String)genderSpinner.getAdapter().getItem(genderSpinner.getSelectedItemPosition());
                ((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = (String)yearSpinner.getAdapter().getItem(yearSpinner.getSelectedItemPosition()) + " ";
                ((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month = (String)monthSpinner.getAdapter().getItem(monthSpinner.getSelectedItemPosition()) + " ";
                ((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                day = (String)daySpinner.getAdapter().getItem(daySpinner.getSelectedItemPosition());
                ((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        btnSelectPic = (Button)findViewById(R.id.btnSelectPic);
        btnSelectPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(AddActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        goPhotoPickerActivity();
                }else {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(AddActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(AddActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                AndroidPermissions.PERMISSION_READ_EXTERNAL_STORAGE.ordinal());
                    }else {
                        ActivityCompat.requestPermissions(AddActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                AndroidPermissions.PERMISSION_READ_EXTERNAL_STORAGE.ordinal());
                    }
                }
            }
        });

        ivUserPic = (ImageView)findViewById(R.id.ivUserPic);
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

    private void savePhoneBook() {

        String name = etName.getEditableText().toString().trim();
        String email = gender;
        String phoneNumber = etPhoneNumber1.getEditableText().toString().trim()+etPhoneNumber2.getEditableText().toString().trim()+etPhoneNumber3.getEditableText().toString().trim();
        String homeNumber = year + month + day;
        String address = etAddress.getEditableText().toString().trim();

        if(name.isEmpty()) {
            Toast.makeText(AddActivity.this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(phoneNumber.isEmpty()) {
            Toast.makeText(AddActivity.this, "핸드폰번호를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(homeNumber.isEmpty()) {
            Toast.makeText(AddActivity.this, "생년월일을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(address.isEmpty()) {
            Toast.makeText(AddActivity.this, "주소를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(email.isEmpty()) {
            Toast.makeText(AddActivity.this, "성별을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneBook phoneBook = new PhoneBook();
        phoneBook.setCode(ObjectId.get().toString());
        phoneBook.setEmail(email);
        phoneBook.setPhoneNumber(phoneNumber);
        phoneBook.setHomeNumber(homeNumber);
        phoneBook.setAddress(address);
        phoneBook.setName(name);

        if(ivUserPic.getTag() != null) {
            phoneBook.setUserPic((String)ivUserPic.getTag());
        }

        phoneBook.save();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == AndroidPermissions.PERMISSION_READ_EXTERNAL_STORAGE.ordinal()) {
            if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goPhotoPickerActivity();
            }else {
                Snackbar.make(toolbar, "PERMISSION_READ_EXTERNAL_STORAGE 퍼미션이 거부되었습니다", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void goPhotoPickerActivity() {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(true)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);

                if(photos.size() > 0) {
                    Glide.with(AddActivity.this)
                            .load(new File(photos.get(0)))
                            .centerCrop()
                            .thumbnail(0.1f)
                            .into(ivUserPic);

                    ivUserPic.setTag(photos.get(0));
                }
            }
        }
    }
}
