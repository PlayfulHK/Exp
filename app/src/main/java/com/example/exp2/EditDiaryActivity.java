
package com.example.exp2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditDiaryActivity extends AppCompatActivity {
    private MyDbHelper dbHelper;
    private int _id;
    private Intent intent;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private ImageView imageView;
    private String title;
    private String photo_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);
        intent = getIntent();
        dbHelper = new MyDbHelper(this, "diary.db", null, 1);
        imageView = findViewById(R.id.picture);
    }

    @Override
    protected void onResume() {
        display();
        super.onResume();
    }

    @Override
    protected void onPause() {
        updateDiary();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "修改标题");
        menu.add(0, TAKE_PHOTO, 0, "拍照");
        menu.add(0, CHOOSE_PHOTO, 0, "从相册选择");
        menu.add(0, 3, 0, "删除图片");
        menu.add(0, 4, 0, "删除日记");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                final EditText editText = new EditText(this);
                editText.setText(getTitle());
                editText.setTextSize(24);
                editText.setMaxWidth(40);
                editText.setSingleLine(true);
                AlertDialog.Builder editTitleDialog = new AlertDialog.Builder(this);
//                editTitleDialog.setIcon(R.mipmap.ic_launcher_round);
                editTitleDialog.setTitle("修改标题").setView(editText);
                editTitleDialog.setPositiveButton("完成", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        title = editText.getText().toString();
                        updateDiary();
                        display();
                        Toast.makeText(EditDiaryActivity.this, "修改标题成功", Toast.LENGTH_SHORT).show();
                    }
                });
                editTitleDialog.show();
                break;
            case TAKE_PHOTO:
                takePhoto();
                break;
            case CHOOSE_PHOTO:
                choosePhoto();
                break;
            case 3:
                photo_path = "";
                updateDiary();
//                display();
                displayImage();
                break;
            case 4:
                dbHelper.deleteDiary(_id);
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void choosePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
//        Intent intent = new Intent("android.intent.action.GET_CONTENT");
//        intent.setType("image/*");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    private void display() {
        Cursor cursor = dbHelper.getDiaryListCursor();
        if (intent.getIntExtra("isNew", 0) == 1)
            cursor.moveToLast();
        else {
            cursor.moveToPosition(intent.getIntExtra("position", 0));
        }
        _id = cursor.getInt(cursor.getColumnIndex(MyDbHelper._ID));
        title = cursor.getString(cursor.getColumnIndex(MyDbHelper.TITLE));
        setTitle(title);

        String author = cursor.getString(cursor.getColumnIndex(MyDbHelper.AUTHOR));
        TextView tv_author = findViewById(R.id.author);
        tv_author.setText(author);

        long create_time_long = cursor.getLong(cursor.getColumnIndex(MyDbHelper.CREATE_TIME));
        java.util.Date date = new Date(create_time_long);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String create_time = simpleDateFormat.format(date);
        TextView tv_create_time = findViewById(R.id.create_time);
        tv_create_time.setText(create_time);

        String content = cursor.getString(cursor.getColumnIndex(MyDbHelper.CONTENT));
        EditText et_content = findViewById(R.id.diary_content);
        et_content.setText(content);

        photo_path = cursor.getString(cursor.getColumnIndex(MyDbHelper.PHOTO_PATH));
        if (photo_path != null && !photo_path.equals(""))
            imageView.setImageURI(Uri.fromFile(new File(photo_path)));
    }

    private void updateDiary() {
//        int _id = cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper._ID));
        EditText et_content = findViewById(R.id.diary_content);
        String content = et_content.getText().toString();
        dbHelper.updateDiary(_id, title, content, photo_path);
    }


    private void takePhoto() {
//        File outputImage = new File(getExternalCacheDir(), _id + "_image.jpg");
        File outputImage = new File(getExternalFilesDir(null), _id + "_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        uri = Uri.fromFile(outputImage);
        Uri uri = FileProvider.getUriForFile(this, "com.example.exp2.fileProvider", outputImage);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        Intent intent=new Intent(Intent.ACTION_SEND);


        photo_path = outputImage.getPath();
//        photo_path = getImagePath(uri, null);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void getPathFromUriFromIntent(Intent data) {
        Uri uri = data.getData();
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            photo_path = getImagePath(uri);
        } else
            photo_path = uri.getPath();
    }

    private void displayImage() {
        if (photo_path != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(photo_path);
            updateDiary();
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    private String getImagePath(Uri uri) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri,
                null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum();
            } else {
                Toast.makeText(this, "You denied the permission!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    displayImage();
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    getPathFromUriFromIntent(data);
                    displayImage();
                }
            default:
                break;
        }
    }
}