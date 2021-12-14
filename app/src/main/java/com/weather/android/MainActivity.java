package com.weather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText editText1,editText2;
    private CheckBox checkBox;
    private Button button1,button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        checkBox = findViewById(R.id.checkBox);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        //保存用户密码
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        editText1.setText(sp.getString("username",""));
        editText2.setText(sp.getString("password",""));
        //获取checkBox的勾选状态
        checkBox.setChecked(sp.getBoolean("flag",false));
        //登录按钮
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editText1.getText().toString();
                String password = editText2.getText().toString();
                //判断用户是否输入了用户名、密码
                if (!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(password)){
                    MyHelper myHelper = new MyHelper(MainActivity.this,"information",null,1);
                    SQLiteDatabase db = myHelper.getReadableDatabase();
                    Cursor cursor1 = db.rawQuery("select * from user where username = ?",new String[]{username});
                    //判断此用户是否存在
                    if (cursor1.moveToNext()){
                        Cursor cursor2 = db.rawQuery("select password from user where username = ?",new String[]{username});
                        cursor2.moveToNext();
                        //判断用户输入的密码是否正确
                        if (cursor2.getString(0).equals(password)){
                            //是否记住密码
                            if (checkBox.isChecked()){
                                editor.putString("username",username);
                                editor.putString("password",password);
                                //保存checkBox的勾选状态
                                editor.putBoolean("flag",checkBox.isChecked());
                                editor.commit();
                            }else {
                                editor.clear();
                                editor.commit();
                            }
                            Toast.makeText(MainActivity.this,"成功登录!",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, UserManageActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(MainActivity.this,"密码错误!",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(MainActivity.this,"抱歉，此用户不存在!",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this,"用户名、密码都不能为空!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //点击注册按钮，跳转到注册界面
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, RegisterActivity.class);
                startActivityForResult(intent,100);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            //填充用户名和密码
            editText1.setText(data.getExtras().get("username")+"");
            editText2.setText(data.getExtras().get("password")+"");
        }
    }
}
