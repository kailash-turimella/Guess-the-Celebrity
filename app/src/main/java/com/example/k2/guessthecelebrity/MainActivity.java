package com.example.k2.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    MediaPlayer correct;
    MediaPlayer incorrect;
    ImageView imageView ;
    Button option1,option2,option3,option4;
    String result = null,message;
    ArrayList<String> celeburls = new ArrayList<String>();
    ArrayList<String> celebnames = new ArrayList<String>();
    ArrayList<String> answers = new ArrayList<String>();
    Random r =new Random();
    int correctOption;
    int celebIndex;


    public void nextImage(){
        ImageDownloader task = new ImageDownloader();
        Bitmap image;

        try {
            celebIndex = r.nextInt(celebnames.size());
            correctOption = r.nextInt(4)+1;
            image = task.execute(celeburls.get(celebIndex)).get();
            imageView.setImageBitmap(image);
            answers.clear();
            for (int i = 1; i <=4; i++) {
                if (i == correctOption)
                    answers.add(celebnames.get(celebIndex));
                else {
                    int wronganswer = r.nextInt(celebnames.size());
                    while (wronganswer == celebIndex) {
                        wronganswer = r.nextInt(celebnames.size());


                    }
                    answers.add(celebnames.get(wronganswer));
                }
            }

            option1.setText(answers.get(0));
            option2.setText(answers.get(1));
            option3.setText(answers.get(2));
            option4.setText(answers.get(3));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onClick(View view) {
        Button button = (Button) view;

        if(Integer.parseInt((String) button.getTag())==correctOption) {
            message = "CORRECT";
            correct.start();
        }else {
            message = "INCORRECT! it's " + celebnames.get(celebIndex);
            incorrect.start();
        }
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();

        nextImage();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        option1 = findViewById(R.id.one);
        option2 = findViewById(R.id.two);
        option3 = findViewById(R.id.three);
        option4 = findViewById(R.id.four);

        correct = MediaPlayer.create(this, R.raw.correct);
        incorrect = MediaPlayer.create(this, R.raw.incorrect);

        DownloadTask task = new DownloadTask();
        try {
            result=task.execute("https://www.imdb.com/list/ls052283250/").get();

            Pattern p = Pattern.compile("src=\"(.*?).jpg\"");
            Matcher m = p.matcher(result);
            while(m.find()){
                celeburls.add(m.group(1)+".jpg");
            }
            p=Pattern.compile("<img alt=\"(.*?)\"");
            m=p.matcher(result);
            while(m.find()){
                celebnames.add(m.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        nextImage();
        // Log.i("result",result);
    }


    public class ImageDownloader extends AsyncTask<String,Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            URL url ;
            HttpURLConnection connection = null;

            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {

            String content ="";
            URL url ;
            HttpURLConnection connection = null;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    stringBuilder.append(current);
                    data = reader.read();
                }
                content = stringBuilder.toString();
                return content;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}