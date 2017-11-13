package test.unal.edu.co.sqlite;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;


public class ContactAdapter extends ArrayAdapter<Contact> {
    private final Context context;
    private final ArrayList<Contact> values;
    private ArrayList<Contact> arraylist;
    private OnRefreshViewListner mRefreshListner;

    public ContactAdapter(Context context, ArrayList<Contact> list) {
        super(context, R.layout.row_layout, list);
        this.context = context;
        this.values = list;
        this.arraylist = new ArrayList<Contact>();
        this.arraylist.addAll(list);
        mRefreshListner = (OnRefreshViewListner)context;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);

        TextView lvBusinessName = (TextView) rowView.findViewById(R.id.lvBusinessName);
        TextView lvCategory = (TextView) rowView.findViewById(R.id.lvCategory);

        Button btEdit = (Button) rowView.findViewById(R.id.btEdit);
        Button btDelete = (Button) rowView.findViewById(R.id.btDelete);

        lvBusinessName.setText(values.get(position).getBusinessName());
        lvCategory.setText(values.get(position).getCategory());

        btEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, EditContact.class);
                intent.putExtra("contact", (Serializable) values.get(position));
                context.startActivity(intent);
            }
        });

        btDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    ContactsDB db = new ContactsDB(context);
                    db.open();
                    db.deleteEntry(values.get(position).get_id());
                    db.close();
                    Toast.makeText(context, "Empresa eliminada", Toast.LENGTH_SHORT).show();
                    mRefreshListner.refreshView();
                } catch (android.database.SQLException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rowView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        values.clear();
        if (charText.length() == 0) {
            values.addAll(arraylist);
        }
        else
        {
            for (Contact wp : arraylist)
            {
                if (wp.getBusinessName().toLowerCase(Locale.getDefault()).contains(charText)
                        || wp.getCategory().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    values.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}