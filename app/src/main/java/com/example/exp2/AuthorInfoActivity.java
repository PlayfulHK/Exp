package com.example.exp2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AuthorInfoActivity extends AppCompatActivity {

    private String author_name;
    private String sexual;
    private String intro;
    private String default_title;
    private EditText author_name_et;
    private RadioGroup sexual_rg;
    private EditText intro_et;
    private EditText default_title_et;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_info);
        setTitle("编辑作者信息");
        author_name_et = findViewById(R.id.author_name);
        sexual_rg = findViewById(R.id.sexual_radio);
        intro_et = findViewById(R.id.intro);
        default_title_et = findViewById(R.id.default_title);
        sharedPreferences = getSharedPreferences("author_data", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        author_name_et.setSingleLine(true);
        default_title_et.setSingleLine(true);
        showAuthorInfo();
    }

    @Override
    public void onBackPressed() {
        saveAuthorInfo();
        super.onBackPressed();
    }

    private void saveAuthorInfo() {
        author_name = author_name_et.getText().toString();
        int checkedSexualID = sexual_rg.getCheckedRadioButtonId();
        sexual = ((RadioButton) findViewById(checkedSexualID)).getText().toString();
        intro = intro_et.getText().toString();
        default_title = default_title_et.getText().toString();

        editor.putString("author_name", author_name);
        editor.putString("sexual", sexual);
        editor.putString("intro", intro);
        editor.putString("default_title", default_title);
        editor.apply();
    }

    private void showAuthorInfo() {
        author_name = sharedPreferences.getString("author_name", "佚名");
        sexual = sharedPreferences.getString("sexual", "男");
        intro = sharedPreferences.getString("intro", "无介绍");
        default_title = sharedPreferences.getString("default_title", "无标题日记");
        author_name_et.setText(author_name);
        RadioButton male_rb = findViewById(R.id.male);
        RadioButton female_rb = findViewById(R.id.female);
        if (sexual.equals("男")) {
            male_rb.setChecked(true);
        } else {
            female_rb.setChecked(true);
        }
        intro_et.setText(intro);
        default_title_et.setText(default_title);
    }
}