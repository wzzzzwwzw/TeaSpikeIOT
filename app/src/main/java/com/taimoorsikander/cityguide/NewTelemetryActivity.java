package com.taimoorsikander.cityguide;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class NewTelemetryActivity extends AppCompatActivity {

    static String LOG_TAG = "btb";

    public static final String PARAM_TST = "es.upm.btb.telemetrypersistence.timestamp";
    public static final String PARAM_CO2 = "es.upm.btb.telemetrypersistence.co2";
    public static final String PARAM_HUM = "es.upm.btb.telemetrypersistence.humidity";
    public static final String PARAM_LIG = "es.upm.btb.telemetrypersistence.light";
    public static final String PARAM_ST1 = "es.upm.btb.telemetrypersistence.soiltemp1";
    public static final String PARAM_ST2 = "es.upm.btb.telemetrypersistence.soiltemp2";
    public static final String PARAM_ATP = "es.upm.btb.telemetrypersistence.temperature";

    private EditText etTim, etCO2, etHum, etLig, etSt1, etSt2, etTem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_telemetry);

        etTim = findViewById(R.id.etTimestamp);
        etCO2 = findViewById(R.id.etCO2);
        etHum = findViewById(R.id.etHumid);
        etLig = findViewById(R.id.etLux);
        etSt1 = findViewById(R.id.etSoilTem1);
        etSt2 = findViewById(R.id.etSoilTem2);
        etTem = findViewById(R.id.etTemp);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(etTim.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                replyIntent.putExtra(PARAM_TST, etTim.getText().toString());
                replyIntent.putExtra(PARAM_CO2, etCO2.getText().toString());
                replyIntent.putExtra(PARAM_HUM, etHum.getText().toString());
                replyIntent.putExtra(PARAM_LIG, etLig.getText().toString());
                replyIntent.putExtra(PARAM_ST1, etSt1.getText().toString());
                replyIntent.putExtra(PARAM_ST2, etSt2.getText().toString());
                replyIntent.putExtra(PARAM_ATP, etTem.getText().toString());
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
    }

}
