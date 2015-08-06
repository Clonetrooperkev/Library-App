package com.example.android.uamp.ui;

import android.app.Activity;
import android.content.Context;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.android.uamp.R;

import java.util.ArrayList;
import java.util.List;

public class ContactDetailAdapter extends ArrayAdapter<String> {
    private final Context context;
    int layoutResourceId;
    List<String> names;
    List<String> phones;

    //private final String[] names;
    //private final String[] phones;


    public ContactDetailAdapter(Context context,int layoutResourceId, List<String> names, List<String> phones) {
        super(context, -1, names);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.names = names;
        this.phones = phones;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(layoutResourceId, parent, false);
        }


        //ListView subcontacts = (ListView)rowView.findViewById(R.id.subcontactslist);
        TextView nameView = (TextView) rowView.findViewById(R.id.sub_contact_name);
        TextView phoneView = (TextView) rowView.findViewById(R.id.sub_contact_phone_name);
        nameView.setText(names.get(position));
        phoneView.setText(phones.get(position));
        Linkify.addLinks(phoneView, Linkify.PHONE_NUMBERS);

        return rowView;
    }
}
