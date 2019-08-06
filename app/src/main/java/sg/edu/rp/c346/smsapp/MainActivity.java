package sg.edu.rp.c346.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnsend;
    EditText etto;
    EditText etcontent;
    private BroadcastReceiver messageReceiver;

    Button btnmsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // binding UI components
        btnsend = findViewById(R.id.buttonmsg);
        etto = findViewById(R.id.editTextTo);
        etcontent = findViewById(R.id.editTextcontent);
        btnmsg = findViewById(R.id.buttonmsg);

        // init messagereceiver
        MessageReceiver messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(messageReceiver, filter);

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check permission
                checkPermission();

                // Get number of Recipients
                String recipient = etto.getText().toString().trim();
                String[] recipients = recipient.split(",");

                // Get message content
                String message = etcontent.getText().toString();

                // Send Message
                SmsManager smsManager = SmsManager.getDefault();
                for (int i = 0; i < recipients.length; i++) {
                    smsManager.sendTextMessage(recipients[i], null, message, null, null);
                }
            }

        });

        btnmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if fields are empty
                validationChecks();

                // Get number of Recipients
                String recipient = etto.getText().toString().trim();

                // Get message content
                String message = etcontent.getText().toString();

                Uri smsUri = Uri.parse("sms:" + recipient);
                Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
                intent.putExtra("address", recipient);
                intent.putExtra("sms_body", message);
                startActivity(intent);
            }
        });
    }

    private void validationChecks() {
        String to = etto.getText().toString().trim();
        String content = etcontent.getText().toString().trim();

        if (to.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "Enter all fields!", Toast.LENGTH_SHORT).show();
        } else {
            if (to.isEmpty()) {
                Toast.makeText(this, "Enter a recipient.", Toast.LENGTH_SHORT).show();
            }

            if (content.isEmpty()) {
                Toast.makeText(this, "Enter content.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkPermission () {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        this.unregisterReceiver(messageReceiver);
    }
}
