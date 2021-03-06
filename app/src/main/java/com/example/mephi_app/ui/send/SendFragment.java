package com.example.mephi_app.ui.send;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mephi_app.MainActivity;
import com.example.mephi_app.R;
import com.example.mephi_app.IOpensJson;
import com.example.mephi_app.JSONHelper;
import com.example.mephi_app.NetworkTask;
import com.example.mephi_app.ui.LoadErrorMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class SendFragment extends Fragment implements IOpensJson {

    private final String lnk="http://192.168.1.7:3000/home/getqr?nam=";
    private final String lnkpost = "getqr?nam=";
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    private MainActivity ma;
    private static Button butt;
    private static WebView wv;
    private static TextView tv;
    private LoadErrorMessage lem;

    private String contents;
    private qr info;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SendViewModel sendViewModel = ViewModelProviders.of(this).get(SendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_send, container, false);
        //final TextView textView = root.findViewById(R.id.text_send);
        sendViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        if (getActivity() != null) { ma = (MainActivity ) getActivity(); }

        butt = (Button)root.findViewById(R.id.scanner_button);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQR(butt);
            }
        });

        wv = (WebView) root.findViewById(R.id.qr_view);
        tv = (TextView) root.findViewById(R.id.textView5);
        lem = root.findViewById(R.id.lem_qr);

        return root;
    }





    // Запуск сканера qr-кода:
    private void scanQR(View v) {
        try {

            // Запускаем переход на com.google.zxing.client.android.SCAN с помощью intent:
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {

            // Предлагаем загрузить с Play Market:
            showDialog(ma, "Сканер не найден", "Установить сканер с Play Market?", "Да", "Нет").show();
        }
    }

    // alert dialog для перехода к загрузке приложения сканера:
    private static AlertDialog showDialog(final Activity act, CharSequence title,
                                          CharSequence message,CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                // Ссылка поискового запроса для загрузки приложения:
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    // Обрабатываем результат, полученный от приложения сканера:
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                // Получаем данные после работы сканера и выводим их в Toast сообщении:
                contents = intent.getStringExtra("SCAN_RESULT");
                String prefix = contents.substring(0,contents.indexOf(' '));
                try {
                    prefix = URLEncoder.encode(prefix,"utf-8");
                } catch (UnsupportedEncodingException e) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Не удалось считать QR-код. Пожалуйста, попробуйте ещё раз.")
                            .setTitle("QR-код");
                    AlertDialog dialog1 = builder1.create();
                    dialog1.show();
                    e.printStackTrace();
                }

                lem.changeStatus(LoadErrorMessage.LOAD_PROGRESS);
                NetworkTask task1 = new NetworkTask(this);
                Log.d("Connection", "URL: ma.lnkbase+lnkpost+prefix");
                task1.execute(ma.lnkbase+lnkpost+prefix);

            }
        }
    }

    @Override
    public void open(String jsonStr){
        JSONHelper helper1 = new JSONHelper(this,new QrJSONHelper());
        helper1.execute(jsonStr);
        ma.offline = false;
    }

    @Override
    public void swear(String swearing) {
        lem.changeStatus(LoadErrorMessage.LOAD_ERROR);
        String fullSwearing = "Ошибка открытия QR-кода. "+swearing;
        Log.d("Connection", fullSwearing);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(fullSwearing)
                .setTitle("Ошибка!")
                .setPositiveButton("OK", null);
        AlertDialog dialog1 = builder1.create();
        dialog1.show();
    }

    @Override
    public void displayJson(ArrayList a) {
        ma.showingQR = true;
        butt.setVisibility(View.GONE);
        if (a!= null){
            info = (qr)a.get(0);
            wv.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);
            wv.loadData(getString(R.string.web_start) + "<h4>" + info.name + "</h4><br>" + info.text + getString(R.string.web_end), "text/html; charset=utf-8", "utf-8");
        }
        else{
            wv.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            tv.setText("Данный QR-код не является QR-кодом аудитории. Его содержимое:\n"+contents);
        }
        lem.changeStatus(LoadErrorMessage.LOAD_FINISHED);
    }

    public static void closeQR(){
        wv.setVisibility(View.GONE);
        wv.loadData("", "text/html; charset=utf-8", "utf-8");
        tv.setVisibility(View.GONE);
        tv.setText("");
        butt.setVisibility(View.VISIBLE);
    }
}
/*
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.example.mephi_app.MainActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
public class SendFragment extends Fragment implements SurfaceHolder.Callback, Camera.PreviewCallback, Camera.AutoFocusCallback {
    private Camera camera;
    private SurfaceView preview;
    private ViewfinderView vfv;
    private Result rawResult;
    private String TAG = SendFragment.class.getSimpleName();
    private long currKey;
    MainActivity ma;
    CameraManager cm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) { ma = (MainActivity ) getActivity(); }
        ma.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ma.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ma.requestWindowFeature(Window.FEATURE_NO_TITLE);

        FrameLayout fl = new FrameLayout(ma);

        preview = new SurfaceView(ma);
        SurfaceHolder surfaceHolder = preview.getHolder();
        surfaceHolder.addCallback(this);
        fl.addView(preview, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        vfv = new ViewfinderView(ma, null);
        fl.addView(vfv, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ma.setContentView(fl);
        //cm = new CameraManager(ma.getApplication());
    }

    @Override
    public void onResume() {
        super.onResume();
        camera = Camera.open();
        //vfv.setCamera(camera);
        currKey = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Size previewSize = camera.getParameters().getPreviewSize();
        float aspect = (float) previewSize.width / previewSize.height;
        int previewSurfaceWidth = preview.getWidth();
        LayoutParams lp = preview.getLayoutParams();
        Camera.Parameters parameters = camera.getParameters();
        parameters.set("orientation", "landscape");
        camera.setParameters(parameters);
        lp.width = previewSurfaceWidth;
        lp.height = (int) (previewSurfaceWidth / aspect);
        preview.setLayoutParams(lp);
        try {
            camera.autoFocus(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onPreviewFrame(final byte[] bytes, final Camera camera) {
        new Recognizer(currKey, bytes).start();
    }

    @Override
    public void onAutoFocus(boolean b, Camera cam) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (camera!=null && (Camera.Parameters.FOCUS_MODE_AUTO.equals(camera.getParameters().getFocusMode()) ||
                        Camera.Parameters.FOCUS_MODE_MACRO.equals(camera.getParameters().getFocusMode()))) {
                    camera.autoFocus(SendFragment.this);
                }
            }
        }).start();
    }

    public class Recognizer extends Thread {

        private long key;
        private byte[] bytes;

        public Recognizer(long key, byte[] bytes) {
            this.key = key;
            this.bytes = bytes;
        }

        @Override
        public void run() {
            try {
                Size previewSize = camera.getParameters().getPreviewSize();
                Rect rect = vfv.getFramingRectInPreview();
                LuminanceSource source = new PlanarYUVLuminanceSource(bytes, previewSize.width, previewSize.height, rect.left, rect.top,
                        rect.width(), rect.height(), false);

                Map<DecodeHintType,Object> hints = new HashMap<DecodeHintType, Object>();
                Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>(1);
                decodeFormats.add(BarcodeFormat.QR_CODE);
                hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
                hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, new ResultPointCallback() {
                    @Override
                    public void foundPossibleResultPoint(ResultPoint resultPoint) {
                        vfv.addPossibleResultPoint(resultPoint);
                    }
                });
                MultiFormatReader mfr = new MultiFormatReader();
                mfr.setHints(hints);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                rawResult = mfr.decodeWithState(bitmap);
                if (rawResult!=null) {
                    Log.e(TAG, rawResult.getText()+" key="+key+" currKey="+currKey);
                    if (key==currKey) {
                        currKey = System.currentTimeMillis();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(QKActivity.this, ResultActivity.class);
                                i.putExtra(ResultActivity.RESULT, rawResult.getText());
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

*/