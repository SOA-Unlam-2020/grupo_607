package activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facumediotte.tpandroid.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import utils.ObjectSerializer;

public class ListEvents extends AppCompatActivity {

    private ArrayList<String> myEvents;
    private LinearLayout myLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_events);

        myLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutEvent);
        myEvents = getEventList();

        for(int i = myEvents.size()-1; i >= 0; i--) {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,10,0,0);

            TextView newEvent = new TextView(this);
            newEvent.setGravity(Gravity.CENTER);
            newEvent.setText(myEvents.get(i));
            newEvent.setTextColor(Color.WHITE);
            newEvent.setLayoutParams(params);
            newEvent.setBackgroundColor(Color.parseColor("#1a237e"));

            final float scale = this.getResources().getDisplayMetrics().density;
            int pixels = (int) (150 * scale + 0.5f);
            newEvent.setHeight(pixels);
            newEvent.setTextSize(20);
            myLinearLayout.addView(newEvent);
        }

    }

    public ArrayList<String> getEventList() {

        ArrayList<String> eventsSHP = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences(HomeActivity.SHARED_PREFS_FILE, Context.MODE_PRIVATE);

        try {
            eventsSHP = (ArrayList<String>) ObjectSerializer.deserialize(prefs.getString(HomeActivity.FILE_SH_PREF,
                    ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return eventsSHP;
    }

}