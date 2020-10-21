package com.encycode.memesexplorer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    public static Boolean permissionValidate=false;
    private Dialog dialog;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT <= 23){
            permissionValidate = true;
        }
        relativeLayout = findViewById(R.id.mainLay);
         getSupportActionBar().setTitle("Memes Explorer");
        recyclerView = findViewById(R.id.movieList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
        if (item.getItemId() == R.id.abtus) {
            loadAboutus();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getData() {
        final Call<MemeList> memeList = FetchMeme.getMemeService().getMemeList();
        memeList.enqueue(new Callback<MemeList>() {
            @Override
            public void onResponse(Call<MemeList> call, Response<MemeList> response) {
                MemeList list = response.body();
                recyclerView.setAdapter(new MemeAdapter(MainActivity.this, list.getMemes(), relativeLayout));
                SnackBarCall("Memes Loaded");
            }

            @Override
            public void onFailure(Call<MemeList> call, Throwable t) {
                SnackBarCall("Internet connection not Awailable");
            }
        });
    }

    public void loadAboutus() {
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.about_main);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void checkPermission(String permission, int requestCode) {
        //Check for rational
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
           final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.permision_denied_dialog);
            dialog.setCanceledOnTouchOutside(true);
            TextView yes = dialog.findViewById(R.id.yes);
            TextView no = dialog.findViewById(R.id.no);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_CODE);
                    dialog.dismiss();

                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        } else {
            // Checking if permission is not granted
            if (ContextCompat.checkSelfPermission(
                    MainActivity.this,
                    permission)
                    == PackageManager.PERMISSION_DENIED) {

                ActivityCompat
                        .requestPermissions(
                                MainActivity.this,
                                new String[]{permission},
                                requestCode);


            } else {
                permissionValidate = true;
            }
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            permissionValidate = true;

        }else {

            final Snackbar snackbar = Snackbar
                    .make(relativeLayout, "Permission Denied", Snackbar.LENGTH_LONG);
            snackbar.setAction("Grant Permission", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    snackbar.dismiss();
                }
            });
            snackbar.show();
            permissionValidate = false;
        }
    }
    private void SnackBarCall(String message){
        final Snackbar snackbar = Snackbar
                .make(relativeLayout, ""+message, Snackbar.LENGTH_LONG);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();

    }
}