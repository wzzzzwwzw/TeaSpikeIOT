package com.taimoorsikander.cityguide;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
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
    private static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdHVkZW50dXBtMjAyMkBnbWFpbC5jb20iLCJ1c2VySWQiOiI4NDg1OTU2MC00NzU2LTExZWQtOTQ1YS1lOWViYTIyYjlkZjYiLCJzY29wZXMiOlsiVEVOQU5UX0FETUlOIl0sImlzcyI6InRoaW5nc2JvYXJkLmNsb3VkIiwiaWF0IjoxNjY5MDQ4NjMzLCJleHAiOjE2NjkwNzc0MzMsImZpcnN0TmFtZSI6IlN0dWRlbnQiLCJsYXN0TmFtZSI6IlVQTSIsImVuYWJsZWQiOnRydWUsImlzUHVibGljIjpmYWxzZSwiaXNCaWxsaW5nU2VydmljZSI6ZmFsc2UsInByaXZhY3lQb2xpY3lBY2NlcHRlZCI6dHJ1ZSwidGVybXNPZlVzZUFjY2VwdGVkIjp0cnVlLCJ0ZW5hbnRJZCI6ImUyZGQ2NTAwLTY3OGEtMTFlYi05MjJjLWY3NDAyMTlhYmNiOCIsImN1c3RvbWVySWQiOiIxMzgxNDAwMC0xZGQyLTExYjItODA4MC04MDgwODA4MDgwODAifQ.9l7yblXyV_cx0bCoZed-9ZE14PFo2bLOMPrZi9cI_NdQwN2xG66uN4EAKajKndncxtOZB2kVJn39PTZGZyuDzw";
    private static final String BEARER_TOKEN = "Bearer " + TOKEN;
    private static final String DEVICE_ID = "cf87adf0-dc76-11ec-b1ed-e5d3f0ce866e";
    private static final String USER_THB = "studentupm2022@gmail.com";
    private static final String PASS_THB = "student";

    TelemetriaViewModel telemetriaViewModel;
    private String sAuthBearerToken = "";


    private TextView tvRespuesta;
    private ImageView ivRespuesta;
    private EditText etCountryName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tea_spike);
        tvRespuesta = (TextView) findViewById(R.id.tvRespuesta);
        ivRespuesta = (ImageView) findViewById(R.id.ivRespuesta);
        etCountryName = (EditText) findViewById(R.id.countryName);

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

                    for (int i=0;i<lCo2.size()-1;i++) {

                        tvRespuesta.append( ".[Co2 : " +lCo2.get(i).getTs() + "] "+"|"+String.valueOf(lCo2.get(i).getValue())+"]");

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
}





