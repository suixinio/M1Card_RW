package com.init16.m1card_rw;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 佳诚 on 2016/9/24.
 */

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private MifareClassic mfc;
    private Intent intent;
    private Map<String, String> map = new HashMap();
    private String Qust_KEYA;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn_AddMoney);
        mContext = this;

        // check NFC is supports
        NfcCheck();

        // init key
        InitKey();

        // init NFC
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Write();
                } catch (IOException e) {
                    Toast.makeText(mContext, "Sorry , Write false", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void InitKey() {
        //Initialization real UID ---- KEYA
        map.put("UID", "KEYA");
    }

    /**
     * NFC Check
     */
    private void NfcCheck() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(mContext, "Sorry", Toast.LENGTH_SHORT).show();
        } else {
            if (!mNfcAdapter.isEnabled()) {
                Intent setNfc = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(setNfc);
                return;
            }
        }
        System.out.println("nfc= NFCcheck");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("nfc= onResume");
        enableForegroudDispatch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("nfc= onPause");
        disableForgroudDisaptch();
    }

    private void enableForegroudDispatch() {
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    private void disableForgroudDisaptch() {
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("nfc= onNewIntent");
        this.intent = intent;

        //Get card UID
        String id = NFCUtil.byte2HexString(MifareClassic.get((Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)).getTag().getId());
//        String key2 = id.substring(0,);
        Toast.makeText(mContext, "(Uid = " + id + ")", Toast.LENGTH_SHORT).show();
        CheckId(id);
    }

    /**
     * Check UID , UID  ---  KEY
     * @param id
     */
    private void CheckId(String id) {
        System.out.println("nfc key = "+ map.get(id));
        Qust_KEYA = map.get(id);
    }

    /**
     * NFC Read
     * @throws IOException
     */
    public void Write() throws IOException {
        if (intent != null) {
            if (Qust_KEYA != null) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                mfc = MifareClassic.get(tag);
                mfc.connect();
                if (mfc.authenticateSectorWithKeyA(3, NFCUtil.string2byte(Qust_KEYA))) {
                    // KEYA is true --> write data
                    // data ---> real block data
                    mfc.writeBlock(14, NFCUtil.string2byte("data"));
                    Toast.makeText(this, "Write successfully", Toast.LENGTH_SHORT).show();
//            mfc.writeBlock(2.);
//            mfc.writeBlock(2,);
                } else {
                    Toast.makeText(this, "KEY Error", Toast.LENGTH_SHORT).show();
                }
                mfc.close();
            } else {
                Toast.makeText(mContext, "This Card is illegal.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * It is no use.
     * test hack
     * @throws IOException
     */
    private void test02() throws IOException {
        boolean flag = true;
        String keystr = "111111114474";
        long key = Long.parseLong(keystr, 16);
        while (flag) {
            if (mfc.authenticateSectorWithKeyA(3, NFCUtil.string2byte(keystr))) {
                flag = false;
            } else {
                key++;
                keystr = String.valueOf(Long.toHexString(key));
                System.out.println("nfc data -->" + keystr);
            }
        }
    }

}
