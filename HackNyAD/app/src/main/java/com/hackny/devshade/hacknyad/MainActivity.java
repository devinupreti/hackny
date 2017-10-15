package com.hackny.devshade.hacknyad;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import  com.bumptech.glide.request.target.ViewTarget.*;

public class MainActivity extends AppCompatActivity {

    //AIzaSyCjSLIeyAwcXGW-kCAOVp70x91FbfsDRUU
    TextView logTextView;
    ImageView gifHolder;
    LinearLayout inflateThisView;
    //Button button;
    ImageView speakImageView;
    //Button shareButton;
    //MapView mapView;
    //GoogleMap googleMap;
    int widthdp;
    int heightdp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logTextView = (TextView) findViewById(R.id.onscreenLogtextView);
        logTextView.setText("New Text");
        gifHolder = (ImageView) findViewById(R.id.imageView);
        speakImageView = (ImageView) findViewById(R.id.speakImageView);
        //shareButton = (Button) findViewById(R.id.sharebutton);
        inflateThisView = (LinearLayout) findViewById(R.id.inflateThisView);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        float density = getResources().getDisplayMetrics().density;
        widthdp = (int)width / (int)density;
        heightdp = (int)height / (int)density;

//        for(int i=0; i<5; i++){
//            View view = getLayoutInflater().inflate(R.layout.inflaterone, inflateThisView, false);
//            ImageView tempImageView = (ImageView) view.findViewById(R.id.imageViewForOneGif);
//            Glide.with(MainActivity.this.getApplicationContext()).load("https://media.giphy.com/media/b0cWhN9idMgNi/giphy.gif").into(tempImageView);
//            inflateThisView.addView(view);
//        }

        Glide.with(MainActivity.this.getApplicationContext()).load("https://media.giphy.com/media/b0cWhN9idMgNi/giphy.gif").into(gifHolder);

        speakImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // do something when the button is clicked
                inflateThisView.removeAllViews();
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Sunset...speak!!");
                try {
                    startActivityForResult(intent, 100);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), "speech not supported", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        shareButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // do something when the button is clicked
//                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
//                whatsappIntent.setType("text/plain");
//                //whatsappIntent.setPackage("com.whatsapp");
//                whatsappIntent.putExtra(Intent.EXTRA_TEXT, logTextView.getText());
//                try {
//                    startActivity(whatsappIntent);
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(getApplicationContext(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

//        GetData getData = new GetData();
//        DataInput dataInput = new DataInput();
//        dataInput.input = "SomeRandomString";
//        getData.execute(dataInput);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //logTextView.setText(result.get(0));
                    Toast.makeText(getApplicationContext(), result.get(0), Toast.LENGTH_SHORT).show();
                    String inputString = result.get(0);
                    inputString = inputString.replaceAll(" ", "+");
                    GetData getData = new GetData();
                    DataInput dataInput = new DataInput();
                    dataInput.input = inputString;
                    getData.execute(dataInput);
                }
                break;
            }

        }
    }

    class DataInput{
        String input;
    }

    class GetData extends AsyncTask<DataInput, String, String> {

        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(DataInput... args) {

            StringBuilder result = new StringBuilder();

            try {
                Log.i("TRIAL0", args[0].input);
                URL url = new URL("http://api.giphy.com/v1/gifs/search?q="+args[0].input+"&api_key=hpmVXfnjzUtSV7En4zbgEHpzwcoLbDPn&limit=10");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }


            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {

            //Do something with the JSON string
            try{
            JSONObject jsonObj = new JSONObject(result);
            JSONArray dataArray = jsonObj.getJSONArray("data");
//            JSONObject dataObject = (JSONObject) dataArray.get(0);
//            JSONObject images = dataObject.getJSONObject("images");
//            JSONObject downsized_medium = images.getJSONObject("downsized_large");
//            String urlString = downsized_medium.getString("url");
            //urlString = urlString.replaceAll("'\'", "");
            //Log.i("URL STRING", urlString);
            //logTextView.setText(urlString);
            //Glide.with(MainActivity.this.getApplicationContext()).load(urlString).into(gifHolder);
            for(int i=0; i<10; i++){
                JSONObject dataObject = (JSONObject) dataArray.get(i);
                JSONObject images = dataObject.getJSONObject("images");
                JSONObject downsized_medium = images.getJSONObject("downsized_large");
                final String urlString = downsized_medium.getString("url");
                View view = getLayoutInflater().inflate(R.layout.inflaterone, inflateThisView, false);
                ImageView tempImageView = (ImageView) view.findViewById(R.id.imageViewForOneGif);
                tempImageView.setMaxHeight(widthdp);
                tempImageView.setMinimumHeight(widthdp);
                tempImageView.setMaxWidth(widthdp);
                tempImageView.setMinimumWidth(widthdp);
                Button shareButton = (Button) view.findViewById(R.id.sharebuttonit);
                shareButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // do something when the button is clicked
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("text/plain");
                        //whatsappIntent.setPackage("com.whatsapp");
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, urlString);
                        try {
                            startActivity(whatsappIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getApplicationContext(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Glide.with(MainActivity.this.getApplicationContext()).load(urlString).into(tempImageView);
                inflateThisView.addView(view);
            }
            }
            catch(JSONException e){
                Log.e("ON_POST_EXECUTE", e.toString());
            }
        }

    }
}

