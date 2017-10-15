package com.hackny.devshade.hacknyad;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import com.bumptech.glide.load.model.StringLoader;
import  com.bumptech.glide.request.target.ViewTarget.*;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiInput;
import okhttp3.OkHttpClient;

//import clarifai2.api.ClarifaiBuilder;
//import clarifai2.dto.input.ClarifaiInput;
//import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    //AIzaSyCjSLIeyAwcXGW-kCAOVp70x91FbfsDRUU
    TextView logTextView;
    ImageView gifHolder;
    LinearLayout inflateThisView;
    LinearLayout settingsLayout;
    ScrollView scrollerView;
    //Button button;
    ImageView speakImageView;
    ImageView settingImageView;
    ImageView trendingImageView;
    ImageView cameraImageView;
    //Button shareButton;
    //MapView mapView;
    //GoogleMap googleMap;
    int widthdp;
    int heightdp;
    Switch lightHeavy;
    SeekBar nog;
    TextView nogText;

    int progressChangedValue = 1;
    Boolean isHeavy = true;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Uri imageUri;
    File photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ClarifaiBuilder("e8200df8d6b14beaab26d15641935799")
                .client(new OkHttpClient()) // OPTIONAL. Allows customization of OkHttp by the user
                .buildSync();

        pref = getApplicationContext().getSharedPreferences("sunsetPref", 0); // 0 - for private mode
        editor = pref.edit();

        logTextView = (TextView) findViewById(R.id.onscreenLogtextView);
        logTextView.setText("New Text");
        gifHolder = (ImageView) findViewById(R.id.imageView);
        speakImageView = (ImageView) findViewById(R.id.speakImageView);
        settingImageView = (ImageView) findViewById(R.id.settingImageView);
        trendingImageView = (ImageView) findViewById(R.id.trendingImageView);
        cameraImageView = (ImageView) findViewById(R.id.cameraImageView);
        //shareButton = (Button) findViewById(R.id.sharebutton);
        inflateThisView = (LinearLayout) findViewById(R.id.inflateThisView);

        settingsLayout = (LinearLayout) findViewById(R.id.settingLayout);
        scrollerView = (ScrollView) findViewById(R.id.scrollerView);
        scrollerView.setVisibility(View.GONE);
        settingsLayout.setVisibility(View.VISIBLE);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        float density = getResources().getDisplayMetrics().density;
        widthdp = (int)width / (int)density;
        heightdp = (int)height / (int)density;

        lightHeavy = (Switch) findViewById(R.id.switch1);
        nog = (SeekBar) findViewById(R.id.seekBar);
        nogText = (TextView) findViewById(R.id.numbergifTextView);

        isHeavy = pref.getBoolean("isHeavy", true);
        lightHeavy.setChecked(isHeavy);
        //nog.setMin(1);
        //nog.setMin(30);
        progressChangedValue = pref.getInt("nog", 1);
        nog.setProgress((progressChangedValue-1)*3);
        nogText.setText("I want "+progressChangedValue+" GIFs");

        editor.putBoolean("isHeavy", isHeavy);
        editor.putInt("nog", progressChangedValue); // Storing integer
        editor.commit();

        lightHeavy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                editor.putBoolean("isHeavy", isChecked); // Storing boolean - true/false
                editor.commit();
            }
        });

        nog.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = (progress/3)+1;
                editor.putInt("nog", progressChangedValue); // Storing integer
                nogText.setText("I want "+Integer.toString(progressChangedValue)+" GIFs");
                editor.commit();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(MainActivity.this, "Seek bar progress is :" + progressChangedValue, Toast.LENGTH_SHORT).show();
            }
        });

//        for(int i=0; i<5; i++){
//            View view = getLayoutInflater().inflate(R.layout.inflaterone, inflateThisView, false);
//            ImageView tempImageView = (ImageView) view.findViewById(R.id.imageViewForOneGif);
//            Glide.with(MainActivity.this.getApplicationContext()).load("https://media.giphy.com/media/b0cWhN9idMgNi/giphy.gif").into(tempImageView);
//            inflateThisView.addView(view);
//        }

        //Glide.with(MainActivity.this.getApplicationContext()).load("https://media.giphy.com/media/b0cWhN9idMgNi/giphy.gif").into(gifHolder);

        speakImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // do something when the button is clicked
                settingsLayout.setVisibility(View.GONE);
                scrollerView.setVisibility(View.VISIBLE);
                inflateThisView.removeAllViews();
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the magic words!!");
                try {
                    startActivityForResult(intent, 100);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), "speech not supported", Toast.LENGTH_SHORT).show();
                }
            }
        });

        settingImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // do something when the button is clicked
                settingsLayout.setVisibility(View.VISIBLE);
                scrollerView.setVisibility(View.GONE);

            }
        });

        trendingImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // do something when the button is clicked
                settingsLayout.setVisibility(View.GONE);
                scrollerView.setVisibility(View.VISIBLE);
                inflateThisView.removeAllViews();
                GetData getData = new GetData();
                DataInput dataInput = new DataInput();
                dataInput.input = "Dummy";
                dataInput.isHigh = isHeavy;
                dataInput.progress = progressChangedValue;
                dataInput.isTrending = true;
                getData.execute(dataInput);
            }
        });

        cameraImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // do something when the button is clicked
                settingsLayout.setVisibility(View.GONE);
                scrollerView.setVisibility(View.VISIBLE);
                inflateThisView.removeAllViews();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            123);
                    Toast.makeText(MainActivity.this, "No Permissions", Toast.LENGTH_SHORT).show();
                }

                photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo));
                imageUri = Uri.fromFile(photo);
                startActivityForResult(intent, 200);

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
                    //Toast.makeText(getApplicationContext(), result.get(0), Toast.LENGTH_SHORT).show();
                    String inputString = result.get(0);
                    inputString = inputString.replaceAll(" ", "+");
                    GetData getData = new GetData();
                    DataInput dataInput = new DataInput();
                    dataInput.input = inputString;
                    dataInput.isHigh = isHeavy;
                    dataInput.progress = progressChangedValue;
                    dataInput.isTrending = false;
                    getData.execute(dataInput);
                }
                break;
            }

            case 200: {

                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
//                    ContentResolver cr = getContentResolver();
//                    Bitmap bitmap;
                    try {
//                        bitmap = android.provider.MediaStore.Images.Media
//                                .getBitmap(cr, selectedImage);
//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                        byte[] byteArray = stream.toByteArray();

                        ClarifyMy clarifyMy = new ClarifyMy();
                        clarifyMy.execute("Dummy");

                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }

            }

        }
    }

    class DataInput{
        String input;
        Boolean isHigh;
        int progress;
        Boolean isTrending;
    }

    class Result{
        String result;
        Boolean isHigh;
    }

    class ClarifyMy extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Machines are doing their job...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            ClarifaiClient client = new ClarifaiBuilder("e8200df8d6b14beaab26d15641935799").buildSync();
            String abc = client.getDefaultModels().generalModel().predict()
                    .withInputs(ClarifaiInput.forImage(photo))
                    .executeSync().rawBody();
            //Log.i("YAHOOOO", abc);
            return abc;
        }

        @Override
        protected void onPostExecute(String result) {
            //Do something with the JSON string
            try{
                ArrayList<String> myList = new ArrayList<>();
                JSONObject jsonObj = new JSONObject(result);
                int status = jsonObj.getJSONObject("status").getInt("code");
                if(status==10000){
                    JSONObject temp = (JSONObject) jsonObj.getJSONArray("outputs").get(0);
                    JSONObject temp2 = (JSONObject) temp.getJSONObject("data");
                    JSONArray temp3 = (JSONArray) temp2.getJSONArray("concepts");
                    for(int i=0; i< temp3.length(); i++){
                        JSONObject thisObject = (JSONObject) temp3.get(i);
                        String AIString = thisObject.getString("name");
                        myList.add(AIString);
                    }
                    //now use the myList
                    int counter=0;
                    String inputString = "";
                    for(int i=0; i<myList.size(); i++){
                        String thisString = myList.get(i);
                        if(counter<5 && !thisString.equals("no person") && !thisString.equals("adult") && !thisString.equals("outdoors") && !thisString.equals("indoors")){
                            inputString = inputString.concat(thisString+"+");
                            Log.i("AI String", inputString);
                            counter++;
                        }
                        else{
                            continue;
                        }
                    }
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    GetData getData = new GetData();
                    DataInput dataInput = new DataInput();
                    dataInput.input = inputString;
                    dataInput.isHigh = isHeavy;
                    dataInput.progress = progressChangedValue;
                    dataInput.isTrending = false;
                    getData.execute(dataInput);
                }
                else{
                    Log.e("ERROR", "Status not 10000");
                }
            }
            catch(JSONException e){
                Log.e("ON_POST_EXECUTE", e.toString());
            }
        }

    }

    class GetData extends AsyncTask<DataInput, String, Result> {
        //private ProgressDialog dialog;
        HttpURLConnection urlConnection;

        @Override
        protected void onPreExecute() {
            //dialog = new ProgressDialog(MainActivity.this);
            //dialog.setMessage("Doing something, please wait.");
            //dialog.show();
        }

        @Override
        protected Result doInBackground(DataInput... args) {

            StringBuilder result = new StringBuilder();

            try {
                Log.i("TRIAL0", args[0].input);
                URL url = new URL("http://api.giphy.com/v1/gifs/search?q="+args[0].input+"&api_key=hpmVXfnjzUtSV7En4zbgEHpzwcoLbDPn&rating=g&limit="+args[0].progress);
                if(args[0].isTrending == true){
                    url = new URL("http://api.giphy.com/v1/gifs/trending?api_key=hpmVXfnjzUtSV7En4zbgEHpzwcoLbDPn&limit="+args[0].progress);
                }
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

            Result resultO = new Result();
            resultO.isHigh = args[0].isHigh;
            resultO.result = result.toString();
            return resultO;
        }

        @Override
        protected void onPostExecute(Result result) {

            //Do something with the JSON string
            try{
                JSONObject jsonObj = new JSONObject(result.result);
                JSONArray dataArray = jsonObj.getJSONArray("data");
//            JSONObject dataObject = (JSONObject) dataArray.get(0);
//            JSONObject images = dataObject.getJSONObject("images");
//            JSONObject downsized_medium = images.getJSONObject("downsized_large");
//            String urlString = downsized_medium.getString("url");
                //urlString = urlString.replaceAll("'\'", "");
                //Log.i("URL STRING", urlString);
                //logTextView.setText(urlString);
                //Glide.with(MainActivity.this.getApplicationContext()).load(urlString).into(gifHolder);
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//                }
                for(int i=0; i<progressChangedValue; i++){
                    JSONObject dataObject = (JSONObject) dataArray.get(i);
                    JSONObject images = dataObject.getJSONObject("images");
                    JSONObject sizeParam = images.getJSONObject("original");
                    if(!result.isHigh){
                        sizeParam = images.getJSONObject("downsized_medium");
                    }
                    final String urlString = sizeParam.getString("url");
                    View view = getLayoutInflater().inflate(R.layout.inflaterone, inflateThisView, false);
                    ImageView tempImageView = (ImageView) view.findViewById(R.id.imageViewForOneGif);
                    tempImageView.setMaxHeight(widthdp*2);
                    tempImageView.setMinimumHeight(widthdp*2);
                    //tempImageView.setMaxWidth(widthdp);
                    //tempImageView.setMinimumWidth(widthdp);
                    //Button shareButton = (Button) view.findViewById(R.id.sharebuttonit);
                    tempImageView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            // do something when the button is clicked
                            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                            whatsappIntent.setType("text/plain");
                            //whatsappIntent.setPackage("com.whatsapp");
                            whatsappIntent.putExtra(Intent.EXTRA_TEXT, urlString);
                            try {
                                startActivity(whatsappIntent);
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(getApplicationContext(), "Apps have not been installed.", Toast.LENGTH_SHORT).show();
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

