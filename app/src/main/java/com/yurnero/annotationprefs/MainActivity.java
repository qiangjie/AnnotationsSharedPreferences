package com.yurnero.annotationprefs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.getui.annotationsharedpreferences.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MySharedPreferences.get().setAge(19);

        MySharedPreferences.get().getAge();
    }
}
