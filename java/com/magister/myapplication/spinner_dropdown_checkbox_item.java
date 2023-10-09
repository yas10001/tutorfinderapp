package com.magister.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class spinner_dropdown_checkbox_item extends AppCompatActivity {

    private String[] dataList;
    private Set<String> selectedItems = new HashSet<>(); // Initialize the set for selected items

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_info);

        dataList = getResources().getStringArray(R.array.list);

        Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rec, dataList) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.rec, parent, false);
                }

                CheckBox checkbox = convertView.findViewById(R.id.checkbox);
                TextView textView = convertView.findViewById(R.id.spinner_item_text);

                String item = getItem(position);
                textView.setText(item);

                checkbox.setOnCheckedChangeListener(null); // Reset listener to avoid recycling issues
                checkbox.setChecked(selectedItems.contains(item)); // Set the initial state

                checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedItems.add(item); // Add the item to the selectedItems set
                    } else {
                        selectedItems.remove(item); // Remove the item from the selectedItems set
                    }
                });

                return convertView;
            }
        };

        adapter.setDropDownViewResource(R.layout.rec);
        spinner.setAdapter(adapter);
    }
}
