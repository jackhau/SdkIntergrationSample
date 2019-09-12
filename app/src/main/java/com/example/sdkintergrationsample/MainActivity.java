package com.example.sdkintergrationsample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shangrila.merchantreward.RewardConfirmation;
import com.shangrila.merchantreward.RewardResult;
import com.shangrila.merchantreward.RewardSDK;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    static final int USING_MPOS = 1001;
    EditText amount, receiptNumber, posId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button shopper = findViewById(R.id.shopper);
        Button scanMember = findViewById(R.id.scan_member);
        Button eod = findViewById(R.id.eod);
        Button history = findViewById(R.id.history);
        amount = findViewById(R.id.amount);
        receiptNumber = findViewById(R.id.receipt_number);
        posId = findViewById(R.id.pos_id);

        scanMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(RewardSDK.ScreenMode.SCANMEMBER);
            }
        });

        shopper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(RewardSDK.ScreenMode.SCANREWARD);
            }
        });

        eod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                starter(RewardSDK.ScreenMode.ENDOFDAY);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                starter(RewardSDK.ScreenMode.HISTORY);
            }
        });
    }

    private void starter(@RewardSDK.ScreenMode String ScreenMode) {
        try {
            RewardSDK.start(MainActivity.this, ScreenMode);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Terminal app not exists", Toast.LENGTH_SHORT).show();
        }
    }

    private void start(@RewardSDK.ScreenMode String ScreenMode) {
        if (TextUtils.isEmpty(amount.getText().toString())) {
            Toast.makeText(MainActivity.this, "Please enter amount", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            RewardSDK.start(MainActivity.this,
                    ScreenMode,
                    Double.valueOf(amount.getText().toString()),
                    receiptNumber.getText().toString(),
                    getTime(),
                    posId.getText().toString(),
                    USING_MPOS);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Terminal app not exists", Toast.LENGTH_SHORT).show();
        }
    }

    private String getTime() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        return df.format(new Date());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USING_MPOS) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    // Get result
                    RewardResult result = RewardSDK.getResult(data);

                    // Check status
                    if (result.getStatus().equals(RewardSDK.Result.SUCCESS)) {
                        // Do something when success
                        RewardConfirmation rewardConfirmation = result.getRewardConfirmation();
                        Toast.makeText(this, rewardConfirmation.getTotalAmount().toString(), Toast.LENGTH_SHORT).show();
                    } else if (result.getStatus().equals(RewardSDK.Result.ERROR)) {
                        // Do something when error
                        String errorMessage = result.getError();
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
