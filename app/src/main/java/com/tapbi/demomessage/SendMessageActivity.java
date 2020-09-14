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
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.demomessage.addapter.ListMessageAdapter;
import com.tapbi.demomessage.dto.ItemMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SendMessageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_ASK_PERMISSIONS_SENDSMS = 111;
    private ImageButton ib_back_message, ib_send_message,ib_add_image;
    private TextView tv_name_contact;
    private EditText ed_send_message;
    private List<ItemMessage> messageList, itemMessageList;
    private RecyclerView rv_list_message;
    private ListMessageAdapter listMessageAdapter;
    private String number ;
    private String name ;
    private String address;
    private boolean sendSMS, isSend ;
    private  int position;
    private BroadcastReceiver sendBroadcastReceiver;
    private BroadcastReceiver deliveryBroadcastReceiver;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
         checkPermission();
        initView();
        checkProcessMessage();
        if (sendSMS) {
            registerReceiver(broadcastReceiver, new IntentFilter("SMS_RECEIVED_ACTION"));
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equalsIgnoreCase("SMS_RECEIVED_ACTION")){
                address = intent.getStringExtra("sender");
                String message = intent.getStringExtra("message");
                loadAllListMessage();
            }
        }
    } ;
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMS = true;
                }
            }
        }
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
                        loadAllListMessage();

                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        registerReceiver(deliveryBroadcastReceiver, new IntentFilter(DELIVERED));
        registerReceiver(sendBroadcastReceiver , new IntentFilter(SENT));
    }

    private void initView() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position",-1);
        Bundle bundle = intent.getExtras();
        messageList = (List<ItemMessage>)bundle.getSerializable("messagelist") ;

        ib_back_message = findViewById(R.id.ib_back_message);
        ib_back_message.setOnClickListener(this);

        ib_send_message = findViewById(R.id.ib_send_message);
        ib_send_message.setOnClickListener(this);

        ib_add_image = findViewById(R.id.ib_add_image);
        ib_add_image.setOnClickListener(this);

        ed_send_message = findViewById(R.id.ed_message);
        ed_send_message.setScroller(new Scroller(this));
        ed_send_message.setMaxLines(4);
        ed_send_message.setVerticalScrollBarEnabled(true);
        ed_send_message.setMovementMethod(new ScrollingMovementMethod());

        tv_name_contact = findViewById(R.id.tv_name_contact);

       if (position !=-1){
           tv_name_contact.setText(messageList.get(position).getName());
           address = messageList.get(position).getAddress();
           number = messageList.get(position).getNumber();
       }
       else {
           name = intent.getStringExtra("name");
           number = intent.getStringExtra("phone");
           tv_name_contact.setText(name);
           address =  intent.getStringExtra("address");
       }


       loadAllListMessage();
    }

    private void loadAllListMessage(){
        rv_list_message = findViewById(R.id.rv_message);
        itemMessageList = getListMessage(address);
        listMessageAdapter = new ListMessageAdapter(itemMessageList);
       // listMessageAdapter.setData(itemMessageList);
        rv_list_message.setAdapter(listMessageAdapter);
        rv_list_message.setLayoutManager(new LinearLayoutManager(this));

        rv_list_message.post(new Runnable() {
            @Override
            public void run() {
                rv_list_message.scrollToPosition(rv_list_message.getAdapter().getItemCount() -1);
            }
        })  ;
    }


    public List<ItemMessage> getListMessage(String address){
        List<ItemMessage> list = new ArrayList<ItemMessage>();
            Uri myMessage = Uri.parse("content://sms/");
            ContentResolver cr = getContentResolver();
           Cursor c  = cr.query(myMessage,
                   new String[]{"address", "body", "date", "date_sent", "type"},
                   "address = '" + address +"'", null, "date" + " ASC");
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_back_message:
                Intent intent = new Intent(this, MainActivity.class );
                startActivity(intent);
                finish();
                break;
            case R.id.ib_send_message:
                if (sendSMS){
                    doSend();
                    ed_send_message.setText("");
                }
                break;
            case R.id.ib_add_image:
               //load image
                break;
        }

    }



    private void doSend() {
        String phoneNo = number;
        String message = ed_send_message.getText().toString();;
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
            new AlertDialog.Builder(SendMessageActivity.this)
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
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("SMS_RECEIVED_ACTION");
        registerReceiver(broadcastReceiver , filter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            if(broadcastReceiver != null){
                unregisterReceiver(broadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void onStop()
    {
        unregisterReceiver(sendBroadcastReceiver);
        unregisterReceiver(deliveryBroadcastReceiver);
        super.onStop();
    }

}
