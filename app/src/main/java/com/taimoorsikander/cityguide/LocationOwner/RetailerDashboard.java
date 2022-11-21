package com.taimoorsikander.cityguide.LocationOwner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.taimoorsikander.cityguide.Common.LoginSignup.RetailerStartUpScreen;
import com.taimoorsikander.cityguide.Databases.SessionManager;
import com.taimoorsikander.cityguide.TelemetryActivity;
import com.taimoorsikander.cityguide.R;


import java.util.HashMap;

public class RetailerDashboard extends AppCompatActivity {

    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_dashboard);

        TextView textView = findViewById(R.id.textView);

        sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersDetails = sessionManager.getUsersDetailFromSession();

        String fullName = usersDetails.get(SessionManager.KEY_FULLNAME);
        String email = usersDetails.get(SessionManager.KEY_EMAIL);
        String phoneNumber = usersDetails.get(SessionManager.KEY_PHONENUMBER);
        String password = usersDetails.get(SessionManager.KEY_PASSWORD);
        String username = usersDetails.get(SessionManager.KEY_USERNAME);
        String age = usersDetails.get(SessionManager.KEY_DATE);
        String gender = usersDetails.get(SessionManager.KEY_GENDER);

        textView.setText(
                "Full Name: " + fullName + "\n" +
                        "Email: " + email + "\n" +
                        "Phone Number: " + phoneNumber + "\n" +
                        "Password: " + password + "\n" +
                        "Username: " + username + "\n" +
                        "Date of Birth: " + age + "\n" +
                        "Gender: " + gender + "\n"
        );

    }

    public void logoutTheUserFromSession(View view){
        sessionManager.logoutUserFromSession();
        startActivity(new Intent(getApplicationContext(), RetailerStartUpScreen.class));
        finish();
    }
    public void RedirectToTelemetry(View view) {

        startActivity((new Intent(getApplicationContext(), TelemetryActivity.class)));
        finish();


    }
}