package com.taimoorsikander.cityguide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.taimoorsikander.cityguide.device.ISpikeRESTAPIService;
import com.taimoorsikander.cityguide.models.TelemetriaEntity;
import com.taimoorsikander.cityguide.models.TelemetriaViewModel;
import com.taimoorsikander.cityguide.pojo.AuthorizationBearer;
import com.taimoorsikander.cityguide.pojo.Co2;
import com.taimoorsikander.cityguide.pojo.Credentials;
import com.taimoorsikander.cityguide.pojo.Humidity;
import com.taimoorsikander.cityguide.pojo.Light;
import com.taimoorsikander.cityguide.pojo.Measurement;
import com.taimoorsikander.cityguide.pojo.Sensors;
import com.taimoorsikander.cityguide.pojo.SoilTemp1;
import com.taimoorsikander.cityguide.pojo.SoilTemp2;
import com.taimoorsikander.cityguide.pojo.Temperature;
import com.taimoorsikander.cityguide.utils.CSVReader;
import com.taimoorsikander.cityguide.views.TelemetriasListAdapter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TelemetryActivity extends AppCompatActivity {

    static String LOG_TAG = "btb";
    public static final int NEW_ACTIVITY_REQUEST_CODE = 2022;

    private ISpikeRESTAPIService apiService;
    private static final String API_LOGIN_POST = "https://thingsboard.cloud/api/auth/"; // Base url to obtain token
    private static final String API_BASE_GET = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/"; // Base url to obtain data
    private static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdHVkZW50dXBtMjAyMkBnbWFpbC5jb20iLCJ1c2VySWQiOiI4NDg1OTU2MC00NzU2LTExZWQtOTQ1YS1lOWViYTIyYjlkZjYiLCJzY29wZXMiOlsiVEVOQU5UX0FETUlOIl0sImlzcyI6InRoaW5nc2JvYXJkLmNsb3VkIiwiaWF0IjoxNjY2OTY1MTQxLCJleHAiOjE2NjY5OTM5NDEsImZpcnN0TmFtZSI6IlN0dWRlbnQiLCJsYXN0TmFtZSI6IlVQTSIsImVuYWJsZWQiOnRydWUsImlzUHVibGljIjpmYWxzZSwiaXNCaWxsaW5nU2VydmljZSI6ZmFsc2UsInByaXZhY3lQb2xpY3lBY2NlcHRlZCI6dHJ1ZSwidGVybXNPZlVzZUFjY2VwdGVkIjp0cnVlLCJ0ZW5hbnRJZCI6ImUyZGQ2NTAwLTY3OGEtMTFlYi05MjJjLWY3NDAyMTlhYmNiOCIsImN1c3RvbWVySWQiOiIxMzgxNDAwMC0xZGQyLTExYjItODA4MC04MDgwODA4MDgwODAifQ.XabTYIrWA2aqnOBR5uNYEARZ4cBqObIZqKMtROeV5b0-Eluxff98VJUqXO-sIkGLKnfvrwUormbmn4dE3yQmDQ";
    private static final String BEARER_TOKEN = "Bearer " + TOKEN;
    private static final String DEVICE_ID = "cf87adf0-dc76-11ec-b1ed-e5d3f0ce866e";
    private static final String USER_THB = "studentupm2022@gmail.com";
    private static final String PASS_THB = "student";

    TelemetriaViewModel telemetriaViewModel;
    private String sAuthBearerToken ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Add the RecyclerView
        final RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final TelemetriasListAdapter adapter = new TelemetriasListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        telemetriaViewModel = ViewModelProviders.of(this).get(TelemetriaViewModel.class);
        telemetriaViewModel.getAll().observe(this, new Observer<List<TelemetriaEntity>>() {
            @Override
            public void onChanged(@Nullable final List<TelemetriaEntity> listTelemetriaEntity) {
                adapter.setItems(listTelemetriaEntity);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TelemetryActivity.this, NewTelemetryActivity.class);
                startActivityForResult(intent, NEW_ACTIVITY_REQUEST_CODE);
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        TelemetriaEntity tme = adapter.getItemAtPosition(position);

                        if (direction==ItemTouchHelper.RIGHT){
                            Snackbar.make(
                                    recyclerView,
                                    "Borrando " + tme.getTimestamp(),
                                    Snackbar.LENGTH_LONG
                            ).show();
                            telemetriaViewModel.delete(tme);

                        }else{
                            // Direction LEFT
                            Snackbar.make(
                                    recyclerView,
                                    tme.toString(),
                                    Snackbar.LENGTH_LONG
                            ).show();
                        }


                    }
                });

        helper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_delete_all:
                Log.i(LOG_TAG, "opción=" + getString(R.string.menu_delete_all));
                telemetriaViewModel.deleteAll();
                break;
            case R.id.item_count:
                Toast.makeText(this,
                        getString(R.string.menu_count)+" "+telemetriaViewModel.count(),
                        Toast.LENGTH_LONG).show();
                Log.i(LOG_TAG, "opción="
                        + getString(R.string.menu_count)
                        +" "+telemetriaViewModel.count());
                break;
            case R.id.item_load_ws:
                Toast.makeText(this,
                        "Cargar datos de WS",
                        Toast.LENGTH_LONG).show();

                //postBearerToken();
                getTelemetries();
                //getLastTelemetry();


                break;
            case R.id.item_load_csv:
                Log.i(LOG_TAG, "opción=" + getString(R.string.menu_load_csv));

                List<String[]> rows = new ArrayList<>();
                CSVReader csvReader = new CSVReader(this, "telemetrias.csv");
                ArrayList<String> lst = new ArrayList<String>();
                try {
                    rows = csvReader.readCSV();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < rows.size()-1; i++) {
                    TelemetriaEntity tmItem =  new TelemetriaEntity((String)rows.get(i)[0],
                            Integer.parseInt(rows.get(i)[1]),
                            Integer.parseInt(rows.get(i)[2]),
                            Integer.parseInt(rows.get(i)[3]),
                            Integer.parseInt(rows.get(i)[4]),
                            Integer.parseInt(rows.get(i)[5]),
                            Integer.parseInt(rows.get(i)[6])
                    );
                    telemetriaViewModel.insert(tmItem);
                }
                Log.i(LOG_TAG, "Inserted "+rows.size()+" items from CSV");
                Toast.makeText(this,
                        "Inserted "+rows.size()+" items into DB",
                        Toast.LENGTH_LONG).show();

                break;
            default:
                Log.i(LOG_TAG, "Opción desconocida");
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            TelemetriaEntity te = new TelemetriaEntity(
                    data.getStringExtra(NewTelemetryActivity.PARAM_TST),
                    data.getIntExtra(NewTelemetryActivity.PARAM_CO2,0),
                    data.getIntExtra(NewTelemetryActivity.PARAM_HUM,0),
                    data.getIntExtra(NewTelemetryActivity.PARAM_LIG,0),
                    data.getIntExtra(NewTelemetryActivity.PARAM_ST1,0),
                    data.getIntExtra(NewTelemetryActivity.PARAM_ST2,0),
                    data.getIntExtra(NewTelemetryActivity.PARAM_ATP,0)
            );
            telemetriaViewModel.insert(te);

        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void postBearerToken() {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(API_LOGIN_POST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ISpikeRESTAPIService iApi = retrofit.create(ISpikeRESTAPIService.class);
        Credentials c = new Credentials("studentupm2022@gmail.com","student");
        Call<AuthorizationBearer> call = iApi.postAuthorizationBearer(c);

        call.enqueue(new Callback<AuthorizationBearer>() {
            @Override
            public void onResponse(Call<AuthorizationBearer> call, Response<AuthorizationBearer> response) {
                Toast.makeText(TelemetryActivity.this, "Data posted to API", Toast.LENGTH_SHORT).show();
                AuthorizationBearer responseFromAPI = response.body();
                String responseString = "Response Code : " + response.code() + "\nToken : " + responseFromAPI.getToken() + "\n" + "RefreshToken : " + responseFromAPI.getRefreshToken();
                Log.i(LOG_TAG, " response: "+responseString);
                sAuthBearerToken = responseFromAPI.getToken();
                Log.i(LOG_TAG, " granted AuthBearerToken: "+sAuthBearerToken);
            }

            @Override
            public void onFailure(Call<AuthorizationBearer> call, Throwable t) {
                Log.e(LOG_TAG, " error message: "+t.getMessage());
            }
        });
    }

    private void getLastTelemetry() {
        //https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/{{deviceId}}/values/timeseries?keys=co2&useStrictDataTypes=false
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
        Log.i(LOG_TAG, " request params: |"+ BEARER_TOKEN +"|"+ DEVICE_ID +"|"+keys+"|"+useStrictDataTypes);
        Call<Measurement> call = iApi.getLastTelemetry(BEARER_TOKEN, DEVICE_ID, keys, useStrictDataTypes);


        call.enqueue(new Callback<Measurement>() {
            @Override
            public void onResponse(Call<Measurement> call, Response<Measurement> response) {
                Toast.makeText(TelemetryActivity.this, "Data posted to API", Toast.LENGTH_SHORT).show();
                Measurement lm = response.body();

                String responseString = "Response Code : " + response.code();
                Log.i(LOG_TAG, " response: "+responseString);
                Log.i(LOG_TAG, " response.body: "+lm.getCo2().get(0).getValue());
            }

            @Override
            public void onFailure(Call<Measurement> call, Throwable t) {
                Log.e(LOG_TAG, " error message: "+t.getMessage());
            }
        });

    }


    private void getTelemetries() {

        String keys = "co2,humidity,light,soilTemp1,soilTemp2,temperature";

        // Use https://www.epochconverter.com/ to get milliseconds for a given date
        // These are some date examples:
        // String iniTimestamp = "1666735200000";// 26/10/2023
        // String iniTimestamp = "972684000000";// 28/10/2000
        // String iniTimestamp = "1666735200000";// 26/10/2023
        String iniTimestamp = "972684000000";// 28/10/2000
        String endTimestamp = "1666908000000";// 28/10/2023

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
        Log.i(LOG_TAG, " request params: |"+ BEARER_TOKEN +"|"+ DEVICE_ID +"|"+keys+"|"+iniTimestamp+"|"+endTimestamp);
        Call<Sensors> call = iApi.getTelemetries(BEARER_TOKEN, DEVICE_ID, keys, iniTimestamp, endTimestamp);

        call.enqueue(new Callback<Sensors>() {
            @Override
            public void onResponse(Call<Sensors> call, Response<Sensors> response) {
                Toast.makeText(TelemetryActivity.this, "Data posted to API", Toast.LENGTH_SHORT).show();
                Sensors responseFromAPI = response.body();
                String responseString = "Response Code : " + response.code();
                Log.i(LOG_TAG, " response: "+responseString);

                if (responseFromAPI == null) {
                    Log.i(LOG_TAG, " API returned empty values for sensors");
                }else{

                    List<Co2> lCo2 = responseFromAPI.getCo2();
                    List<Humidity> lHum = responseFromAPI.getHumidity();
                    List<Light> lLig = responseFromAPI.getLight();
                    List<SoilTemp1> lST1 = responseFromAPI.getSoilTemp1();
                    List<SoilTemp2> lST2 = responseFromAPI.getSoilTemp2();
                    List<Temperature> lTem = responseFromAPI.getTemperature();


                    DateFormat df = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                    }
                    for (int i = 0; i < lCo2.size()-1; i++) {

                        Date currentDate = new Date(lCo2.get(i).getTs());
                        String sTs = df.format(currentDate);
                        TelemetriaEntity tmItem =  new TelemetriaEntity(sTs,
                                Integer.parseInt((String)lCo2.get(i).getValue().toString()),
                                Integer.parseInt((String)lHum.get(i).getValue().toString()),
                                Integer.parseInt((String)lLig.get(i).getValue()),
                                Integer.parseInt((String)lST1.get(i).getValue()),
                                Integer.parseInt((String)lST2.get(i).getValue()),
                                Integer.parseInt((String)lTem.get(i).getValue())
                        );
                        telemetriaViewModel.insert(tmItem);
                        Log.i(LOG_TAG, " timestamps ["+i
                                +"] ["+String.valueOf(lCo2.get(i).getTs())+"|"+String.valueOf(lCo2.get(i).getValue())+"]"
                                +"] ["+String.valueOf(lHum.get(i).getTs())+"|"+String.valueOf(lHum.get(i).getValue())+"]"
                                +"] ["+String.valueOf(lLig.get(i).getTs())+"|"+String.valueOf(lLig.get(i).getValue())+"]"
                                +"] ["+String.valueOf(lST1.get(i).getTs())+"|"+String.valueOf(lST1.get(i).getValue())+"]"
                                +"] ["+String.valueOf(lST2.get(i).getTs())+"|"+String.valueOf(lST2.get(i).getValue())+"]"
                                +"] ["+String.valueOf(lTem.get(i).getTs())+"|"+String.valueOf(lTem.get(i).getValue())+"]");
                    }
                    Log.i(LOG_TAG, " response: "+responseFromAPI.toString());
                }
            }

            @Override
            public void onFailure(Call<Sensors> call, Throwable t) {
                Log.e(LOG_TAG, " error message: "+t.getMessage());
            }
        });

    }
}