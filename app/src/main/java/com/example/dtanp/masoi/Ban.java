package com.example.dtanp.masoi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dtanp.masoi.adapter.CustomAdapterChat;
import com.example.dtanp.masoi.control.StaticFirebase;
import com.example.dtanp.masoi.control.StaticUser;
import com.example.dtanp.masoi.model.Chat;
import com.example.dtanp.masoi.model.NhanVat;
import com.example.dtanp.masoi.model.TextViewAdd;
import com.example.dtanp.masoi.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Ban extends Activity {

    ListView listchat;
    CustomAdapterChat adapterChat;
    Button btnss, btnsend,btnGiet,btnKhongGiet;
    ImageView imgnhanvat,imgTreoCo;
    LinearLayout linearLayout,linearLayoutListUser,linearLayoutTreoCo;
    List<TextViewAdd> textViewAddList, textViewAddListSong, textViewAddListDanThuong;
    TextView txtthoigian,txtTreoCo,txtluot;
    List<Chat> list;
    FirebaseDatabase database;
    EditText edtchat;
    DatabaseReference reference;
    TextView user1, user2, user3, user4, user5, user6, user7, user8, user9,txttenuser,txtsophong,txttenphong;
    NhanVat nhanvat;
    LinearLayout linearLayoutChat;
    private ImageButton btnuser1, btnuser2, btnuser3, btnuser4, btnuser5, btnuser6, btnuser7, btnuser8, btnuser9;
    int dem;
    Timer timer;
    Handler handler;
    HashMap<String, String> hashMap;
    List<NhanVat> listnhanvat;
    List<User> listuser;
    boolean die=false;
    boolean nap = false;
    boolean ready =false;
    boolean flagstart=false;
    ImageButton btnback;
    AlertDialog dialog;
    String iDBiBoPhieu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban);
        database = StaticFirebase.database;
//        DatabaseReference reference = database.getReference();
//        reference.child("Chat").child("cKvRXGjGbOX5e6t7fMM7YKRAoyp2").push().setValue(new Chat(" ","tao ne"));
        textViewAddList = new ArrayList<>();

        btnss = findViewById(R.id.btnss);
        //btnss.setVisibility(View.INVISIBLE);
        imgnhanvat = findViewById(R.id.imgnhanvat);
        // imgnhanvat.setImageAlpha(1);
        txtthoigian = findViewById(R.id.txtthoigian);
        txtthoigian.setVisibility(View.VISIBLE);
        txtsophong = findViewById(R.id.txtsophong);
        txttenphong = findViewById(R.id.txttenphong);
        txttenphong.setText(StaticUser.phong.getTenphong());
        txtsophong.setText(StaticUser.phong.getSophong()+"");
        listchat = findViewById(R.id.listchat);
        listchat.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list = new ArrayList<>();
        adapterChat = new CustomAdapterChat(this, R.layout.custom_chat, list);
        listchat.setAdapter(adapterChat);
        adapterChat.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listchat.setSelection(adapterChat.getCount()-1);
            }
        });
        btnsend = findViewById(R.id.btnsend);
        edtchat = findViewById(R.id.edtchat);
        linearLayoutChat = findViewById(R.id.lnrchat);
        linearLayoutChat.setVisibility(View.INVISIBLE);
        hashMap = new HashMap<>();
        listnhanvat = new ArrayList<>();
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chat chat = new Chat();
                if (edtchat.getText().toString() != "") {
                    chat.setUsername(StaticUser.user.getUser());
                    chat.setMesage(edtchat.getText().toString());
                    send(chat);
                    edtchat.setText("");
                }
            }
        });
        linearLayoutListUser = findViewById(R.id.lnrlistuser);
        linearLayoutTreoCo = findViewById(R.id.lnrtreoco);
        linearLayoutTreoCo.setVisibility(View.INVISIBLE);
        linearLayoutListUser.setVisibility(View.VISIBLE);
        imgnhanvat.setVisibility(View.INVISIBLE);
        btnGiet = findViewById(R.id.btngiet);
        btnKhongGiet=findViewById(R.id.btnkhonggiet);
        txtTreoCo = findViewById(R.id.txttreoco);
        imgTreoCo = findViewById(R.id.imgtreoco);
        listuser = new ArrayList<>();
        txtluot = findViewById(R.id.txtLuot);
        addDialog();
        btnback= findViewById(R.id.btnback);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        capnhatlistchat();
        anhxa();
        //findViewById(R.id.lnrchat).setAlpha(1);
        capnhatlistuser();
        laylistuser();
        LangNgheAllManHinh();
        LangNgheOK();
        //OntouchUser(textViewAddList);
        AddConTrols();


    }

    public void addDialoguser(User user)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_user,null);
        builder.setView(view);
        TextView txtuser = view.findViewById(R.id.txtuser);
        TextView txtid = view.findViewById(R.id.txtid);
        TextView txtlevel = view.findViewById(R.id.txtlevel);
        TextView txtwin = view.findViewById(R.id.txtwwin);
        TextView txtloss = view.findViewById(R.id.txtloss);

        txtid.setText(user.getId());
        txtuser.setText(user.getUser());

        Button btnkick = view.findViewById(R.id.btnkick);
        btnkick.setVisibility(View.INVISIBLE);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    public void addDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thoát");
        builder.setMessage("Bạn muốn thoát ra khỏi phòng !");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reference.child("Room").child(StaticUser.userHost.getId()).child("ListUser").child(StaticUser.user.getId()).removeValue();
                Intent intent = new Intent(Ban.this,room.class);
                startActivity(intent);
                finish();

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog = builder.create();

    }

    public void AddConTrols()
    {
        btnGiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Room").child(StaticUser.userHost.getId()).child("BangBoPhieu").child(StaticUser.user.getId()).setValue(1);
                btnGiet.setVisibility(View.INVISIBLE);
                btnKhongGiet.setVisibility(View.INVISIBLE);
            }
        });
        btnKhongGiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Room").child(StaticUser.userHost.getId()).child("BangBoPhieu").child(StaticUser.user.getId()).setValue(2);
                btnKhongGiet.setVisibility(View.INVISIBLE);
                btnGiet.setVisibility(View.INVISIBLE);
            }
        });
        btnss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ready==false)
                {
                    reference.child("Room").child(StaticUser.userHost.getId()).child("ListUserReady").child(StaticUser.user.getId()).setValue(true);
                    ready=true;
                    btnss.setText("BỎ SẲN SÀNG");
                }
                else
                {
                    reference.child("Room").child(StaticUser.userHost.getId()).child("ListUserReady").child(StaticUser.user.getId()).setValue(false);
                    ready=false;
                    btnss.setText("SẲN SÀNG");
                }
            }
        });
    }

    public void LangNgheOK()
    {
        reference.child("Room").child(StaticUser.userHost.getId()).child("OK").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = dataSnapshot.getValue(Boolean.class);
                if (flag==false && flagstart==true)
                {
                    OntouchUser(textViewAddList);
                    linearLayoutChat.setVisibility(View.INVISIBLE);
                    imgnhanvat.setVisibility(View.INVISIBLE);
                    txtthoigian.setVisibility(View.INVISIBLE);
                    linearLayoutTreoCo.setVisibility(View.INVISIBLE);
                    linearLayoutListUser.setVisibility(View.VISIBLE);
                    die=false;
                    textViewAddListSong.clear();
                    textViewAddListDanThuong.clear();
                    listnhanvat.clear();
                    list.clear();
                    ResetAnhUser();
                }
                else if (flag==true)
                {
                    OffTouchUser(textViewAddList);
                    getListSong();
                    getNhanVat();
                    getListNhanVat();
                    NapLangNghe();
                    flagstart=true;

                }
                System.out.println("Lang Nghe OK");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public  void ResetAnhUser()
    {
        for (TextViewAdd text : textViewAddList)
        {
            text.getUser().setImageResource(R.drawable.image_user);
        }
    }

    public void getListSong()
    {
        for (TextViewAdd text : textViewAddList)
        {
            textViewAddListSong.add(text);
        }
    }

    public void NapLangNghe()
    {
        if(nap==false)
        {
            //LangNgheAllManHinh();
            LangNgheChat();
            setThoiGian();
            LangNgheLuotDB();
            LangNgheIdChon();
            LangNgheDie();
            LangNgheLuot();
            nap=true;
        }
    }

    public void LamMoUser()
    {
        for (TextViewAdd textViewAdd : textViewAddList)
        {
            textViewAdd.getUser().setAlpha(0.4f);
            textViewAdd.getUser().setEnabled(false);
        }
    }
    public void RemoveUser(User user)
    {
        for (TextViewAdd text : textViewAddList) {
            if (text.getUseradd().getId().toString().trim().equals(user.getId().toString())) {
                text.getTxtuser().setText("");
                text.setFlag(false);
                text.setUseradd(null);
                text.getUser().setEnabled(false);
                text.getUser().setAlpha(0.4f);
                break;
            }
        }
        hashMap.remove(user.getUser().toString());
    }

    public void LangNgheChat() {
        reference.child("Room").child(StaticUser.userHost.getId()).child("AllChat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = dataSnapshot.getValue(Boolean.class);
                if (flag == true) {
                    linearLayoutChat.setVisibility(View.VISIBLE);
                    listchat.setVisibility(View.VISIBLE);
                    findViewById(R.id.lnrkhungchat).setVisibility(View.VISIBLE);
                    txtthoigian.setVisibility(View.VISIBLE);
                    DemGiay(30);
                } else {
                    linearLayoutChat.setVisibility(View.INVISIBLE);
                    txtthoigian.setVisibility(View.INVISIBLE);
                    listchat.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void DemGiay(int giay) {
        dem = giay;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                dem--;
                handler.sendEmptyMessage(0);

            }
        }, 0, 1000);


    }

    public void setThoiGian() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                txtthoigian.setText(dem + "");
                if (dem < 1) {
                    //handlerMaSoi.sendEmptyMessage(0);
                    timer.cancel();
                    //manv=8;
                }
            }
        };
    }

    public void setImageNhanVat(int ma) {
        switch (ma) {
            case 1:
                imgnhanvat.setImageResource(R.drawable.imgmasoi);
                break;
            case 2:
                imgnhanvat.setImageResource(R.drawable.imgdanlang);
                break;
            case 3:
                imgnhanvat.setImageResource(R.drawable.imgthosan);
                break;
            case 4:
                imgnhanvat.setImageResource(R.drawable.imgbaove);
                break;
            case 5:
                imgnhanvat.setImageResource(R.drawable.imggialang);
                break;
            case 6:
                imgnhanvat.setImageResource(R.drawable.imgtientri);
                break;
            default:
                break;
        }
        imgnhanvat.setVisibility(View.VISIBLE);
    }

    public void LangNgheLuot() {
        reference.child("Room").child(StaticUser.userHost.getId()).child("ListUserSang").child(StaticUser.user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = dataSnapshot.getValue(Boolean.class);
                if (flag == true) {
                    txtthoigian.setVisibility(View.VISIBLE);
                    DemGiay(30);
                    AddClickUser("BangChonChucNang");
                    if (nhanvat.getManv() == 1) {
                        OntouchUser(textViewAddListDanThuong);
                        System.out.println("so nguoi" + textViewAddListDanThuong.size());
                    } else {
                        OntouchUser(textViewAddListSong);
                    }
                } else {
                    if (nhanvat.getManv() == 1) {
                        OffTouchUser(textViewAddListDanThuong);
                        txtthoigian.setVisibility(View.INVISIBLE);
                    } else {
                        txtthoigian.setVisibility(View.INVISIBLE);
                        OffTouchUser(textViewAddListSong);
                    }
                }
                System.out.println("Lang Nghe Luot");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void setDieUser(TextViewAdd text) {

        text.getUser().setEnabled(false);
        text.getUser().setImageResource(R.drawable.die);
        text.getUser().setAlpha(0.3f);
        textViewAddList.remove(text);
    }


    public void LangNgheDie()
    {
        reference.child("Room").child(StaticUser.userHost.getId()).child("BangDie").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String id = dataSnapshot.getValue(String.class);
                if(!id.equals("A"))
                {
                    if(id.equals(StaticUser.user.getId()))
                    {
                        die=true;
                    }
                    else {
                        XoaNhanVat(id);
                        for (TextViewAdd text : textViewAddList) {
                            if (text.getUseradd().getId().toString().equals(id)) {
                                setDieUser(text);
                                break;
                            }
                        }
                    }
                }
                System.out.println("Lang Nghe Die");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String id = dataSnapshot.getValue(String.class);
                if(!id.equals("A"))
                {
                    if(id.equals(StaticUser.user.getId()))
                    {
                        die=true;
                    }
                    else {
                        for (TextViewAdd text : textViewAddList) {
                            if (text.getUseradd().getId().toString().equals(id)) {
                                setDieUser(text);
                                break;
                            }
                        }
                    }
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void OffTouchUser(List<TextViewAdd> list) {
        for (TextViewAdd text : list) {
            text.getUser().setEnabled(false);
            text.getUser().setAlpha(0.8f);
        }
    }

    public void OntouchUser(List<TextViewAdd> list) {
        for (TextViewAdd text : list) {
            text.getUser().setEnabled(true);
            text.getUser().setAlpha(1f);
        }
    }

    public void getNhanVat() {
        reference.child("Room").child(StaticUser.userHost.getId()).child("BangNhanVat").child(StaticUser.user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nhanvat = dataSnapshot.getValue(NhanVat.class);
                System.out.println(nhanvat.getId());

                setImageNhanVat(nhanvat.getManv());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void capnhatlistuser() {
        reference = database.getReference();
        reference.child("Room").child(StaticUser.userHost.getId()).child("ListUser").child(StaticUser.user.getId()).setValue(StaticUser.user);
    }

    public void capnhatlistchat() {
        reference = database.getReference();
        reference.child("Chat").child(StaticUser.userHost.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                if (!chat.getMesage().equals(" ")) {
                    list.add(chat);
                    adapterChat.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void send(Chat chat) {
        reference = database.getReference();
        reference.child("Chat").child(StaticUser.userHost.getId()).push().setValue(chat);

    }

    public void anhxa() {
        user1 = findViewById(R.id.txtuser1);
        user2 = findViewById(R.id.txtuser2);
        user3 = findViewById(R.id.txtuser3);
        user4 = findViewById(R.id.txtuser4);
        user5 = findViewById(R.id.txtuser5);
        user6 = findViewById(R.id.txtuser6);
        user7 = findViewById(R.id.txtuser7);
        user8 = findViewById(R.id.txtuser8);
        user9 = findViewById(R.id.txtuser9);

        btnuser1 = findViewById(R.id.btnuser1);
        btnuser2 = findViewById(R.id.btnuser2);
        btnuser3 = findViewById(R.id.btnuser3);
        btnuser4 = findViewById(R.id.btnuser4);
        btnuser5 = findViewById(R.id.btnuser5);
        btnuser6 = findViewById(R.id.btnuser6);
        btnuser7 = findViewById(R.id.btnuser7);
        btnuser8 = findViewById(R.id.btnuser8);
        btnuser9 = findViewById(R.id.btnuser9);


        textViewAddList.add(new TextViewAdd(btnuser1, user1, false));
        textViewAddList.add(new TextViewAdd(btnuser2, user2, false));
        textViewAddList.add(new TextViewAdd(btnuser3, user3, false));
        textViewAddList.add(new TextViewAdd(btnuser4, user4, false));
        textViewAddList.add(new TextViewAdd(btnuser5, user5, false));
        textViewAddList.add(new TextViewAdd(btnuser6, user6, false));
        textViewAddList.add(new TextViewAdd(btnuser7, user7, false));
        textViewAddList.add(new TextViewAdd(btnuser8, user8, false));
        textViewAddList.add(new TextViewAdd(btnuser9, user9, false));

        txttenuser=findViewById(R.id.txttenuser);
        txttenuser.setText(StaticUser.user.getUser());

        textViewAddListDanThuong = new ArrayList<>();
        textViewAddListSong = new ArrayList<>();
        LamMoUser();


    }

    public void ghitextview(int id, User user) {
        reference = database.getReference();
        reference.child("Room").child(StaticUser.userHost.getId()).child("TextView").child(user.getId()).setValue(id);
    }

    public void adduser(final User user) {
        //int id = 1;
        for (TextViewAdd text : textViewAddList) {
            if (text.isFlag() == false) {
                text.setFlag(true);
                text.getTxtuser().setText(user.getUser().toString());
                text.setUseradd(user);
                text.getUser().setAlpha(1f);
                text.getUser().setEnabled(true);
                text.getUser().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addDialoguser(user);
                    }
                });
                break;

            }
        }


        hashMap.put(user.getUser().toString(), user.getId().toString());
    }

    public void laylistuser() {
        reference = database.getReference();
        reference.child("Room").child(StaticUser.userHost.getId()).child("ListUser").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User us = dataSnapshot.getValue(User.class);
                if (us.getId() != StaticUser.user.getId()) {
                    System.out.println(us.getUser());
                    adduser(us);
                    listuser.add(us);

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                User us = dataSnapshot.getValue(User.class);
                if(!us.getId().toString().trim().equals(StaticUser.user.getId().toString().trim()))
                {
                    RemoveUser(us);
                }
                else
                {
                    Intent intent = new Intent(Ban.this,room.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void HienThiLuot(int luot) {
        String to = "";
        switch (luot) {
            case 1:
                to = "Ma Soi Dang Chon";
                break;
            case 2:
                to = "Bao Ve Dang chon";
                break;
            case 3:
                to = "Tho San Dang Chon";
                break;
            case 4:
                to = "Tien Tri Dang Chon";
                break;
            case 5:
                to = "Dan Lang Bieu Quyet";
                break;
            case 6:
                to = "Nguoi Treo co giai trinh";
                break;
            case 7:
                to = "Bo Phieu Giet";
                break;
            default:
                break;
        }
        txtluot.setText(to.toString().trim());
    }
    public void LangNgheLuotDB()
    {
        reference.child("Room").child(StaticUser.userHost.getId()).child("Luot").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int l = dataSnapshot.getValue(Integer.class);
                if (l != 0) {
                    if(l==1)
                    {
                        linearLayoutListUser.setVisibility(View.VISIBLE);
                        linearLayoutTreoCo.setVisibility(View.INVISIBLE);
                        linearLayoutChat.setVisibility(View.INVISIBLE);
                    }
                    if(l==7)
                    {
                        if(die==false) {
                            if(StaticUser.user.getId().toString().trim().equals(iDBiBoPhieu)==false) {
                                btnKhongGiet.setVisibility(View.VISIBLE);
                                btnGiet.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    HienThiLuot(l);
                } else {
                    txtluot.setText("");
                }
                System.out.println("Lang Nghe Luot DB");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void AddClickUser(final String st) {
        for (final TextViewAdd text : textViewAddList) {
            text.getUser().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    timer.cancel();
                    if (nhanvat.getManv() == 6 && st.equals("BangChonChucNang")==true) {
                        for (NhanVat nv : listnhanvat) {
                            if (text.getUseradd().getId().toString().equals(nv.getId().toString())) {
                                if (nv.getManv() == 1) {
                                    Toast.makeText(Ban.this, "day la ma soi", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(Ban.this, "day khong phai la soi la ma soi", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                        }
                    }
                    reference.child("Room").child(StaticUser.userHost.getId()).child(st).child(StaticUser.user.getId()).setValue(hashMap.get(text.getTxtuser().getText().toString()));
                    OffTouchUser(textViewAddListSong);

                }
            });
        }
    }

    public void getListNhanVat() {
        reference.child("Room").child(StaticUser.userHost.getId()).child("BangNhanVat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                NhanVat nv = dataSnapshot.getValue(NhanVat.class);
                if (!nv.getId().toString().equals(nhanvat.getId().toString())) {
                    listnhanvat.add(nv);
                    if (nv.getManv() != 1) {
                        for (TextViewAdd text : textViewAddList) {
                            if (text.getUseradd().getId().toString().equals(nv.getId().toString())) {
                                textViewAddListDanThuong.add(text);
                                System.out.println(nv);
                                System.out.println(text.getUseradd().getUser());
                                System.out.println(text.getTxtuser().getText().toString());
                                break;
                            }

                        }
                    }
                    else
                    {
                        System.out.println("NhanVat la soi " + nv);
                    }

                }
                else
                {
                    System.out.println("La Toi " + nv);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

//                NhanVat nv = dataSnapshot.getValue(NhanVat.class);
//                if (!nv.getId().toString().equals(nhanvat.getId().toString())) {
//                    listnhanvat.add(nv);
//                    if (nv.getManv() != 1) {
//                        for (TextViewAdd text : textViewAddList) {
//                            if (text.getUseradd().getId().toString().equals(nv.getId().toString())) {
//                                textViewAddListDanThuong.add(text);
//                                break;
//                            }
//
//                        }
//                    }
//
//                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void LangNgheAllManHinh() {
        reference.child("Room").child(StaticUser.userHost.getId()).child("AllManHinhChon").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = dataSnapshot.getValue(Boolean.class);
                if (flag == true) {
                    if(die==false) {
                        OntouchUser(textViewAddListSong);
                        AddClickUser("BangIdChon");
                    }
                } else {
                    OffTouchUser(textViewAddListSong);
                }
                System.out.println("Lang Nghe ALL Man Hinh");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void LangNgheIdChon()
    {
        reference.child("Room").child(StaticUser.userHost.getId()).child("IDBiBoPhieu").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String st = dataSnapshot.getValue(String.class);
                iDBiBoPhieu=st;
                if(!st.equals("A"))
                {
                    if(st.equals(StaticUser.user.getId().toString()))
                    {
                        linearLayoutChat.setVisibility(View.VISIBLE);
                        listchat.setVisibility(View.VISIBLE);
                        findViewById(R.id.lnrkhungchat).setVisibility(View.VISIBLE);
                        linearLayoutListUser.setVisibility(View.INVISIBLE);
                        linearLayoutTreoCo.setVisibility(View.VISIBLE);
                        txtTreoCo.setText(StaticUser.user.getUser());
                    }
                    else {
                        btnGiet.setVisibility(View.INVISIBLE);
                        btnKhongGiet.setVisibility(View.INVISIBLE);
                        findViewById(R.id.lnrkhungchat).setVisibility(View.INVISIBLE);
                        linearLayoutListUser.setVisibility(View.INVISIBLE);
                        linearLayoutTreoCo.setVisibility(View.VISIBLE);
                        linearLayoutChat.setVisibility(View.VISIBLE);
                        listchat.setVisibility(View.VISIBLE);
                        System.out.println("Toi IDBIBOPHIEU");


                        for (User user : listuser) {
                            if (user.getId().equals(st)) {
                                txtTreoCo.setText(user.getUser());
                                break;
                            }
                        }
                    }
                }
                System.out.println("Lang Nghe ID Bo Phieu");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void XoaNhanVat(String id) {
        TextViewAdd textViewAdd = new TextViewAdd();
        for (TextViewAdd text : textViewAddListSong) {
            if (text.getUseradd().getId().toString().equals(id)) {
                textViewAdd = text;
                textViewAddListSong.remove(text);
                break;
            }
        }
        for (TextViewAdd text : textViewAddListDanThuong) {
            if (text.getUseradd().getId().toString().equals(id)) {
                textViewAdd = text;
                textViewAddListDanThuong.remove(text);
                break;
            }
        }
        if(!id.equals(StaticUser.user.getId()))
        {
            setDieUser(textViewAdd);
        }
        else
        {
            die=true;
        }

    }

}
