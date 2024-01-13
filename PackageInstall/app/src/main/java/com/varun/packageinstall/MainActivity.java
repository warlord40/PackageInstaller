package com.varun.packageinstall;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    public final String APK_PATH ="file:///data/vendor/SampleApplication.apk"; //replace the APK_PATH

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TestInstallPackage as = new TestInstallPackage();
        as.packageInstallApk(APK_PATH, getApplicationContext());
    }

}
