package com.tapbi.demomessage;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.demomessage.addapter.MessageAdapter;
import com.tapbi.demomessage.dto.ItemContact;
import com.tapbi.demomessage.dto.ItemMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MessageAdapter.OnCallBack {
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<ItemMessage> messageList ,messageListLoad;
    private List<ItemContact> contactList;
    private Button btn_add_message;
    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private boolean checkPermiss =false;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        registerReceiver(broadcastReceiver, new IntentFilter("SMS_RECEIVED_ACTION"));
    }

    public void getAllSMS(){
        int i = 0;
        Uri uriSMSURI = Uri.parse("content://sms/");
        Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);
        while (cur != null && cur.moveToNext()) {
            String address = cur.getString(cur.getColumnIndex("address"));
            String body = cur.getString(cur.getColumnIndexOrThrow("body"));
            Log.d("SMS: ",i + " : "+  address );
            i++;
        }

        if (cur != null) {
            cur.close();
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equalsIgnoreCase("SMS_RECEIVED_ACTION")){
                initView();
            }
        }
    } ;



   @RequiresApi(api = Build.VERSION_CODES.M)
   private void checkPermission(){
       int readSMSPermission =  ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
       int readContactsPermission =  ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
       int receiveSMSPermission =  ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);

       if ( readSMSPermission != PackageManager.PERMISSION_GRANTED ||
               readContactsPermission != PackageManager.PERMISSION_GRANTED ||
               receiveSMSPermission != PackageManager.PERMISSION_GRANTED ) {

               ActivityCompat.requestPermissions(
                       MainActivity.this, new String[]{Manifest.permission.READ_SMS,
                               Manifest.permission.READ_CONTACTS, Manifest.permission.RECEIVE_SMS}, REQUEST_CODE_ASK_PERMISSIONS);
       }
       else {
           contactList = getListContacts();
           if (contactList != null) {
               initView();
               getAllSMS();
           }
       }
//           ActivityCompat.requestPermissions(
//                   MainActivity.this, new String[]{Manifest.permission.READ_SMS,
//                           Manifest.permission.READ_CONTACTS, Manifest.permission.RECEIVE_SMS}, REQUEST_CODE_ASK_PERMISSIONS);
   }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    contactList = getListContacts();
                    if (contactList != null) {
                        initView();
                        getAllSMS();
                    }
                }
        }
    }


    private void initView() {
        messageList =  getSMS();
        recyclerView = findViewById(R.id.rv_message);
        recyclerView.setHasFixedSize(true);
        messageAdapter = new MessageAdapter( this, messageList);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btn_add_message = findViewById(R.id.btn_add_message);
        btn_add_message.setOnClickListener(this);
    }

    private List<ItemContact> getListContacts() {
        List<ItemContact> list = new ArrayList<ItemContact>();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        phones.moveToFirst();
        ItemContact itemContact1 = new ItemContact();
        String name1 = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        String phoneNumber1 = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

        phoneNumber1 = convertNumber(phoneNumber1);
        itemContact1.setName(name1);
        itemContact1.setNumber(phoneNumber1);
        list.add(itemContact1);

        phones.moveToNext();
        while (!phones.isAfterLast()) {
            ItemContact itemContact = new ItemContact();
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phoneNumber = convertNumber(phoneNumber);

          itemContact.setName(name);
          itemContact.setNumber(phoneNumber);

          if (checkContacts(list, name)){
              list.add(itemContact);
          }
          phones.moveToNext();
        }
        return list;
    }

    public List<ItemMessage> getSMS() {
        List<ItemMessage> list = new ArrayList<ItemMessage>();
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);

        cursor.moveToFirst();
        ItemMessage itemMessage1 = new ItemMessage();
        itemMessage1.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
        for (ItemContact x : contactList) {
            if (convertAddress(cursor.getString(cursor.getColumnIndexOrThrow("address"))).equals(x.getNumber())) {
                itemMessage1.setName(x.getName());
                itemMessage1.setNumber(x.getNumber());
            } else {
                itemMessage1.setName(convertAddress(cursor.getString(cursor.getColumnIndexOrThrow("address"))));
                itemMessage1.setNumber(convertAddress(cursor.getString(cursor.getColumnIndexOrThrow("address"))));
            }
        }
        itemMessage1.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
        itemMessage1.setContent(cursor.getString(cursor.getColumnIndexOrThrow("body")));
        itemMessage1.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
        itemMessage1.setRead(cursor.getString(cursor.getColumnIndexOrThrow("read")));
        list.add(itemMessage1);

        cursor.moveToNext();
        while (!cursor.isAfterLast()) {
            boolean equal = true;
            ItemMessage itemMessage = new ItemMessage();
            itemMessage.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));

            for (ItemContact x : contactList) {
                if (convertAddress(cursor.getString(cursor.getColumnIndexOrThrow("address"))).equals(x.getNumber())) {
                    itemMessage.setName(x.getName());
                    itemMessage.setNumber(x.getNumber());
                    equal = false;
                }
            }
            if (equal == true) {
                itemMessage.setName(convertAddress(cursor.getString(cursor.getColumnIndexOrThrow("address"))));
                itemMessage.setNumber(convertAddress(cursor.getString(cursor.getColumnIndexOrThrow("address"))));
            }
            itemMessage.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            itemMessage.setContent(cursor.getString(cursor.getColumnIndexOrThrow("body")));
            itemMessage.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
            itemMessage.setRead(cursor.getString(cursor.getColumnIndexOrThrow("read")));

            if (checkMessage(list, itemMessage.getName())) {
                list.add(itemMessage);
            }
            cursor.moveToNext();

        }
        return list;
    }

    public String convertAddress(String s){
        String s1 = s;
        if (s.charAt(0) == '+' && s.charAt(1) == '8' && s.charAt(2) == '4'){
            s1 = '0' + s.substring(3);
        }
        return s1;
    }
    public String convertNumber(String s){
        String s1 = s;
        if (s.length()>8) {
            if (s.charAt(0) == '+' && s.charAt(1) == '8' && s.charAt(2) == '4') {
                s1 = '0' + s.substring(3);
            }
            if (s.charAt(3) == '-' && s.charAt(7) == '-') {
                s1 = s.substring(0, 3) + s.substring(4, 7) + s.substring(8);
            }
        }

        return s1;
    }

    public boolean checkMessage(List<ItemMessage> list, String address){
        for (int i =0 ; i< list.size(); i++){
            if ((list.get(i).getName()).equals(address)){
                return false;
            }

        }
        return true;
    }
    public boolean checkContacts(List<ItemContact> list, String name){
        for (int i =0 ; i< list.size(); i++){
            if ((list.get(i).getName()).equals(name)){
                return false;
            }
        }
        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add_message:
                Intent intent = new Intent(this, SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("messagelist", (Serializable) messageList);
                bundle.putSerializable("contactlist1", (Serializable) contactList);
                bundle.putSerializable("contactlist2", (Serializable) contactList);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(MainActivity.this, SendMessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("messagelist", (Serializable) messageList);
        intent.putExtras(bundle);
        intent.putExtra("position",position);
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if ((getBaseContext().checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED )
                && (getBaseContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)) {
            contactList = getListContacts();
            if (contactList != null) {
                initView();
            }
        }
    }
}
