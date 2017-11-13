package test.unal.edu.co.sqlite;

import android.content.Intent;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class EditContact extends AppCompatActivity {

    EditText businessName, website, phone, email, products;
    Spinner category;
    Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        businessName = (EditText) findViewById(R.id.etBusinessNameEdit);
        website = (EditText) findViewById(R.id.etWebsiteEdit);
        phone = (EditText) findViewById(R.id.etPhoneEdit);
        email = (EditText) findViewById(R.id.etEmailEdit);
        products = (EditText) findViewById(R.id.etProductsEdit);
        category = (Spinner) findViewById(R.id.etCategoryEdit);

        category.setOnItemSelectedListener(new SelectOnItemSelectedListener());

        contact = (Contact) getIntent().getSerializableExtra("contact");
        businessName.setText(contact.getBusinessName());
        website.setText(contact.getWebsite());
        phone.setText(contact.getPhone());
        email.setText(contact.getEmail());
        products.setText(contact.getProduct());
        category.setSelection(((ArrayAdapter) category.getAdapter()).getPosition(contact.getCategory()));

    }

    public void editContact(View v) {
        try {
            ContactsDB db = new ContactsDB(this);
            db.open();
            db.updateEntry(contact.get_id(), getBusinessName(), getWebsite(), getPhone(), getEmail(), getProducts(), getCategory());
            db.close();
            Intent intent = new Intent(EditContact.this, MainActivity.class);
            startActivity(intent);
        } catch (SQLException e) {
            Toast.makeText(EditContact.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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