package com.tapbi.demomessage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.demomessage.addapter.ContactAdapter;
import com.tapbi.demomessage.addapter.ListMessageAdapter;
import com.tapbi.demomessage.dto.ItemContact;
import com.tapbi.demomessage.dto.ItemMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, ContactAdapter.OnCallBack {
    private RecyclerView rvContact, rvListMessage;
    private List<ItemContact> contactList, contactListUpdate;
    private List<ItemMessage> messageList, itemMessageList;
    private ContactAdapter contactAdapter;
    private ListMessageAdapter listMessageAdapter;
    private EditText ed_search, ed_send;
    private LinearLayout ln_send_message;
    private ImageButton ib_back, ib_send_message;
    final int REQUEST_CODE_ASK_PERMISSIONS_SENDSMS  =112;
    private boolean sendSMS = false;
    private String name, address;
    private BroadcastReceiver sendBroadcastReceiver;
    private BroadcastReceiver deliveryBroadcastReceiver;
    private boolean isReceiverRegistered = false;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_search);
        // getData();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        messageList = (List<ItemMessage>)bundle.getSerializable("messagelist") ;
        contactListUpdate =  (List<ItemContact>)bundle.getSerializable("contactlist2");
        contactList = (List<ItemContact>)bundle.getSerializable("contactlist1");
        checkPermission();
        initView();
        checkProcessMessage();

    }


    public void checkProcessMessage(){
        sendBroadcastReceiver = new BroadcastReceiver()
        {
            public void onReceive(Context arg0, Intent arg1)
            {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS Sent", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        deliveryBroadcastReceiver = new BroadcastReceiver()
        {
            public void onReceive(Context arg0, Intent arg1)
            {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS Delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        registerReceiver(deliveryBroadcastReceiver, new IntentFilter(DELIVERED));
        registerReceiver(sendBroadcastReceiver , new IntentFilter(SENT));
        isReceiverRegistered = true;
    }

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        REQUEST_CODE_ASK_PERMISSIONS_SENDSMS);
        }
        else {
            sendSMS = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS_SENDSMS : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    sendSMS = true;
                }
            }
        }
    }


    private void initView() {
        rvContact = findViewById(R.id.rv_contact);
        loadContactsList();

        rvListMessage = findViewById(R.id.rv_list_message);
        ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(this);

        ln_send_message = findViewById(R.id.ln_send_message);
        ln_send_message.setVisibility(View.GONE);

        ed_send = findViewById(R.id.ed_message);
        ed_send.setOnClickListener(this);
        ed_send.setScroller(new Scroller(this));
        ed_send.setMaxLines(4);
        ed_send.setVerticalScrollBarEnabled(true);
        ed_send.setMovementMethod(new ScrollingMovementMethod());
        
        ib_send_message = findViewById(R.id.ib_send_message);
        ib_send_message.setOnClickListener(this);

        ed_search = findViewById(R.id.ed_search);
        ed_search.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(ed_search, InputMethodManager.SHOW_IMPLICIT);
        
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        ed_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.

                        String number = ed_search.getText().toString();
                        if (isNumeric(number)){
                           ln_send_message.setVisibility(View.VISIBLE);
                           rvContact.setVisibility(View.GONE);
                            rvListMessage.setVisibility(View.VISIBLE);
                            itemMessageList = getListMessage(ed_search.getText().toString());
                            loadMessageList();
                            ed_send.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(ed_send, InputMethodManager.SHOW_IMPLICIT);
                        }

                        return true; // consume.
                    }
                }
                return false; // pass on to other listeners.
            }
        });
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    private void filter(String name) {
        //new array list that will hold the filtered data
        ArrayList<ItemContact> filterdNames = new ArrayList<ItemContact>();

        //looping through existing elements
        for (ItemContact s : contactList) {
            //if the existing elements contains the search input
            if (s.getName().toLowerCase().contains(name.toLowerCase()) || s.getNumber().toLowerCase().contains(name.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }
        //calling a method of the adapter class and passing the filtered list
        if (name.length() == 0){
            rvListMessage.setVisibility(View.GONE);
            ln_send_message.setVisibility(View.GONE);
            rvContact.setVisibility(View.VISIBLE);
            loadContactsList();
        }
        contactAdapter.filterList(filterdNames);
        contactListUpdate.clear();
        contactListUpdate.addAll(filterdNames);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_back:
                finish();
                break;
            case R.id.ib_send_message:
                if (ed_search.getText().toString().equals("") || ed_send.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "please type phone number", Toast.LENGTH_LONG).show();
                }
                else if (sendSMS){
                    name = ed_search.getText().toString();
                    address = name;
                    String phone = name;
                    for (ItemMessage x : messageList) {
                        if (name.equals(x.getName())) {
                            address = x.getAddress();
                            phone = x.getAddress();
                        }
                    }
                    String content = ed_send.getText().toString();
                    doSend(phone, content);
                    Intent intentSends = new Intent(SearchActivity.this, SendMessageActivity.class);
                    intentSends.putExtra("name",name);
                    intentSends.putExtra("address",address);
                    intentSends.putExtra("phone",phone);
                    startActivity(intentSends);
                    finish();
               }
                break;
            default:
                break;
        }

    }

    private void loadContactsList(){
        rvContact.setHasFixedSize(true);
        contactAdapter = new ContactAdapter(contactList, this);
        rvContact.setAdapter(contactAdapter);
        rvContact.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadMessageList() {
        listMessageAdapter = new ListMessageAdapter(itemMessageList);
        // listMessageAdapter.setData(itemMessageList);
        rvListMessage.setAdapter(listMessageAdapter);
        rvListMessage.setLayoutManager(new LinearLayoutManager(this));

        rvListMessage.post(new Runnable() {
            @Override
            public void run() {
                rvListMessage.scrollToPosition(rvListMessage.getAdapter().getItemCount() -1);
            }
        })  ;
    }


    public List<ItemMessage> getListMessage(String address){
        List<ItemMessage> list = new ArrayList<ItemMessage>();
        Uri myMessage = Uri.parse("content://sms/");
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(myMessage,
                new String[]{"address", "body","date","date_sent","type"},
                "address = '" + address + "'", null, "date" + " ASC");
        if (c.getCount()>0) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                ItemMessage itemMessage = new ItemMessage();
                itemMessage.setName(c.getString(c.getColumnIndexOrThrow("address")));
                itemMessage.setContent(c.getString(c.getColumnIndexOrThrow("body")));

                itemMessage.setDate(formatDate(c.getString(c.getColumnIndexOrThrow("date"))));

                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    itemMessage.setFolder("inbox");
                } else {
                    itemMessage.setFolder("sent");
                }

                list.add(itemMessage);
                c.moveToNext();
            }
            c.close();
        }
        return list;
    }

    public String formatDate(String date){
        Date dateformat = new Date(Long.parseLong(date));
        String formattedDate = new SimpleDateFormat("MM/dd/yyyy hh:mm").format(dateformat);
        return formattedDate;
    }


    @Override
    public void onItemClicked(int position) {

        String address = contactListUpdate.get(position).getName();

        for (ItemMessage x : messageList) {
            if (contactListUpdate.get(position).getNumber().equals(x.getNumber())) {
               address = x.getAddress();
            }
        }

        ed_search.setText(contactListUpdate.get(position).getName());
        rvContact.setVisibility(View.GONE);
        rvListMessage.setVisibility(View.VISIBLE);
        itemMessageList = getListMessage(address);
        loadMessageList();
        ln_send_message.setVisibility(View.VISIBLE);
        ed_send.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(ed_send, InputMethodManager.SHOW_IMPLICIT);
    }


    private void doSend(String phoneNo, String message ) {

        if (phoneNo.length() > 0 && message.length() > 0) {
            TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            int simState = telMgr.getSimState();
            switch (simState) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    displayAlert();
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    // do something
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    // do something
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    // do something
                    break;
                case TelephonyManager.SIM_STATE_READY:
                    // do something
                    sendSMS(phoneNo, message); // method to send message
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    // do something
                    break;
            }

        } else {
            Toast.makeText(getBaseContext(),
                    "Please enter both phone number and message.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void displayAlert(){
        new AlertDialog.Builder(SearchActivity.this)
                .setMessage("Sim card not available")
                .setCancelable(false)
                // .setIcon(R.drawable.alert)
                .setPositiveButton("ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Log.d("I am inside ok", "ok");
                                dialog.cancel();
                            }
                        })

                .show();

    }

    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isReceiverRegistered){
            unregisterReceiver(deliveryBroadcastReceiver);
            unregisterReceiver(sendBroadcastReceiver);
            isReceiverRegistered = false;
        }
    }
}
