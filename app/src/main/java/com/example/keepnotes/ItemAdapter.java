package com.example.keepnotes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
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

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ListViewHolder> {

    Context context;
    Activity activity;
    String gettitle;
    ArrayList<String> arr_title;
    DatabaseHelper databaseHelper;
    AESUtils aesUtils = new AESUtils();
    String spc_emsg, spc_iv, spc_salt;

    public ItemAdapter(Context context, ArrayList<String> arr_title, MainActivity activity) {
        this.context = context;
        this.arr_title = arr_title;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mview = LayoutInflater.from(context).inflate(R.layout.row_1, parent, false);
        return new ListViewHolder(mview);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, final int position) {

        databaseHelper = new DatabaseHelper(context);

        holder.unit_name.setText(arr_title.get(position));

        {
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.delete_dialog);
                    dialog.setCanceledOnTouchOutside(true);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                    TextView header = dialog.findViewById(R.id.delete_header);
                    TextView cancel = dialog.findViewById(R.id.cancel_btn);
                    TextView delete = dialog.findViewById(R.id.delete_btn);

                    spc_emsg = databaseHelper.arr_emsg(position);
                    spc_iv = databaseHelper.arr_iv(position);
                    spc_salt = databaseHelper.arr_salt(position);

                    header.setText("DELETE " + arr_title.get(position));

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean check = databaseHelper.deleteMessage(arr_title.get(position), spc_emsg, spc_iv, spc_salt);
                            if (check) {
                                Toast.makeText(context, "Deleted Successfully.", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                            activity.recreate();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });

                    dialog.show();
                }
            });
        } //delete onclick

        {
            holder.msg_card.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(activity);
                    dialog.setContentView(R.layout.input_dialog);
                    dialog.setCanceledOnTouchOutside(true);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                    Button next = dialog.findViewById(R.id.btn_next);
                    TextView header = dialog.findViewById(R.id.header);
                    final Button cancel = dialog.findViewById(R.id.btn_cancel);
                    final EditText input = dialog.findViewById(R.id.input_name);
                    header.setText("Type password for verify :");
                    header.setTextSize(18);
                    cancel.setVisibility(View.GONE);

                    {
                        next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                gettitle = input.getText().toString();

                                spc_emsg = databaseHelper.arr_emsg(position);
                                spc_iv = databaseHelper.arr_iv(position);
                                spc_salt = databaseHelper.arr_salt(position);

                                Log.d("get_msg", spc_emsg);
                                Log.d("get_iv", spc_emsg);
                                Log.d("get_salt", spc_emsg);

                                if (gettitle.isEmpty()) {
                                    input.requestFocus();
                                    input.setError("Please enter the password!");
                                } else {
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("salt", spc_salt);
                                    map.put("iv", spc_iv);
                                    map.put("message", spc_emsg);
                                    map.put("password", gettitle);

                                    try {
                                        String or_msg = aesUtils.decrypt(map);
                                        if (or_msg != null) {
                                            dialog.dismiss();
                                            res(or_msg);
                                        }
                                    } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException |
                                            NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException
                                            | BadPaddingException e) {
                                        e.printStackTrace();
                                        input.setText("");
                                        input.requestFocus();
                                        input.setError("Wrong Password!!!");
                                    }
                                }
                            }
                        });
                    } //next onclick

                    dialog.show();
                }
            });
        } //card onclick

    }

    @Override
    public int getItemCount() {
        Log.d("kan", "" + arr_title.size());
        return arr_title.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView unit_name;
        CardView msg_card;
        Button delete;

        public ListViewHolder(View itemView) {
            super(itemView);
            unit_name = itemView.findViewById(R.id.msg_title);
            msg_card = itemView.findViewById(R.id.msg_card);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    public void res(String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.result_dialog);
        dialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        Button ok = dialog.findViewById(R.id.btn_ok);
        TextView text_msg = dialog.findViewById(R.id.msg_txt);

        text_msg.setText(msg);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
