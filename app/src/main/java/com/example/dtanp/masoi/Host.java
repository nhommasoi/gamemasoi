package com.example.dtanp.masoi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.dtanp.masoi.model.Phong;
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

public class Host extends Activity {
    ListView listchat;
    CustomAdapterChat adapterChat;
    Button btnbatdau, btnsend, btnGiet, btnKhongGiet;
    ImageView imgnhanvat, imgTreoCo;
    List<TextViewAdd> textViewAddList, textViewAddListSong, textViewAddListDanThuong;
    TextView txtthoigian, txtLuot, txtTreoCo;
    List<Chat> list;
    FirebaseDatabase database;
    EditText edtchat;
    public List<User> listuser, listuseringame;
    DatabaseReference reference;
    TextView user1, user2, user3, user4, user5, user6, user7, user8, user9,txttenuser,txtsophong,txttenphong;
    LinearLayout linearLayoutChat, linearLayoutListUser, linearLayoutTreoCo,linearLayoutkhungchat;
    private Timer timer;
    private Handler handler, handlerMaSoi;
    private ImageButton btnuser1, btnuser2, btnuser3, btnuser4, btnuser5, btnuser6, btnuser7, btnuser8, btnuser9;
    List<User> listUserMaSoi;
    List<User> listUserDanLang;
    User userThoSan, userGiaLang, userBaoVe, userTienTri;
    List<NhanVat> listNhanVat;
    Phong phong;
    NhanVat nhanvat;
    int manv;
    List<String> listidMaSoichon, listallchon;
    String idThoSanChon, idTienTriChon = "", idBaoVeChon, IDBoPhieu;
    HashMap<String, String> hashMap;
    boolean die = false;
    private boolean flagThoSan = false, flagTienTri = false, flagGiaLang = false, flagBaoVe = false;
    int countUserReady=0;
    AlertDialog dialog;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        anhxa();
        database = StaticFirebase.database;
        reference = database.getReference();
        taophong();
        capnhatlistchat();
        laylistuser();
        ConTrols();
        setThoiGian();
        XuLyChon();
        LangNgheUserReady();
        LamMoUser();

    }

    public  void LangNgheUserReady()
    {
        reference.child("Room").child(StaticUser.user.getId()).child("ListUserReady").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    boolean flag = dataSnapshot.getValue(Boolean.class);
                    if(flag==true)
                    {
                        countUserReady++;
                        if (countUserReady>8)
                        {
                            countUserReady=0;
                            btnbatdau.setAlpha(1);
                            btnbatdau.setEnabled(true);

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

    public void ConTrols() {
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (die == false) {
                    Chat chat = new Chat();
                    if (edtchat.getText().toString() != "") {
                        chat.setUsername(StaticUser.user.getUser());
                        chat.setMesage(edtchat.getText().toString());
                        send(chat);
                        edtchat.setText("");
                        //Toast.makeText(Host.this,"send",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        findViewById(R.id.btnbatdau).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OffTouchUser(textViewAddList);
                RanDom();
                PushNhanVat();
                reference.child("Room").child(StaticUser.user.getId()).child("OK").setValue(true);
                pushNgay();
                XuLyLuot(1, true);
                LangNgheLuotDB();
                getNhanVat();
                LangNgheAllManHinh();
                LangNgheLuot();
                //HienThiLuot(1);
                LangNgheChat();
                ListenSuKien();
                getListXuLy();
                getTextViewAddList();
                ListenIdBiGiet();
                LangNgheKQBP();
                LangNgheBangIDChon();
                LangNgheDie();
                LangNgheOK();
                btnbatdau.setVisibility(View.INVISIBLE);



            }
        });
        btnGiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Room").child(StaticUser.user.getId()).child("BangBoPhieu").child(StaticUser.user.getId()).setValue(1);
                btnGiet.setVisibility(View.INVISIBLE);
                btnKhongGiet.setVisibility(View.INVISIBLE);
            }
        });
        btnKhongGiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Room").child(StaticUser.user.getId()).child("BangBoPhieu").child(StaticUser.user.getId()).setValue(2);
                btnKhongGiet.setVisibility(View.INVISIBLE);
                btnGiet.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void LangNgheOK() {
        reference.child("Room").child(StaticUser.user.getId()).child("OK").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = dataSnapshot.getValue(Boolean.class);
                if (flag == false) {
                    OntouchUser(textViewAddList);
                    linearLayoutChat.setVisibility(View.INVISIBLE);
                    imgnhanvat.setVisibility(View.INVISIBLE);
                    txtthoigian.setVisibility(View.INVISIBLE);
                    linearLayoutTreoCo.setVisibility(View.INVISIBLE);
                    linearLayoutListUser.setVisibility(View.VISIBLE);
                    resetLaiGameMoi();
                    btnbatdau.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        textViewAddList = new ArrayList<>();


        textViewAddList.add(new TextViewAdd(btnuser1, user1, false));
        textViewAddList.add(new TextViewAdd(btnuser2, user2, false));
        textViewAddList.add(new TextViewAdd(btnuser3, user3, false));
        textViewAddList.add(new TextViewAdd(btnuser4, user4, false));
        textViewAddList.add(new TextViewAdd(btnuser5, user5, false));
        textViewAddList.add(new TextViewAdd(btnuser6, user6, false));
        textViewAddList.add(new TextViewAdd(btnuser7, user7, false));
        textViewAddList.add(new TextViewAdd(btnuser8, user8, false));
        textViewAddList.add(new TextViewAdd(btnuser9, user9, false));



        txttenuser = findViewById(R.id.txttenuser);
        txttenuser.setText(StaticUser.user.getUser());

        txtsophong = findViewById(R.id.txtsophong);
        txttenphong = findViewById(R.id.txttenphong);

        btnbatdau = findViewById(R.id.btnbatdau);
        btnbatdau.setAlpha(0.6f);
        btnbatdau.setEnabled(false);


        imgnhanvat = findViewById(R.id.imgnhanvat);
        // imgnhanvat.setAlpha(0);
        imgnhanvat.setVisibility(View.INVISIBLE);
        txtthoigian = findViewById(R.id.txtthoigian);

        listuser = new ArrayList<>();

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

        linearLayoutkhungchat = findViewById(R.id.lnrkhungchat);

        listUserMaSoi = new ArrayList<User>();
        listUserDanLang = new ArrayList<>();
        listNhanVat = new ArrayList<>();
        txtLuot = findViewById(R.id.txtLuot);
        listidMaSoichon = new ArrayList<>();
        listallchon = new ArrayList<>();
        hashMap = new HashMap<>();

        textViewAddListDanThuong = new ArrayList<>();
        textViewAddListSong = new ArrayList<>();

        txtthoigian.setVisibility(View.INVISIBLE);

        linearLayoutListUser = findViewById(R.id.lnrlistuser);
        linearLayoutTreoCo = findViewById(R.id.lnrtreoco);
        btnGiet = findViewById(R.id.btngiet);
        btnKhongGiet = findViewById(R.id.btnkhonggiet);
        txtTreoCo = findViewById(R.id.txttreoco);
        imgTreoCo = findViewById(R.id.imgtreoco);
        linearLayoutTreoCo.setVisibility(View.INVISIBLE);
        listuseringame = new ArrayList<>();
    }

    public void LamMoUser()
    {
        for (TextViewAdd textViewAdd : textViewAddList)
        {
            textViewAdd.getUser().setAlpha(0.4f);
            textViewAdd.getUser().setEnabled(false);
        }
    }

    public void getListUser(List<User> list) {
        for (User us : listuser) {
            list.add(us);
        }
    }

    public void capnhatlistchat() {
        reference = database.getReference();
        reference.child("Chat").child(StaticUser.user.getId()).addChildEventListener(new ChildEventListener() {
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

    public void addDialoguser(final User user)
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
        btnkick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Room").child(StaticUser.user.getId()).child("ListUser").child(user.getId().toString()).removeValue();
                dialog.cancel();
            }
        });
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog = builder.create();
        dialog.show();
    }
    public void ghitextview(int id, User user) {
        reference = database.getReference();
        reference.child("Room").child(StaticUser.user.getId()).child("TextView").child(user.getId()).setValue(id);
    }

    public void AddUser(final User user) {
        for (TextViewAdd text : textViewAddList) {
            if (text.isFlag() == false) {
                text.getTxtuser().setText(user.getUser());
                text.setFlag(true);
                text.setUseradd(user);
                text.getUser().setEnabled(true);
                text.getUser().setAlpha(1f);
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

    public void setDieUser(TextViewAdd text) {
        text.getUser().setEnabled(false);
        text.getUser().setImageResource(R.drawable.die);
        text.getUser().setAlpha(0.3f);
        //textViewAddList.remove(text);
    }


    public void send(Chat chat) {
        reference = database.getReference();
        reference.child("Chat").child(StaticUser.user.getId()).push().setValue(chat);

    }

    public void laylistuser() {
        reference = database.getReference();
        reference.child("Room").child(StaticUser.user.getId()).child("ListUser").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User us = dataSnapshot.getValue(User.class);
                if (!us.getId().toString().equals(StaticUser.user.getId().toString())) {
                    AddUser(us);
                    listuser.add(us);
                    StaticUser.phong.setSonguoi(StaticUser.phong.getSonguoi()+1);
                    reference.child("Room").child(StaticUser.user.getId()).child("songuoi").setValue(StaticUser.phong.getSonguoi());
                    reference.child("Room").child(StaticUser.user.getId()).child("ListUserReady").child(us.getId().toString()).setValue(false);
                    System.out.println("list user in lay list user" + listuser.size());
                } else {
                    listuser.add(us);
                }
                reference.child("Room").child(StaticUser.user.getId()).child("BangIdChon").child(us.getId().toString()).setValue("A");
                reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(us.getId().toString()).setValue("A");
                reference.child("Room").child(StaticUser.user.getId()).child("ListUserSang").child(us.getId().toString()).setValue(false);
                reference.child("Room").child(StaticUser.user.getId()).child("BangBoPhieu").child(us.getId()).setValue(0);
                reference.child("Room").child(StaticUser.user.getId()).child("BangDie").child(us.getId()).setValue("A");


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                User us = dataSnapshot.getValue(User.class);
                RemoveUserList(us);
                Toast.makeText(Host.this,"aaa"+listuser.size(),Toast.LENGTH_SHORT).show();
                RemoveUser(us);
                StaticUser.phong.setSonguoi(StaticUser.phong.getSonguoi()-1);
                reference.child("Room").child(StaticUser.user.getId()).child("songuoi").setValue(StaticUser.phong.getSonguoi());
                reference.child("Room").child(StaticUser.user.getId()).child("ListUserReady").child(us.getId().toString()).removeValue();
                reference.child("Room").child(StaticUser.user.getId()).child("BangIdChon").child(us.getId().toString()).removeValue();
                reference.child("Room").child(StaticUser.user.getId()).child("ListUserSang").child(us.getId().toString()).removeValue();
                reference.child("Room").child(StaticUser.user.getId()).child("BangBoPhieu").child(us.getId()).removeValue();
                reference.child("Room").child(StaticUser.user.getId()).child("BangDie").child(us.getId()).removeValue();
                reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(us.getId().toString()).removeValue();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void RemoveUserList(User user)
    {
        for (User us : listuser)
        {
            if (us.getId().toString().trim().equals(us.getId()))
            {
                listuser.remove(us);
                break;
            }
        }
    }
    public void taophong() {
        phong = new Phong();
        // System.out.println(StaticUser.user.getId());
        int soPhong = getIntent().getIntExtra("sophong",0);
        phong.setId(StaticUser.user.getId());
        phong.setSophong(soPhong);
        phong.setTenphong(StaticUser.user.getUser());
        phong.setSonguoi(1);
        StaticUser.phong = phong;
        txttenphong.setText(phong.getTenphong());
        txtsophong.setText(phong.getSophong()+"");
        reference.child("Room").child(phong.getId()).setValue(phong);
        reference.child("Room").child(phong.getId()).child("ListUser").child(StaticUser.user.getId()).setValue(StaticUser.user);
        reference.child("Room").child(phong.getId()).child("ListUserSang").child(StaticUser.user.getId()).setValue(false);
        reference.child("Room").child(phong.getId()).child("Luot").setValue(0);
        reference.child("Room").child(phong.getId()).child("AllChat").setValue(false);
        reference.child("Room").child(phong.getId()).child("AllManHinhChon").setValue(false);
        reference.child("Room").child(phong.getId()).child("BangIdChon").child(StaticUser.user.getId()).setValue("A");
        reference.child("Room").child(phong.getId()).child("IDBiBoPhieu").setValue("A");
        reference.child("Room").child(phong.getId()).child("BangBoPhieu").child(StaticUser.user.getId()).setValue(0);
        reference.child("Room").child(phong.getId()).child("BangDie").child(StaticUser.user.getId()).setValue("A");
        reference.child("Room").child(StaticUser.user.getId()).child("OK").setValue(false);
        Chat chat = new Chat();
        chat.setUsername(StaticUser.user.getUser());
        chat.setMesage(" ");
        reference.child("Chat").child(phong.getId()).push().setValue(chat);


    }

    int dem;
    boolean flagchat = false;

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
                if (dem < 0) {
                    //handlerMaSoi.sendEmptyMessage(0);
                    if (flagchat == true) {
                        manv = 7;
                        handlerMaSoi.sendEmptyMessage(0);
                        flagchat = false;
                    }
                    if (flagxuli == true) {
                        reference.child("Room").child(StaticUser.user.getId()).child("ListUserSang").child(IDBoPhieu).setValue(false);
                        //setLuotDB(7);
                        XuLyLuot(7, false);

                        if (die == false) {
                            if(StaticUser.user.getId().toString().trim().equals(IDBoPhieu)==false) {
                                btnKhongGiet.setVisibility(View.VISIBLE);
                                btnGiet.setVisibility(View.VISIBLE);
                            }
                        }
                        setLuotDB(7);
                        manv = 9;
                        flagxuli = false;
                    }
                    timer.cancel();
                    txtthoigian.setVisibility(View.INVISIBLE);
                    //manv=8;

                }
            }
        };
    }

    public void LangNgheDie() {
        reference.child("Room").child(StaticUser.user.getId()).child("BangDie").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String id = dataSnapshot.getValue(String.class);
                if (!id.equals("A")) {
                    if (id.equals(StaticUser.user.getId())) {
                        die = true;
                    } else {
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
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String id = dataSnapshot.getValue(String.class);
                if (!id.equals("A")) {
                    if (id.equals(StaticUser.user.getId())) {
                        die = true;
                    } else {
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


    public void RanDom() {
        List<User> listUserRandom = new ArrayList<>();
        getListUser(listUserRandom);
        getListUser(listuseringame);
        System.out.println(listuseringame.size() + "list user in game");
        System.out.println(listuser.size() + "list user");
        //listUserRandom.add(StaticUser.user);
        for (int i = 0; i < 10; i++) {
            // System.out.println(listUserRandom.size()+ "Lisuserrandom");
            NhanVat nv = new NhanVat();
            int k;
            if (listUserRandom.size() > 1) {
                k = (int) (Math.random() * listUserRandom.size());

            } else
                k = 0;
            if (i < 3) {
                listUserMaSoi.add(listUserRandom.get(k));
                nv.setManv(1);
                nv.setResource(R.drawable.imgmasoi);
            } else if (i < 6) {
                listUserDanLang.add(listUserRandom.get(k));
                nv.setManv(2);
                nv.setResource(R.drawable.imgdanlang);
            } else if (i == 6) {
                userThoSan = listUserRandom.get(k);
                listUserDanLang.add(listUserRandom.get(k));
                nv.setManv(3);
                nv.setResource(R.drawable.imgthosan);
            } else if (i == 7) {
                userBaoVe = listUserRandom.get(k);
                listUserDanLang.add(listUserRandom.get(k));
                nv.setManv(4);
                nv.setResource(R.drawable.imgbaove);
            } else if (i == 8) {
                userGiaLang = listUserRandom.get(k);
                listUserDanLang.add(listUserRandom.get(k));
                nv.setManv(5);
                nv.setResource(R.drawable.imggialang);
            } else if (i == 9) {
                userTienTri = listUserRandom.get(k);
                listUserDanLang.add(listUserRandom.get(k));
                nv.setManv(6);
                nv.setResource(R.drawable.imgtientri);


            }
            // System.out.println(k);
            nv.setId(listUserRandom.get(k).getId());
            listUserRandom.remove(k);
            //System.out.println(nv.getId());
            listNhanVat.add(nv);

        }
    }

    public void getListXuLy() {
        for (NhanVat nv : listNhanVat) {
            if (nv.getManv() != 1) {
                for (TextViewAdd text : textViewAddList) {
                    if (text.getUseradd().getId().toString().equals(nv.getId().toString())) {
                        textViewAddListDanThuong.add(text);
                        break;
                    }
                }
            }

        }
        // System.out.println(listNhanVat.size() + "list nhan vat");
        // System.out.println(textViewAddListDanThuong.size() + "list dan thuong");
    }

    public void getTextViewAddList() {
        for (TextViewAdd text : textViewAddList) {
            textViewAddListSong.add(text);
        }
    }

    public void PushNhanVat() {
        for (NhanVat nv : listNhanVat) {
            reference.child("Room").child(StaticUser.user.getId()).child("BangNhanVat").child(nv.getId()).setValue(nv);
        }

    }

    public void setImageNhanVat(int ma) {
        // imgnhanvat.setAlpha(1f);
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

    public void pushNgay() {
        // phong.setNgay(phong.getNgay() + 1);
        // reference.child("Room").child(StaticUser.user.getId()).child("ngay").setValue(phong.getNgay());
    }

    public void pushLuot(int t) {

        reference.child("Room").child(StaticUser.user.getId()).child("Luot").setValue(t);
    }

    public void getNhanVat() {
        reference.child("Room").child(StaticUser.user.getId()).child("BangNhanVat").child(StaticUser.user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nhanvat = dataSnapshot.getValue(NhanVat.class);
                setImageNhanVat(nhanvat.getManv());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        System.out.println(listuseringame.size() + "list user in game trong get nhan vat");
        System.out.println(listuser.size() + "list user trong get nhan vat");
    }

    public void removeListUserInGameID(String id) {
        for (User us : listuseringame) {
            if (us.getId().toString().equals(id)) {
                listuseringame.remove(us);
                break;
            }
        }
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
        txtLuot.setText(to.toString().trim());
    }

    public void setLuotDB(int luot) {
        reference.child("Room").child(StaticUser.user.getId()).child("Luot").setValue(luot);
    }

    public void LangNgheLuotDB() {
        reference.child("Room").child(StaticUser.user.getId()).child("Luot").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int l = dataSnapshot.getValue(Integer.class);
                if (l != 0) {
                    if (l == 1) {
                        linearLayoutChat.setVisibility(View.INVISIBLE);
                        listchat.setVisibility(View.INVISIBLE);
                        linearLayoutkhungchat.setVisibility(View.INVISIBLE);

                    }
                    if (l == 7) {
                        if(die==false) {
                            if(StaticUser.user.getId().toString().trim().equals(IDBoPhieu)==false) {
                                System.out.println("toi luot 7");
                                btnGiet.setVisibility(View.VISIBLE);
                                btnKhongGiet.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    HienThiLuot(l);
                } else {
                    txtLuot.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void XuLyChon() {
        handlerMaSoi = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (manv == 1) {
                    if (listUserMaSoi.size() == listidMaSoichon.size()) {
                        setLuotDB(2);
                        //pushLuot(2);
                        XuLyLuot(1, false);
                        XuLyLuot(4, true);
                    }
                } else if (manv == 4) {
                    setLuotDB(3);
                    //pushLuot(3);
                    XuLyLuot(4, false);
                    XuLyLuot(3, true);
                } else if (manv == 3) {
                    setLuotDB(4);
                    //pushLuot(4);
                    XuLyLuot(3, false);
                    XuLyLuot(6, true);
                } else if (manv == 6) {

                    XuLyLuot(6, false);
                    XuLyLuot(7, true);
                } else if (manv == 7) {
                    XuLyLuot(7, false);
                    XuLyLuot(8, true);
                    setLuotDB(5);
                } else if (manv == 8) {
                    XuLyLuot(8, false);
                    IDBoPhieu = getIDBOPhieu();
                    reference.child("Room").child(StaticUser.user.getId()).child("IDBiBoPhieu").setValue(IDBoPhieu);
                    reference.child("Room").child(StaticUser.user.getId()).child("ListUserSang").child(IDBoPhieu).setValue(true);
                    XuLiGiaiTrinh();
                } else if (manv == 9) {
                    if (giet == true) {
                        giet = false;
                        XoaNhanVat(IDBoPhieu);
                        XoaNhanVatChucNang(IDBoPhieu);
                        removeListUserInGameID(IDBoPhieu);
                        reference.child("Room").child(StaticUser.user.getId()).child("BangDie").child(IDBoPhieu).setValue(IDBoPhieu);
                    }

                    linearLayoutListUser.setVisibility(View.VISIBLE);
                    linearLayoutTreoCo.setVisibility(View.INVISIBLE);
                    linearLayoutChat.setVisibility(View.INVISIBLE);
                    XuLiCuoiNgay();

                }

            }
        };

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
        for (User user : listUserMaSoi) {
            if (user.getId().toString().equals(id)) {
                listUserMaSoi.remove(user);
                break;
            }
        }
        for (User user : listUserDanLang) {
            if (user.getId().toString().equals(id)) {
                listUserDanLang.remove(user);
                break;
            }
        }
        if (!id.equals(StaticUser.user.getId())) {
            setDieUser(textViewAdd);
        } else {
            die = true;
        }

    }

    boolean flagxuli = false;

    public void XuLiGiaiTrinh() {

        //XuLyLuot(7,true);
        setLuotDB(6);
        flagchat = false;
        flagxuli = true;
        txtthoigian.setVisibility(View.VISIBLE);
        DemGiay(20);

    }

    private int countYes = 0, countNo = 0;
    boolean giet = false;

    public void LangNgheKQBP() {
        reference.child("Room").child(StaticUser.user.getId()).child("BangBoPhieu").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int t = dataSnapshot.getValue(Integer.class);
                if (t != 0) {
                    if (t == 1) {
                        countYes++;
                    } else {
                        countNo++;
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int t = dataSnapshot.getValue(Integer.class);
                if (t != 0) {
                    if (t == 1) {
                        countYes++;
                    } else {
                        countNo++;
                    }
                }
                if (countNo + countYes == (listuseringame.size()-1)){
                    if (countYes > countNo) {
                        giet = true;
                        countNo = 0;
                        countYes = 0;
                    }
                    manv = 9;
                    handlerMaSoi.sendEmptyMessage(0);
                }
                System.out.println(countYes + "Dong y");
                System.out.println(textViewAddListSong.size() + "so nguoi con song");

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

    public void XuLyLuot(int luot, boolean flag) {
        if (flagGiaLang == true) {
            if (luot > 1 && luot < 7) {
                luot = 7;
            }
        } else {

            if (luot == 4) {
                if (flagBaoVe == true) {
                    luot = 6;
                }
            }
            if (luot == 3) {
                if (flagThoSan == true) {
                    luot = 4;
                }
            }
            if (luot == 6) {
                if (flagTienTri == true) {
                    luot = 7;
                }
            }
        }
        if (luot == 1) {
            pushLuot(1);
            if (listUserMaSoi.size() > 0) {
                reference.child("Room").child(StaticUser.user.getId()).child("ListUserSang").child(listUserMaSoi.get(0).getId().toString()).setValue(flag);
            }
            if (listUserMaSoi.size() > 1) {
                reference.child("Room").child(StaticUser.user.getId()).child("ListUserSang").child(listUserMaSoi.get(1).getId().toString()).setValue(flag);
            }
            if (listUserMaSoi.size() > 2) {
                reference.child("Room").child(StaticUser.user.getId()).child("ListUserSang").child(listUserMaSoi.get(2).getId().toString()).setValue(flag);
            }
        } else if (luot == 3) {
            pushLuot(3);
            reference.child("Room").child(StaticUser.user.getId()).child("ListUserSang").child(userThoSan.getId().toString()).setValue(flag);
        } else if (luot == 4) {
            pushLuot(2);
            reference.child("Room").child(StaticUser.user.getId()).child("ListUserSang").child(userBaoVe.getId().toString()).setValue(flag);
        } else if (luot == 6) {
            pushLuot(4);
            reference.child("Room").child(StaticUser.user.getId()).child("ListUserSang").child(userTienTri.getId().toString()).setValue(flag);
        } else if (luot == 7) {
            reference.child("Room").child(StaticUser.user.getId()).child("AllChat").setValue(flag);
        } else if (luot == 8) {
            reference.child("Room").child(StaticUser.user.getId()).child("AllManHinhChon").setValue(flag);
        }
    }

    public void ListenSuKien() {

        //Bao ve
            reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(userBaoVe.getId().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getValue(String.class);
                    if (!id.equals("A")) {
                        manv = 4;
                        idBaoVeChon = id;
                        handlerMaSoi.sendEmptyMessage(0);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        //Phu thuy
            reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(userTienTri.getId().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getValue(String.class);
                    if (!id.equals("A")) {
                        manv = 6;
                        idTienTriChon = id;
                        handlerMaSoi.sendEmptyMessage(0);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        //Tho San

            reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(userThoSan.getId().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getValue(String.class);
                    if (!id.equals("A")) {
                        manv = 3;
                        idThoSanChon = id;
                        handlerMaSoi.sendEmptyMessage(0);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        //Ma Soi
            reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(listUserMaSoi.get(0).getId().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getValue(String.class);
                    if (!id.equals("A")) {
                        manv = 1;
                        listidMaSoichon.add(id);
                        handlerMaSoi.sendEmptyMessage(0);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(listUserMaSoi.get(1).getId().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getValue(String.class);
                    if (!id.equals("A")) {
                        manv = 1;
                        listidMaSoichon.add(id);
                        handlerMaSoi.sendEmptyMessage(0);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(listUserMaSoi.get(2).getId().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String id = dataSnapshot.getValue(String.class);
                    if (!id.equals("A")) {
                        manv = 1;
                        listidMaSoichon.add(id);
                        handlerMaSoi.sendEmptyMessage(0);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

    public void ListenIdBiGiet() {
        reference.child("Room").child(StaticUser.user.getId()).child("BangIdChon").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String st = dataSnapshot.getValue(String.class);
                if (!st.equals("A")) {
                    listallchon.add(st);
                    if (listallchon.size() == listuseringame.size()) {
                        manv = 8;
                        handlerMaSoi.sendEmptyMessage(0);
                    }
                    System.out.println(listallchon.size() + "list all chon");
                    System.out.println(listuseringame.size() + "List user game");
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

    //client
    public void LangNgheLuot() {
        reference.child("Room").child(StaticUser.user.getId()).child("ListUserSang").child(StaticUser.user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = dataSnapshot.getValue(Boolean.class);
                if (flag == true) {
                    txtthoigian.setVisibility(View.VISIBLE);
                    DemGiay(30);
                    AddClickUser("BangChonChucNang");
                    if (nhanvat.getManv() == 1) {

                        OntouchUser(textViewAddListDanThuong);
                        //System.out.println("NhanVat ne");
                    } else {
                        OntouchUser(textViewAddListSong);
                    }
                } else {
                    if (nhanvat.getManv() == 1 && flag == false) {
                        OffTouchUser(textViewAddListDanThuong);
                        txtthoigian.setVisibility(View.INVISIBLE);
                    } else {
                        OffTouchUser(textViewAddListSong);
                        txtthoigian.setVisibility(View.INVISIBLE);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void LangNgheChat() {
        reference.child("Room").child(StaticUser.user.getId()).child("AllChat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = dataSnapshot.getValue(Boolean.class);
                if (flag == true&&die==false) {
                    linearLayoutChat.setVisibility(View.VISIBLE);
                    findViewById(R.id.lnrkhungchat).setVisibility(View.VISIBLE);
                    listchat.setVisibility(View.VISIBLE);
                    txtthoigian.setVisibility(View.VISIBLE);
                    DemGiay(10);
                    flagchat = true;
                } else {
                    linearLayoutChat.setVisibility(View.INVISIBLE);
                    findViewById(R.id.lnrkhungchat).setVisibility(View.INVISIBLE);
                    listchat.setVisibility(View.INVISIBLE);
                    txtthoigian.setVisibility(View.INVISIBLE);
                    flagchat = false;
                }
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
                    if (nhanvat.getManv() == 6) {
                        for (NhanVat nv : listNhanVat) {
                            if (text.getUseradd().getId().toString().equals(nv.getId().toString())) {
                                if (nv.getManv() == 1) {
                                    Toast.makeText(Host.this, "day la ma soi", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(Host.this, "day khong phai la soi la ma soi", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                        }
                    }
                    reference.child("Room").child(StaticUser.user.getId()).child(st).child(StaticUser.user.getId()).setValue(hashMap.get(text.getTxtuser().getText().toString()));
                    OffTouchUser(textViewAddListSong);
                }
            });
        }
    }

    public void LangNgheAllManHinh() {
        reference.child("Room").child(StaticUser.user.getId()).child("AllManHinhChon").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = dataSnapshot.getValue(Boolean.class);
                if (flag == true) {
                    if (die == false) {
                        OntouchUser(textViewAddListSong);
                        AddClickUser("BangIdChon");
                    }

                } else {
                    OffTouchUser(textViewAddListSong);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getIdSoiChon() {
        String st = "";
        if (listidMaSoichon.size() == 3) {
            if (listidMaSoichon.get(0).toString().equals(listidMaSoichon.get(1).toString())) {
                st = listidMaSoichon.get(0);
            } else if (listidMaSoichon.get(1).toString().equals(listidMaSoichon.get(2).toString())) {
                st = listidMaSoichon.get(1);
            } else {
                st = listidMaSoichon.get(0);
            }
        }
        st = listidMaSoichon.get(0);
        return st;
    }

    public String getIDBOPhieu() {
        String id = "";
        int max = 1, count = 0;
        {
            for (int i = 0; i < listallchon.size(); i++) {
                count = 0;
                for (int j = i + 1; j < listallchon.size(); j++) {
                    if (listallchon.get(i).toString().equals(listallchon.get(j).toString())) {
                        count++;
                    }
                }
                if (count > max) {
                    max = count;
                    id = listallchon.get(i);
                }
            }
        }
        return id;

    }


    public void XuLiCuoiNgay() {
        String idMaSoiChon = getIdSoiChon();
        if (idMaSoiChon.equals(idBaoVeChon)) {
            return;
        } else if (idMaSoiChon.equals(IDBoPhieu))
            return;
        else if (idMaSoiChon.equals(userThoSan.getId().toString()) && flagGiaLang == false) {
            XoaNhanVat(idMaSoiChon);
            XoaNhanVat(idThoSanChon);
            XoaNhanVatChucNang(idMaSoiChon);
            XoaNhanVatChucNang(idThoSanChon);
            removeListUserInGameID(idMaSoiChon);
            removeListUserInGameID(idThoSanChon);
            if (!idMaSoiChon.equals(IDBoPhieu)) {
                reference.child("Room").child(StaticUser.user.getId()).child("BangDie").child(idMaSoiChon).setValue(idMaSoiChon);
                reference.child("Room").child(StaticUser.user.getId()).child("BangDie").child(idThoSanChon).setValue(idThoSanChon);
            }
        } else {

            XoaNhanVat(idMaSoiChon);
            XoaNhanVatChucNang(idMaSoiChon);
            removeListUserInGameID(idMaSoiChon);
            reference.child("Room").child(StaticUser.user.getId()).child("BangDie").child(idMaSoiChon).setValue(idMaSoiChon);
        }
        if (listUserMaSoi.size() < 1) {
            resetLaiGameMoi();
        } else if (listUserMaSoi.size() >= listUserDanLang.size()) {
            resetLaiGameMoi();
        }
        ResetLaiNgayMoi();
    }

    public void resetLaiGameMoi() {
        reference.child("Room").child(StaticUser.user.getId()).child("OK").setValue(false);
        listUserMaSoi.clear();
        listUserDanLang.clear();
        flagTienTri = false;
        flagBaoVe = false;
        flagThoSan = false;
        flagGiaLang = false;
        listidMaSoichon.clear();
        listallchon.clear();
        listNhanVat.clear();
        idBaoVeChon = "";
        idThoSanChon = "";
        idTienTriChon = "";
        resetAllBang();
        resetLaiBangDie();
        textViewAddListDanThuong.clear();
        textViewAddListSong.clear();
        list.clear();
        listuseringame.clear();
        die = false;
        ResetAnhUser();

    }


    public void XoaNhanVatChucNang(String id) {

        if (flagGiaLang == false && userGiaLang.getId().toString().equals(id)) {
            flagGiaLang = true;
            flagBaoVe = true;
            flagTienTri = true;
            flagThoSan = true;
        }
        if (flagTienTri == false && userTienTri.getId().toString().equals(id)) {
            flagTienTri = true;
        }
        if (flagThoSan == false && userThoSan.getId().toString().equals(id)) {
            flagThoSan = true;
        }
        if (flagBaoVe == false && userBaoVe.getId().toString().equals(id)) {
            flagBaoVe = true;
        }
    }

    public void ResetLaiNgayMoi() {
        //pushNgay();
        XuLyLuot(1, true);
        System.out.println("Toi Xu li luot 1 true");
        listidMaSoichon.clear();
        listallchon.clear();
        idBaoVeChon = "";
        idThoSanChon = "";
        idTienTriChon = "";
        resetAllBang();

    }

    public void LangNgheBangIDChon() {
        reference.child("Room").child(StaticUser.user.getId()).child("IDBiBoPhieu").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String st = dataSnapshot.getValue(String.class);
                String name = "";
                if (st.equals(StaticUser.user.getId())) {
                    linearLayoutChat.setVisibility(View.VISIBLE);
                    findViewById(R.id.lnrkhungchat).setVisibility(View.VISIBLE);
                    listchat.setVisibility(View.VISIBLE);
                } else {
                    if (!st.equals("A")) {
                        findViewById(R.id.lnrkhungchat).setVisibility(View.INVISIBLE);
                        linearLayoutListUser.setVisibility(View.INVISIBLE);
                        btnKhongGiet.setVisibility(View.INVISIBLE);
                        btnGiet.setVisibility(View.INVISIBLE);
                        linearLayoutTreoCo.setVisibility(View.VISIBLE);
                        //.setVisibility(View.INVISIBLE);
                        // btnKhongGiet.setVisibility(View.INVISIBLE);
                        linearLayoutChat.setVisibility(View.VISIBLE);
                        listchat.setVisibility(View.VISIBLE);

                        for (User user : listuser) {
                            if (user.getId().equals(st)) {
                                txtTreoCo.setText(user.getUser());
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void resetAllBang() {
        for (User us : listuser) {
            reference.child("Room").child(StaticUser.user.getId()).child("BangIdChon").child(us.getId().toString()).setValue("A");
            reference.child("Room").child(StaticUser.user.getId()).child("BangChonChucNang").child(us.getId().toString()).setValue("A");
            reference.child("Room").child(StaticUser.user.getId()).child("BangBoPhieu").child(us.getId()).setValue(0);
        }
        reference.child("Room").child(StaticUser.user.getId()).child("IDBiBoPhieu").setValue("A");
    }

    public void resetLaiBangDie()
    {
        for (User us : listuser)
        {
            reference.child("Room").child(StaticUser.user.getId()).child("BangDie").child(us.getId()).setValue("A");
        }
    }

    public  void ResetAnhUser()
    {
        for (TextViewAdd text : textViewAddList)
        {
            text.getUser().setImageResource(R.drawable.image_user);
        }
    }
    //Chưa reset lai game moi
}
