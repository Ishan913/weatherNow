package com.weatherNow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView displayTextView;

    public void checkWeather(View view){
        try {
            String encodedCityName = URLEncoder.encode(editText.getText().toString(),"UTF-8");
            GetJson task = new GetJson();
            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&units=metric&APPID=c02dc05ad895748d04fe8119b4a5c2dd");
            //task.execute("https://openweathermap.org/data/2.5/weather?q="+ editText.getText().toString() +"&appid=439d4b804bc8187953eb36d2a8c26a02").get();

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editText = findViewById(R.id.editText);
        displayTextView = findViewById(R.id.displayTextView);


    }

    public class GetJson extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();

                //Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();

                return null;
            }
    }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {

                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                String temperatureInfo = jsonObject.getString("main");
                String windInfo = jsonObject.getString("wind");

                JSONArray weatherArray = new JSONArray(weatherInfo);
                JSONObject tempObject = new JSONObject(temperatureInfo);
                JSONObject windObject = new JSONObject(windInfo);

                String message ="";

                String temp = tempObject.getString("temp");
                message += "Temperature: " + temp+ "°C" +"\r\n";

                String humidity = tempObject.getString("humidity");
                message += "Humidity: " + humidity + "%" +"\r\n";

                String windSpeed = windObject.getString("speed");
                message += "Wind Speed: "+windSpeed +"meter/sec"+"\r\n";

                for (int i=0;i<weatherArray.length();i++){
                    JSONObject jsonPart = weatherArray.getJSONObject(i);

                    String main= jsonPart.getString("main");
                    String description=jsonPart.getString("description");

                    if (!main.equals("") && !description.equals("")){
                        message += main+ ": "+ description + "\r\n";
                    }
                }



                if (!message.equals("")){
                    displayTextView.setText(message);
                }else{
                    Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();
                }



            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();
            }

        }
    }
}