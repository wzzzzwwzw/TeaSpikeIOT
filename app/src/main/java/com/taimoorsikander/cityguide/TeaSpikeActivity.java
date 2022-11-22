package com.taimoorsikander.cityguide;

import static java.io.File.separator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


import okhttp3.OkHttpClient;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import com.taimoorsikander.cityguide.device.ISpikeRESTAPIService;
import com.taimoorsikander.cityguide.models.TelemetriaViewModel;
import com.taimoorsikander.cityguide.pojo.Co2;
import com.taimoorsikander.cityguide.pojo.Humidity;
import com.taimoorsikander.cityguide.pojo.Light;
import com.taimoorsikander.cityguide.pojo.Measurement;
import com.taimoorsikander.cityguide.pojo.Sensors;
import com.taimoorsikander.cityguide.pojo.SoilTemp1;
import com.taimoorsikander.cityguide.pojo.SoilTemp2;
import com.taimoorsikander.cityguide.pojo.Temperature;

public class TeaSpikeActivity extends Activity {
    static String LOG_TAG = "btb";
    public static final int NEW_ACTIVITY_REQUEST_CODE = 2022;

    private ISpikeRESTAPIService apiService;
    private static final String API_LOGIN_POST = "https://thingsboard.cloud/api/auth/"; // Base url to obtain token
    private static final String API_BASE_GET = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/"; // Base url to obtain data
    private static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdHVkZW50dXBtMjAyMkBnbWFpbC5jb20iLCJ1c2VySWQiOiI4NDg1OTU2MC00NzU2LTExZWQtOTQ1YS1lOWViYTIyYjlkZjYiLCJzY29wZXMiOlsiVEVOQU5UX0FETUlOIl0sImlzcyI6InRoaW5nc2JvYXJkLmNsb3VkIiwiaWF0IjoxNjY5MTE5ODA0LCJleHAiOjE2NjkxNDg2MDQsImZpcnN0TmFtZSI6IlN0dWRlbnQiLCJsYXN0TmFtZSI6IlVQTSIsImVuYWJsZWQiOnRydWUsImlzUHVibGljIjpmYWxzZSwiaXNCaWxsaW5nU2VydmljZSI6ZmFsc2UsInByaXZhY3lQb2xpY3lBY2NlcHRlZCI6dHJ1ZSwidGVybXNPZlVzZUFjY2VwdGVkIjp0cnVlLCJ0ZW5hbnRJZCI6ImUyZGQ2NTAwLTY3OGEtMTFlYi05MjJjLWY3NDAyMTlhYmNiOCIsImN1c3RvbWVySWQiOiIxMzgxNDAwMC0xZGQyLTExYjItODA4MC04MDgwODA4MDgwODAifQ.kxXcPGYEuPAnqt6SDeMb1RvzZvo2c51fagZG4Pjh_nvwqOubooL6AWnyvi0D3VoPgcX1e92x1_3ytHo_ELSRgg";
    private static final String BEARER_TOKEN = "Bearer " + TOKEN;
    private static final String DEVICE_ID = "cf87adf0-dc76-11ec-b1ed-e5d3f0ce866e";
    private static final String USER_THB = "studentupm2022@gmail.com";
    private static final String PASS_THB = "student";

    TelemetriaViewModel telemetriaViewModel;
    private String sAuthBearerToken = "";


    private TextView tvRespuesta;
    private ImageView ivRespuesta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tea_spike);
        tvRespuesta = (TextView) findViewById(R.id.tvRespuesta);
        ivRespuesta = (ImageView) findViewById(R.id.ivRespuesta);


        // btb added for retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_GET)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getUnsafeOkHttpClient())
                .build();

        apiService = retrofit.create(ISpikeRESTAPIService.class);


    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    //
    // Al hacer click sobre la lupa
    // Ver  activity_actividad_principal.xml
    //
    public void getTelemetries(View v) throws NoSuchAlgorithmException {

        String keys = "co2,humidity,light,soilTemp1,soilTemp2,temperature";
        String iniTimestamp = "972684000000";// 28/10/2000
        String endTimestamp = "1666908000000";

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(API_BASE_GET)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ISpikeRESTAPIService iApi = retrofit.create(ISpikeRESTAPIService.class);
        Log.i(LOG_TAG, " request params: |" + BEARER_TOKEN + "|" + DEVICE_ID + "|" + keys + "|" + iniTimestamp + "|" + endTimestamp);
        Call<Sensors> call = iApi.getTelemetries(BEARER_TOKEN, DEVICE_ID, keys, iniTimestamp, endTimestamp);

        call.enqueue(new Callback<Sensors>() {
            @Override
            public void onResponse(Call<Sensors> call, Response<Sensors> response) {
                Sensors sensorsList = response.body();

                if (null != sensorsList) {

                    List<Co2> lCo2 = sensorsList.getCo2();
                    List<Humidity> lHum = sensorsList.getHumidity();
                    List<Light> lLig = sensorsList.getLight();
                    List<SoilTemp1> lST1 = sensorsList.getSoilTemp1();
                    List<SoilTemp2> lST2 = sensorsList.getSoilTemp2();
                    List<Temperature> lTem = sensorsList.getTemperature();

                    DateFormat df = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                    }

                    for (int i = 0; i < lCo2.size() - 1 - lCo2.size() + 2; i++) {
                        Date currentDate = new Date(lCo2.get(i).getTs());
                        String sTs = df.format(currentDate);
                        tvRespuesta.append(sTs);
                        tvRespuesta.append(separator);
                        tvRespuesta.append(".[Co2 : " + lCo2.get(i).getTs() + "] " + "|" + String.valueOf(lCo2.get(i).getValue()) + "]");
                        tvRespuesta.append(".[Humidity : " + lHum.get(i).getTs() + "] " + "|" + String.valueOf(lHum.get(i).getValue()) + "]");
                        tvRespuesta.append(".[Light: " + lLig.get(i).getTs() + "] " + "|" + String.valueOf(lLig.get(i).getValue()) + "]");
                        tvRespuesta.append(".[SoilTemp1 : " + lST1.get(i).getTs() + "] " + "|" + String.valueOf(lST1.get(i).getValue()) + "]");
                        tvRespuesta.append(".[SoilTemp2 : " + lST2.get(i).getTs() + "] " + "|" + String.valueOf(lST2.get(i).getValue()) + "]");
                        tvRespuesta.append(".[Temperature : " + lTem.get(i).getTs() + "] " + "|" + String.valueOf(lTem.get(i).getValue()) + "]");


                    }

                } else {
                    tvRespuesta.setText(getString(R.string.strError));
                    Log.i(LOG_TAG, getString(R.string.strError));
                }
            }

            @Override
            public void onFailure(Call<Sensors> call, Throwable t) {
                Toast.makeText(
                        getApplicationContext(),
                        "ERROR: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
                Log.e(LOG_TAG, t.getMessage());
            }
        });


    }


    public void getLastTelemetry(View V) {
        String keys = "co2,humidity,light,soilTemp1,soilTemp2,temperature";
        String useStrictDataTypes = "false";


        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(API_BASE_GET)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ISpikeRESTAPIService iApi = retrofit.create(ISpikeRESTAPIService.class);
        Log.i(LOG_TAG, " request params: |" + BEARER_TOKEN + "|" + DEVICE_ID + "|" + keys + "|" + useStrictDataTypes);
        Call<Measurement> call = iApi.getLastTelemetry(BEARER_TOKEN, DEVICE_ID, keys, useStrictDataTypes);


        call.enqueue(new Callback<Measurement>() {
            @Override
            public void onResponse(Call<Measurement> call, Response<Measurement> response) {
                Toast.makeText(TeaSpikeActivity.this, "Data posted to API", Toast.LENGTH_SHORT).show();
                Measurement lm = response.body();
                assert lm != null;
                List<Co2> lCo2 = lm.getCo2();
                String responseString = "Response Code : " + response.code();

                Log.i(LOG_TAG, " response: " + responseString);
                @SuppressLint("SimpleDateFormat") DateFormat df = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                }

                Date currentDate = new Date(lCo2.get(0).getTs());
                String sTs = df.format(currentDate);
                tvRespuesta.append("timestamp : " + sTs + "\n");
                tvRespuesta.append(separator);
                tvRespuesta.append("Co2 : " + lm.getCo2().get(0).getValue() + "\n" +
                        " Humidity : " + lm.getHumidity().get(0).getValue() + "\n" +
                        " Light : " + lm.getLight().get(0).getValue() + "\n" +
                        " Temperature  : " + lm.getTemperature().get(0).getValue() + "\n" +
                        " SoilTemp1 : " + lm.getSoilTemp1().get(0).getValue() + "\n" +
                        " SoilTemp2 : " + lm.getSoilTemp2().get(0).getValue() + "\n")
                ;

                Log.i(LOG_TAG, " response.body: " + lm.getCo2().get(0).getValue());


            }

            @Override
            public void onFailure(Call<Measurement> call, Throwable t) {
                Log.e(LOG_TAG, " error message: " + t.getMessage());
            }
        });

    }

    public void clear(View v) {
        tvRespuesta.setText("");
    }
}





