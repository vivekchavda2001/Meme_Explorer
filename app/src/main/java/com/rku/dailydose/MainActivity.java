package com.rku.dailydose;

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
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relativeLayout = findViewById(R.id.mainLay);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N)
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_CODE);
        getSupportActionBar().setTitle("Memes Now");
        recyclerView = findViewById(R.id.movieList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        getData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.refresh){
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, "Refreshed", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getData(){
        final Call<MemeList> memeList = FetchMeme.getMemeService().getMemeList();
        memeList.enqueue(new Callback<MemeList>() {
            @Override
            public void onResponse(Call<MemeList> call, Response<MemeList> response) {
                MemeList list= response.body();

                recyclerView.setAdapter(new MemeAdapter(MainActivity.this,list.getMemes(),relativeLayout));
                Snackbar snackbar = Snackbar
                        .make(relativeLayout, "Memes Loaded", Snackbar.LENGTH_LONG);
                snackbar.show();

            }

            @Override
            public void onFailure(Call<MemeList> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Internet connection not Awailable", Toast.LENGTH_SHORT).show();

            }
        });
    }
    public void checkPermission(String permission, int requestCode){
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            MainActivity.this,
                            new String[] { permission },
                            requestCode);
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
        if(requestCode != WRITE_EXTERNAL_STORAGE_CODE){
            Toast.makeText(this, "Permission ", Toast.LENGTH_SHORT).show();
        }
    }
}