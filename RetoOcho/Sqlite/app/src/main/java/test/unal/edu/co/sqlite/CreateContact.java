package test.unal.edu.co.sqlite;

import android.content.Intent;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateContact extends AppCompatActivity {

    EditText businessName, website, phone, email, products;
    Spinner category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);

        businessName = (EditText) findViewById(R.id.etBusinessName);
        website = (EditText) findViewById(R.id.etWebsite);
        phone = (EditText) findViewById(R.id.etPhone);
        email = (EditText) findViewById(R.id.etEmail);
        products = (EditText) findViewById(R.id.etProducts);
        category = (Spinner) findViewById(R.id.etCategory);

        category.setOnItemSelectedListener(new SelectOnItemSelectedListener());
    }

    public void createContact(View v) {
        try {
            ContactsDB db = new ContactsDB(this);
            db.open();
            db.createEntry(getBusinessName(), getWebsite(), getPhone(), getEmail(), getProducts(), getCategory());
            db.close();
            Intent intent = new Intent(CreateContact.this, MainActivity.class);
            startActivity(intent);
        } catch (SQLException e) {
            Toast.makeText(CreateContact.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String getBusinessName() {
        return businessName.getText().toString().trim();
    }

    public String getWebsite() {
        return website.getText().toString().trim();
    }

    public String getPhone() {
        return phone.getText().toString().trim();
    }

    public String getEmail() {
        return email.getText().toString().trim();
    }

    public String getProducts() {
        return products.getText().toString().trim();
    }

    public String getCategory() {
        return String.valueOf(category.getSelectedItem()).trim();
    }
}
