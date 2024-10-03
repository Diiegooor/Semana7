package com.example.actividadsemana7diego;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView mImageView;
    private Bitmap mBitmap;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.mImageView);
        Button loadImageButton = findViewById(R.id.loadImageButton);

        if (savedInstanceState != null) {
            mBitmap = savedInstanceState.getParcelable("image");
            if (mBitmap != null) {
                mImageView.setImageBitmap(mBitmap);
            }
        }

        loadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadImageTask().execute("https://aguacatec.es/wp-content/uploads/2023/10/e5a978b8-6772-4c85-a50e-15581af7d483.png");
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];

            if (Math.abs(x) > Math.abs(y))
            {
                mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {

                mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBitmap != null) {
            outState.putParcelable("image", mBitmap);
        }
    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            return loadImageFromNetwork(urls[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                mBitmap = bitmap;
                mImageView.setImageBitmap(mBitmap);
            } else {
                Toast.makeText(MainActivity.this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }

        private Bitmap loadImageFromNetwork(String urlString) {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return bitmap;
        }
    }
}
