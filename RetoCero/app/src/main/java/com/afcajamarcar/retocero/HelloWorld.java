package com.afcajamarcar.retocero;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.text.Html;
import android.text.method.LinkMovementMethod;

public class HelloWorld extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
        TextView link = (TextView) findViewById(R.id.textView);
        String linkText = "Developed by <a href='https://github.com/afcajamarcar'> Andres Cajamarca</a>.";
        link.setText(Html.fromHtml(linkText));
        link.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
