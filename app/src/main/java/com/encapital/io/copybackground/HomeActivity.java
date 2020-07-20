package com.encapital.io.copybackground;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.encapital.io.copybackground.service.FloatingViewService;
import com.google.gson.Gson;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private Button btStartScript, btShowFileContent;
    private EditText etPeriod;
    private RecyclerView rvData;
    private static final int FILE_SELECT_CODE = 0;
    private Gson gson= new Gson();

    private File getCopyScriptFile() {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator +Environment.DIRECTORY_DOWNLOADS;
        String fileName = "copy_script.txt";
        File copyScriptFile = new File(baseDir + File.separator + fileName);
        return copyScriptFile;
    }

    private List<String> readFileScript() {
        List<String> data = new ArrayList<>();
        File copyScriptFile = getCopyScriptFile();
        if (!copyScriptFile.exists()) {
            Toast.makeText(HomeActivity.this, "File không tồn tại", Toast.LENGTH_SHORT).show();
        }

        data = FileUtils.readFile(copyScriptFile);
        if (data.size() <= 0) {
            Toast.makeText(HomeActivity.this, "File không có nội dung", Toast.LENGTH_SHORT).show();
        }
        return data;
    }

    private void handlerShowFile(){
        List<String> dataScript = readFileScript();
        rvData.setAdapter(new LineAdapter(dataScript));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        MySharedPreferences.init(getApplicationContext());


        etPeriod = findViewById(R.id.etPeriod);
        btStartScript = findViewById(R.id.btStartScript);
        btShowFileContent = findViewById(R.id.btShowFileContent);
        rvData = findViewById(R.id.rvData);

        // in content do not change the layout size of the RecyclerView
        rvData.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvData.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvData.getContext(),
                layoutManager.getOrientation());
        rvData.addItemDecoration(dividerItemDecoration);

        etPeriod.setText(String.valueOf(MySharedPreferences.getPeriod()));

        handlerShowFile();
        btShowFileContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerShowFile();
            }
        });

        btStartScript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int period = 0;
                try {
                    period = Integer.parseInt(etPeriod.getText().toString());
                } catch (Exception ignored) {

                }
                ;

                if (period <= 0) {
                    etPeriod.setError("Giá trị không hợp lệ");
                    return;
                }
                List<String> dataScript = readFileScript();
                if (dataScript.size() > 0) {
                    MySharedPreferences.setPeriod(period);
                    MySharedPreferences.setScriptCopy(gson.toJson(dataScript));
                    startFloatView();
                }

            }
        });


    }

    private void startFloatView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            initializeView();
        }
    }

    private void initializeView() {
        startService(new Intent(HomeActivity.this, FloatingViewService.class));
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CODE_DRAW_OVER_OTHER_APP_PERMISSION:
                //Check if the permission is granted or not.
                if (resultCode == RESULT_OK) {
                    initializeView();
                } else { //Permission is not available
                    Toast.makeText(this,
                            "Draw over other app permission not available. Closing the application",
                            Toast.LENGTH_SHORT).show();

                    finish();
                }
                break;
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    // Get the path
                    assert uri != null;
                    String path = null;
//                    try {
//                        path = FileUtils.getPath(HomeActivity.this, uri);
//                        etFile.setText(String.valueOf(path));
//
//                    } catch (URISyntaxException e) {
//                        e.printStackTrace();
//                    }
//                        Log.e("thepss",path);
//                    etFile.setText(uri.getPath().toString());
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
