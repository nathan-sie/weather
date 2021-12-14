package com.weather.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class UserManageActivity extends AppCompatActivity {
    private EditText editText;
    private TextView textView;
    private Button button1,button2;
    private ListView listView;
    private ArrayAdapter arrayAdapter;
    private List<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manage);
        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        listView = findViewById(R.id.listView);
        list = new ArrayList<>();
        //添加按钮
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = editText.getText().toString();
                //判断文本框内的输入内容是否为空
                if (!TextUtils.isEmpty(username)){
                    MyHelper myHelper = new MyHelper(UserManageActivity.this,"information",null,1);
                    final SQLiteDatabase db = myHelper.getReadableDatabase();
                    final Cursor cursor1 = db.rawQuery("select * from user where username = ?",new String[]{username});
                    //判断用户是否存在，若不存在，弹出对话框，添加用户信息
                    if (cursor1.moveToNext()){
                        Toast.makeText(UserManageActivity.this,"此用户已存在!",Toast.LENGTH_SHORT).show();
                    }else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(UserManageActivity.this);
                        //为对话框设置自定义的布局
                        final View view = View.inflate(UserManageActivity.this,R.layout.edit_user,null);
                        builder.setTitle("添加用户信息");
                        builder.setView(view);
                        TextView textView = view.findViewById(R.id.textView);
                        final EditText editText1 = view.findViewById(R.id.editText1);
                        final EditText editText2 = view.findViewById(R.id.editText2);
                        final EditText editText3 = view.findViewById(R.id.editText3);
                        final EditText editText4 = view.findViewById(R.id.editText4);
                        //用户名设置为编辑框输入的内容
                        textView.setText(username);
                        builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String password = editText1.getText().toString();
                                        String phone = editText2.getText().toString();
                                        String address = editText3.getText().toString();
                                        String sex = editText4.getText().toString();
                                        //判断密码是否为空，电话、地址可以为空。
                                        if (!TextUtils.isEmpty(password)){
                                            db.execSQL("insert into user values(null,?,?,?,?,?)",new String[]{username,password,phone,address,sex});
                                            Cursor cursor2 = db.rawQuery("select * from user where username = ?",new String[]{username});
                                            //判断是否添加成功
                                            if (cursor2.moveToNext()){
                                                Toast.makeText(UserManageActivity.this,"用户添加成功！",Toast.LENGTH_SHORT).show();
                                                closeDialog(dialog,true);
                                            }else {
                                                Toast.makeText(UserManageActivity.this,"用户添加失败！",Toast.LENGTH_SHORT).show();
                                                closeDialog(dialog,false);
                                            }
                                        }else {
                                            Toast.makeText(UserManageActivity.this,"密码不能为空!",Toast.LENGTH_SHORT).show();
                                            closeDialog(dialog,false);
                                        }
                                    }
                                });
                        builder.setNegativeButton("取消",null);
                        //显示对话框
                        builder.create().show();
                    }
                }else {
                    Toast.makeText(UserManageActivity.this,"输入内容不能为空!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //查询按钮
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //每次进行新的查询时，要将list的内容先清空
                list.clear();
                String text = editText.getText().toString();
                //判断文本框内的输入内容是否为空
                if (!TextUtils.isEmpty(text)){
                    MyHelper myHelper = new MyHelper(UserManageActivity.this,"information",null,1);
                    SQLiteDatabase db = myHelper.getReadableDatabase();
                    //模糊查询
                    Cursor cursor = db.rawQuery("select * from user where username like ?",new String[]{"%"+text+"%"});
                    //将查询到的用户名添加到list中
                    while (cursor.moveToNext()){
                        list.add(cursor.getString(1));
                    }
                    //新建适配器
                    arrayAdapter = new ArrayAdapter(UserManageActivity.this,android.R.layout.simple_list_item_1,list);
                    //刷新
                    arrayAdapter.notifyDataSetChanged();
                    listView.setAdapter(arrayAdapter);
                    //当listView的内容为空时，显示提示信息
                    listView.setEmptyView(textView);
                }else {
                    Toast.makeText(UserManageActivity.this,"输入内容不能为空!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //点击listView的每一项时,弹出编辑用户信息的对话框
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //得到被点击的用户名
                final String username = list.get(position);
                MyHelper myHelper = new MyHelper(UserManageActivity.this,"information",null,1);
                final SQLiteDatabase db = myHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from user where username = ?",new String[]{username});
                final AlertDialog.Builder builder = new AlertDialog.Builder(UserManageActivity.this);
                //为对话框设置自定义的布局
                final View view = View.inflate(UserManageActivity.this,R.layout.edit_user,null);
                builder.setTitle("编辑用户信息");
                builder.setView(view);
                TextView textView = view.findViewById(R.id.textView);
                final EditText editText1 = view.findViewById(R.id.editText1);
                final EditText editText2 = view.findViewById(R.id.editText2);
                final EditText editText3 = view.findViewById(R.id.editText3);
                final EditText editText4 = view.findViewById(R.id.editText4);
                textView.setText(username);
                //将数据库查询到的用户信息，填充在文本框里
                while (cursor.moveToNext()){
                        editText1.setText(cursor.getString(2));
                        editText2.setText(cursor.getString(3));
                        editText3.setText(cursor.getString(5));
                        editText4.setText(cursor.getString(4));
                }
                //点击确定更新数据
                builder.setPositiveButton("确定", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = editText1.getText().toString();
                        String phone = editText2.getText().toString();
                        String address = editText3.getText().toString();
                        String sex = editText4.getText().toString();
                        //判断密码是否为空,地址、电话可以为空
                        if(!TextUtils.isEmpty(password)){
                            //更新数据库
                            db.execSQL("update user set password = ?, phone = ?, address = ?,sex = ? where username = ?",new String[]{password,phone,address,sex,username});
                            closeDialog(dialog,true);
                        }else {
                            Toast.makeText(UserManageActivity.this, "密码不能为空!", Toast.LENGTH_SHORT).show();
                            closeDialog(dialog,false);
                        }
                    }
                });
                builder.setNegativeButton("取消",null);
                //显示对话框
                builder.create().show();
            }
        });
        //长点击listView的每一项时,弹出删除用户的对话框
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //得到被点击的用户名
                final String username = list.get(position);
                new AlertDialog.Builder(UserManageActivity.this).setTitle("删除用户").setMessage("你确定要删除"+username+"吗？")
                        .setPositiveButton("确定", new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyHelper myHelper = new MyHelper(UserManageActivity.this,"information",null,1);
                                SQLiteDatabase db  = myHelper.getReadableDatabase();
                                //删除用户
                                db.execSQL("delete from user where username = ?",new String[]{username});
                                //删除用户时，需要将用户名从list中移除
                                list.remove(username);
                                //刷新列表
                                arrayAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消",null).create().show();
                 /*
                因为android默认的优先响应ItemClickListener，所以当返回值为false时，
                ItemLongClickListener和ItemClickListener都会响应点击事件，因此应该
                返回true，提高ItemLongClickListener响应的优先级。
                */
                return true;
            }
        });
    }
    //利用反射机制关闭对话框
    public void closeDialog(DialogInterface dialog,boolean flag){
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //当flag为true时关闭对话框，否则不关闭
            field.set(dialog,flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
