package com.weather.android;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText editText1,editText2,editText3;
    private Button button1,button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        //返回登陆页面
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        //注册
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户名
                String username = editText1.getText().toString();
                //密码
                String password = editText2.getText().toString();
                //确认密码
                String rePassword = editText3.getText().toString();
                //判断用户名、密码、确认密码是否为空
                if(!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(rePassword)) {
                    MyHelper myHelper = new MyHelper(RegisterActivity.this, "information", null, 1);
                    SQLiteDatabase db = myHelper.getReadableDatabase();
                    Cursor cursor1 = db.rawQuery("select * from user where username = ?",new String[]{username});
                    //判断用户名是否被注册
                    if (cursor1.moveToNext()){
                        Toast.makeText(RegisterActivity.this,"此用户名已被注册，换一个试试!",Toast.LENGTH_SHORT).show();
                    }else {
                        //判断两次输入密码是否一致
                        if (password.equals(rePassword)){
                            db.execSQL("insert into user(username,password) values(?,?)",new String[]{username,password});
                            Cursor cursor2 = db.rawQuery("select * from user where username = ? and password = ?",new String[]{username,password});
                            //判断是否注册成功
                            if (cursor2.moveToNext()){
                                Toast.makeText(RegisterActivity.this,"注册成功!",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.putExtra("username",username);
                                intent.putExtra("password",password);
                                setResult(RESULT_OK,intent);
                                finish();
                            }else {
                                Toast.makeText(RegisterActivity.this,"注册失败!",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(RegisterActivity.this,"确认密码与密码不相同！",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(RegisterActivity.this,"用户名、密码、确认密码都不能为空!",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
