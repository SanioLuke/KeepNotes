package com.example.keepnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {

    RecyclerView listView;
    ArrayList<String> arrayList= new ArrayList<>();
    ItemAdapter itemAdapter;
    FloatingActionButton add;
    String gettitle;
    DatabaseHelper databaseHelper;
    TextView empty_txt;
    AESUtils aesUtils= new AESUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView= findViewById(R.id.items);
        add = findViewById(R.id.add);
        empty_txt= findViewById(R.id.empty_txt);
        databaseHelper= new DatabaseHelper(getApplicationContext());

        arrayList= databaseHelper.arr_title();

        if(arrayList.isEmpty()){
            empty_txt.setVisibility(View.VISIBLE);
        }

        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1);
        listView.setLayoutManager(manager);

        itemAdapter= new ItemAdapter(MainActivity.this,arrayList,MainActivity.this);
        listView.setAdapter(itemAdapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title_fn();
            }
        });
    }

    private void title_fn(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.input_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        final Button next= dialog.findViewById(R.id.btn_next);
        final Button cancel= dialog.findViewById(R.id.btn_cancel);
        final EditText input= dialog.findViewById(R.id.input_name);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettitle= input.getText().toString();
                if(gettitle.isEmpty()){
                    input.requestFocus();
                    input.setError("Please enter the title name!");
                }
                else{
                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("Process", Context.MODE_PRIVATE).edit();
                    editor.putString("title",gettitle);
                    editor.apply();
                    dialog.dismiss();
                    msg_fn();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("Process", Context.MODE_PRIVATE).edit();
                editor.putString("title","");
                editor.apply();
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void msg_fn(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.input_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        Button next= dialog.findViewById(R.id.btn_next);
        TextView header= dialog.findViewById(R.id.header);
        final Button cancel= dialog.findViewById(R.id.btn_cancel);
        final EditText input= dialog.findViewById(R.id.input_name);
        header.setText("Message :");
        input.setHint("Enter the message...");

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettitle= input.getText().toString();
                if(gettitle.isEmpty()){
                    input.requestFocus();
                    input.setError("Please enter the message!");
                }
                else {
                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("Process", Context.MODE_PRIVATE).edit();
                    editor.putString("msg", gettitle);
                    editor.apply();
                    dialog.dismiss();
                    pass_fn();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("Process", Context.MODE_PRIVATE).edit();
                editor.putString("title","");
                editor.putString("msg","");
                editor.apply();
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void pass_fn(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.input_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        Button next= dialog.findViewById(R.id.btn_next);
        TextView header= dialog.findViewById(R.id.header);
        final Button cancel= dialog.findViewById(R.id.btn_cancel);
        final EditText input= dialog.findViewById(R.id.input_name);
        next.setText("CREATE");
        cancel.setVisibility(View.GONE);
        header.setText("Password :");
        input.setHint("Enter the password...");

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettitle= input.getText().toString();
                if(gettitle.isEmpty()){
                    input.requestFocus();
                    input.setError("Please enter the password!");
                }
                else {
                    try {
                        SharedPreferences prefs = getSharedPreferences("Process", Activity.MODE_PRIVATE);
                        String title = prefs.getString("title", "");
                        String msg= prefs.getString("msg", "");

                        HashMap<String,String> map= aesUtils.encrypt(gettitle,msg);
                        Log.d("msg", Objects.requireNonNull(map.get("message")));
                        Log.d("ivan", Objects.requireNonNull(map.get("iv")));
                        Log.d("salt", Objects.requireNonNull(map.get("salt")));

                        String emsg= map.get("message");
                        String iv= map.get("iv");
                        String salt= map.get("salt");

                        boolean add= databaseHelper.addUser(title,emsg,iv,salt);
                        if(add){
                            Log.d("adduser", "data added");
                        }
                        else {
                            Log.d("adduser", "data not added");
                        }
                        dialog.dismiss();
                        recreate();
                    }
                    catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException |
                            NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException
                            | BadPaddingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        dialog.show();
    }

}