package com.taimoorsikander.cityguide;

import static java.io.File.separator;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;


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
    private static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdHVkZW50dXBtMjAyMkBnbWFpbC5jb20iLCJ1c2VySWQiOiI4NDg1OTU2MC00NzU2LTExZWQtOTQ1YS1lOWViYTIyYjlkZjYiLCJzY29wZXMiOlsiVEVOQU5UX0FETUlOIl0sImlzcyI6InRoaW5nc2JvYXJkLmNsb3VkIiwiaWF0IjoxNjY5MTQ1MTE0LCJleHAiOjE2NjkxNzM5MTQsImZpcnN0TmFtZSI6IlN0dWRlbnQiLCJsYXN0TmFtZSI6IlVQTSIsImVuYWJsZWQiOnRydWUsImlzUHVibGljIjpmYWxzZSwiaXNCaWxsaW5nU2VydmljZSI6ZmFsc2UsInByaXZhY3lQb2xpY3lBY2NlcHRlZCI6dHJ1ZSwidGVybXNPZlVzZUFjY2VwdGVkIjp0cnVlLCJ0ZW5hbnRJZCI6ImUyZGQ2NTAwLTY3OGEtMTFlYi05MjJjLWY3NDAyMTlhYmNiOCIsImN1c3RvbWVySWQiOiIxMzgxNDAwMC0xZGQyLTExYjItODA4MC04MDgwODA4MDgwODAifQ.UY7li1S784wJHSd1eFTNeZTgA31fhqOEv20y3n5XGSxCPon77Cv0r2QQsweNvGqJegMJeLappcEPj2kr-x8WWw";    private static final String BEARER_TOKEN = "Bearer " + TOKEN;
    private static final String DEVICE_ID = "cf87adf0-dc76-11ec-b1ed-e5d3f0ce866e";
    private static final String USER_THB = "studentupm2022@gmail.com";
    private static final String PASS_THB = "student";

    TelemetriaViewModel telemetriaViewModel;
    private String sAuthBearerToken = "";


    private TextView tvRespuesta;
    private ImageView color;
    private ImageView ivRespuesta;
    private ImageView ivRespuestaHum;
    private ImageView ivRespuestaLig;
    private ImageView ivRespuestaTemp;
    private ImageView ivRespuestaSTP1;
    private ImageView ivRespuestaSTP2;


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

                    for (int i = 0; i < lCo2.size() - 1; i++) {
                        Date currentDate = new Date(lCo2.get(i).getTs());
                        String sTs = df.format(currentDate);
                        tvRespuesta.append(sTs);
                        tvRespuesta.append("\n\n");
                        tvRespuesta.append("[Co2 : " + lCo2.get(i).getTs() + "] " + "|" + String.valueOf(lCo2.get(i).getValue()) + " PPM]" + "\n");
                        tvRespuesta.append("[Humidity : " + lHum.get(i).getTs() + "] " + "|" + String.valueOf(lHum.get(i).getValue()) + " %]" + "\n");
                        tvRespuesta.append("[Light: " + lLig.get(i).getTs() + "] " + "|" + String.valueOf(lLig.get(i).getValue()) + "] lux" + "\n");
                        tvRespuesta.append("[SoilTemp1 : " + lST1.get(i).getTs() + "] " + "|" + String.valueOf(lST1.get(i).getValue()) + " ºC]" + "\n");
                        tvRespuesta.append("[SoilTemp2 : " + lST2.get(i).getTs() + "] " + "|" + String.valueOf(lST2.get(i).getValue()) + " ºC]" + "\n");
                        tvRespuesta.append("[Temperature : " + lTem.get(i).getTs() + "] " + "|" + String.valueOf(lTem.get(i).getValue()) + " ºC]" + "\n");


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
                tvRespuesta.append("Timestamp : " + sTs + "\n");
                int numCO2 = Integer.parseInt(lm.getCo2().get(0).getValue());
                int numHum = Integer.parseInt(lm.getHumidity().get(0).getValue());
                int numLig = Integer.parseInt(lm.getLight().get(0).getValue());
                int numTemp = Integer.parseInt(lm.getTemperature().get(0).getValue());
                int numST1 = Integer.parseInt(lm.getSoilTemp1().get(0).getValue());
                int numST2 = Integer.parseInt(lm.getSoilTemp2().get(0).getValue());
                if (numCO2 > 384 && numHum > 45 && numLig > 57.15 && numTemp > 24.47 &&
                        numST1 > 21.15 && numST2 > 21.56) {
                    ivRespuesta.setImageResource(R.drawable.co2);
                    tvRespuesta.append(lm.getCo2().get(0).getValue());
                    ivRespuesta.setImageResource(R.drawable.co2gred);
                    tvRespuesta.append(lm.getHumidity().get(0).getValue());
                    ivRespuestaHum.setImageResource(R.drawable.humidityred);
                    tvRespuesta.append(lm.getLight().get(0).getValue());
                    ivRespuestaLig.setImageResource(R.drawable.light);
                    tvRespuesta.append(lm.getTemperature().get(0).getValue());
                    ivRespuestaTemp.setImageResource(R.drawable.redtemp);
                    tvRespuesta.append(lm.getSoilTemp1().get(0).getValue());
                    ivRespuestaSTP1.setImageResource(R.drawable.soilred);
                    tvRespuesta.append(lm.getSoilTemp2().get(0).getValue());
                    ivRespuestaSTP2.setImageResource(R.drawable.soilred);

                } else if (numCO2 < 384 || numHum < 45 || numLig < 57.15 || numTemp < 24.47 ||
                        numST1 < 21.15 || numST2 < 21.56) {



                } else {
                    tvRespuesta.append("Co2 : " + lm.getCo2().get(0).getValue() +

                            " Humidity : " + lm.getHumidity().get(0).getValue() + "\n" +
                            " Light : " + lm.getLight().get(0).getValue() + "\n" +
                            " Temperature  : " + lm.getTemperature().get(0).getValue() + "\n" +
                            " SoilTemp1 : " + lm.getSoilTemp1().get(0).getValue() + "\n" +
                            " SoilTemp2 : " + lm.getSoilTemp2().get(0).getValue() + "\n")
                    ;


                }


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
        ivRespuesta.setImageResource(0);
    }
}





