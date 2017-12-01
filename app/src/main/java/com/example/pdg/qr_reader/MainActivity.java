package com.example.pdg.qr_reader;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Vibrator;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity {

    @BindView(R.id.cameraView)
    SurfaceView cameraView;

    @BindView(R.id.result)
    TextView resultTextView;

    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private final int cameraRequest = 1001;

    private final String CLASSNAME = getClass().getSimpleName();

    private String barcodeTemp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();


        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Log.i(CLASSNAME, "surfaceCreated");
                try {
                    DisplayManager displayManager = new DisplayManager(MainActivity.this);
                    DisplayMetrics diplayMetrics = displayManager.getDiplayMetrics();

                    cameraSource = new CameraSource
                            .Builder(MainActivity.this, barcodeDetector)
                            .setRequestedPreviewSize(diplayMetrics.heightPixels, diplayMetrics.widthPixels)
                            .setAutoFocusEnabled(true)
                            .build();

                    cameraSource.start(cameraView.getHolder());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                Log.i(CLASSNAME, "surfaceChanged");

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Log.i("barcodeDetector", "release");
//                resultTextView.setText("");
//                resultTextView.setVisibility(View.GONE);
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCode = detections.getDetectedItems();

                if (qrCode.size() != 0) {
                    final String result = qrCode.valueAt(0).displayValue;// 값이 들어옴

                    if (!barcodeTemp.equals(result)) {
                        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                        long[] pattern = {500,0,0,500};

                        vibrator.vibrate(pattern, -1); // -1 한번만 진동
                    }

                    barcodeTemp = result;

                    resultTextView.post(new Runnable() {
                        @Override
                        public void run() {

                            resultTextView.setVisibility(View.VISIBLE);
                            resultTextView.setText(result);
                        }
                    });
                } else {

                }

            }
        });
    }




}
