package no.ntnu.fredrik.lab2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class UserPreference extends AppCompatActivity {

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preference);

        //Stores RSS url from user
        editText = findViewById(R.id.editText);

        Button btnSave = findViewById(R.id.button);

        //Fetches the radio buttons for numbers of rows
        maxRowsRadioButtons();
        setTimerRadioButtons();

        // saves userpreferences and sends back to home page
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveURLToRSSFeed();
                Intent returnIntent = getIntent();
                setResult(RESULT_OK,returnIntent);
                Intent intent = new Intent(UserPreference.this, listView.class);
                startActivity(intent);
            }
        });
    }

    //Populates the timers buttons
    private void setTimerRadioButtons() {
        RadioGroup timerRadioGroup = findViewById(R.id.refresh);
        final int[] inputs = getResources().getIntArray(R.array.refresh_time);

        // Fills radiobuttons
        for (final int refreshItem : inputs) {
            RadioButton button = new RadioButton(this);
            button.setText(refreshItem + " min");

            //Saves selected button
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendInput(refreshItem);
                }
            });

            //Add to radio group:
            timerRadioGroup.addView(button);

            // select default button
            if (refreshItem == getNumbersOfTimerButtons(this)) {

                button.setChecked(true);
            }
        }
    }

    // Saves to shared preferences
    private void sendInput(int rows) {
        SharedPreferences prefs = this.getSharedPreferences("timerPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Intervals", rows);
        editor.apply();
    }

    //Reads from shared preferences
    static public int getNumbersOfTimerButtons(Context context){
        SharedPreferences prefs = context.getSharedPreferences("timerPrefs", MODE_PRIVATE);
        return prefs.getInt("Intervals", 10);
    }

    // Populates the radio buttons for numbers of rows
    private void maxRowsRadioButtons() {
        RadioGroup rowRadioGroup = (RadioGroup)findViewById(R.id.articleAmount);
        final int[] numberOfRows = getResources().getIntArray(R.array.list_amount);

        // Fills radiobuttons buttons
        for (final int rows : numberOfRows) {
            RadioButton button = new RadioButton(this);
            button.setText(rows + " articles");

            //Saves selected button
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveNumbersOfPreferedRows(rows);
                }
            });

            //Add to radio group:
            rowRadioGroup.addView(button);

            // select default button
            if (rows == getNumbersofRowsnumbers(this)) {

                button.setChecked(true);
            }
        }
    }

    // Saves to shared preferences
    private void saveNumbersOfPreferedRows(int rows) {
        SharedPreferences prefs = this.getSharedPreferences("rowPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Number of rows", rows);
        editor.apply();
    }

    //Reads from shared preferences
    static public int getNumbersofRowsnumbers(Context context){
        SharedPreferences prefs = context.getSharedPreferences("rowPrefs", MODE_PRIVATE);
        return prefs.getInt("Number of rows", 10);
    }

    //Saves url to rss feed
    private void saveURLToRSSFeed(){
        String editTextValue = editText.getText().toString();
        SharedPreferences prefs = this.getSharedPreferences("RSSPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("PrefsForRSS", String.valueOf(editTextValue));
        editor.apply();
    }

    //Reads from shared preferences
    static public String getRSSFeedFromUser(Context context){
        SharedPreferences prefs = context.getSharedPreferences("RSSPrefs", MODE_PRIVATE);
        return prefs.getString("PrefsForRSS", "http://www.aweber.com/blog/feed/");

    }
}
