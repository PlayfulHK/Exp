package com.example.exp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private MyDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new MyDbHelper(this, "diary.db", null, 1);
        dbHelper.getWritableDatabase();

        Button btnNewDiary = findViewById(R.id.new_diary);
        btnNewDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("author_data", MODE_PRIVATE);
                String defaultTitle = sharedPreferences.getString("default_title", "无标题日记");
                String authorName = sharedPreferences.getString("author_name", "佚名");
                dbHelper.createDiary(defaultTitle, authorName);
                Intent intent = new Intent(MainActivity.this, EditDiaryActivity.class);
                intent.putExtra("isNew", 1);
                startActivity(intent);
            }
        });

        ListView listView = findViewById(R.id.list_view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditDiaryActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(MainActivity.this);
//                deleteDialog.setMessage("确定删除？");
                deleteDialog.setTitle("确定删除这篇日记？");

                //添加AlertDialog.Builder对象的setPositiveButton()方法
                deleteDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cursor cursor = dbHelper.getDiaryListCursor();
                        cursor.moveToPosition(position);
                        int _id = cursor.getInt(cursor.getColumnIndex(MyDbHelper._ID));
                        dbHelper.deleteDiary(_id);
                        Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        refreshView();
                    }
                });

                //添加AlertDialog.Builder对象的setNegativeButton()方法
                deleteDialog.setNegativeButton("取消",null);
                deleteDialog.create().show();
                refreshView();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "编辑作者信息..");
        menu.add(0, 1, 0, "删除所有日记..");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent(MainActivity.this, AuthorInfoActivity.class);
                startActivity(intent);
                break;
            case 1:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setIcon(R.mipmap.ic_launcher_round);
                builder.setTitle("确定删除所有日记？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteAllDiary();
                        refreshView();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        refreshView();
        super.onResume();
    }

    public void refreshView() {
        SharedPreferences sharedPreferences = getSharedPreferences("author_data", MODE_PRIVATE);
        String author_name = sharedPreferences.getString("author_name", "我");
        setTitle(author_name + "的日记本");
        android.database.Cursor cursor = dbHelper.getDiaryListCursor();
        String[] from = new String[]{MyDbHelper.TITLE, MyDbHelper.CREATE_TIME, MyDbHelper.CONTENT};
        int[] to = new int[]{R.id.list_item_title, R.id.list_item_create_time, R.id.list_item_content};
        SimpleCursorAdapter.ViewBinder viewBinder = new SimpleCursorAdapter.ViewBinder() {

            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (cursor.getColumnIndex(MyDbHelper.TITLE) == columnIndex) {
                    String title = cursor.getString(columnIndex);
                    TextView tv = (TextView) view;
                    tv.setText(title);
                    tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    return true;
                }
                if (cursor.getColumnIndex(MyDbHelper.CREATE_TIME) == columnIndex) {
                    long create_time_long = cursor.getLong(columnIndex);
                    java.util.Date date = new Date(create_time_long);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat()
                    String create_time = simpleDateFormat.format(date);
                    TextView tv = (TextView) view;
                    tv.setText(create_time);
                    return true;
                }
                if (cursor.getColumnIndex(MyDbHelper.CONTENT) == columnIndex) {
                    String content = cursor.getString(columnIndex);
                    if (content.length() >= 15)
                        content = content.substring(0, 15) + "…";
                    content = content.replaceAll("[\r\n]", " ");
                    TextView tv = (TextView) view;
                    tv.setText(content);
                    return true;
                }
                return false;
            }
        };
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
                this, R.layout.diary_list_item, cursor, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        simpleCursorAdapter.setViewBinder(viewBinder);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(simpleCursorAdapter);
        TextView count_tv = findViewById(R.id.count);
        count_tv.setText("共" + cursor.getCount() + "篇日记");
    }

}