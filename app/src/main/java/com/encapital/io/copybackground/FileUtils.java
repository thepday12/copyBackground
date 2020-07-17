package com.encapital.io.copybackground;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static void showFileChooser( Activity context, int FILE_SELECT_CODE) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf"); //text/plain
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            context.startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(context, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        String tempID= "", id ="";
        String actualfilepath="";
        if (uri.getAuthority().equals("media")){
            tempID =   uri.toString();
            tempID = tempID.substring(tempID.lastIndexOf("/")+1);
            id = tempID;
            Uri contenturi = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String selector = MediaStore.Images.Media._ID+"=?";
            actualfilepath = getColunmData( context,contenturi, selector, new String[]{id}  );


        }else if (uri.getAuthority().equals("com.android.providers.media.documents")){
            tempID = DocumentsContract.getDocumentId(uri);
            String[] split = tempID.split(":");
            String type = split[0];
            id = split[1];
            Uri contenturi = null;
            if (type.equals("image")){
                contenturi = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            }else if (type.equals("video")){
                contenturi = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            }else if (type.equals("audio")){
                contenturi = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }
            String selector = "_id=?";
            actualfilepath = getColunmData(context, contenturi, selector, new String[]{id}  );

        } else if (uri.getAuthority().equals("com.android.providers.downloads.documents")){

            tempID =   uri.toString();
            tempID = tempID.substring(tempID.lastIndexOf("/")+1);
            id = tempID;
            Uri contenturi = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            // String selector = MediaStore.Images.Media._ID+"=?";
            actualfilepath = getColunmData(context, contenturi, null, null  );


        }else if (uri.getAuthority().equals("com.android.externalstorage.documents")){

            tempID = DocumentsContract.getDocumentId(uri);
            String[] split = tempID.split(":");
            String type = split[0];
            id = split[1];
            Uri contenturi = null;
            if (type.equals("primary")){
                actualfilepath=  Environment.getExternalStorageDirectory()+"/"+id;
            }


        }

        return null;
    }

    public static  String getColunmData(Context context, Uri uri, String selection, String[] selectarg){

        String filepath ="";
        Cursor cursor = null;
        String colunm = "_data";
        String[] projection = {colunm};
        cursor =  context.getContentResolver().query( uri, projection, selection, selectarg, null);
        if (cursor!= null){
            cursor.moveToFirst();
            filepath = cursor.getString(cursor.getColumnIndex(colunm));
        }
        if (cursor!= null)
            cursor.close();
        return  filepath;
    }

    public static  List<String> readFile(File file){
        // File file = new File(Environment.getExternalStorageDirectory(), "mytextfile.txt");
        List<String> result = new ArrayList<>();
        Log.e("main", "read start");
        try {

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine())!=null){
                result.add(line);
            }

            br.close();

        }catch (Exception ignored){
        }
//        textView.setText(builder.toString());
        return result;

    }
}
