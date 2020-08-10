package com.example.weather;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private EditText searchBar;
    private TextView show;
    private Button searchBtn;
    private String jsonResponse;
    private String url="https://api.openweathermap.org/data/2.5/weather?q=";
    private String fullUrl;
    private String API_Key="&appid=74139fdc99e0b7dc8d404debaee2bff8";
    private static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchBar=(EditText)findViewById(R.id.search_city);
        searchBtn=(Button)findViewById(R.id.search_btn);
        show=(TextView)findViewById(R.id.weather);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String cityName;
                    cityName = URLEncoder.encode(searchBar.getText().toString(),"UTF-8");
                    fullUrl=url+cityName+API_Key;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                makeJsonRequest();
                removeKeyboard();
            }
        });
    }
    private void makeJsonRequest(){
        final JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, fullUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    DecimalFormat decimalFormat=new DecimalFormat("#.##");
                    jsonResponse = "";
                    String cT,maT,miT,fT;
                    JSONObject object = response.getJSONObject("main");
                    double currentTemp=object.getDouble("temp");
                    currentTemp=currentTemp-273.15;
                    cT=decimalFormat.format(currentTemp);
                    double max_temp=object.getDouble("temp_max");
                    max_temp=max_temp-273.15;
                    maT=decimalFormat.format(max_temp);
                    double min_temp=object.getDouble("temp_min");
                    min_temp=min_temp-273.15;
                    miT=decimalFormat.format(min_temp);
                    double feels_like=object.getDouble("feels_like");
                    feels_like=feels_like-273.15;
                    fT=decimalFormat.format(feels_like);
                    jsonResponse=jsonResponse+"Current Temperature:"+cT+"°C"+"\n";
                    jsonResponse=jsonResponse+"Maximum Temperature: "+maT+"°C"+"\n";
                    jsonResponse=jsonResponse+"Minimum Temperature: "+miT+"°C"+"\n";
                    jsonResponse=jsonResponse+"Feels Like Temperature: "+fT+"°C"+"\n";
                    JSONArray array=response.getJSONArray("weather");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject weather = array.getJSONObject(i);
                        String main = weather.getString("description");
                        jsonResponse = jsonResponse + "Main: " + main;
                    }
                    if (jsonResponse != "") {
                        show.setBackgroundColor(Color.parseColor("#e6ccff"));
                        show.setText(jsonResponse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error 404", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG,"ERROR CONNECTION PROBLEM"+error.getMessage());
                Toast.makeText(getApplicationContext(),"Not Connected",Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
    private void removeKeyboard(){
        InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(show.getWindowToken(),0);
    }
}
