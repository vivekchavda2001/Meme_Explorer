package com.encycode.memesexplorer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

public class MemeAdapter extends RecyclerView.Adapter<MemeAdapter.MovieViewholder>{
    private Context context;
    private List<Meme> items;
    RelativeLayout relativeLayout;

    public MemeAdapter(Context context, List<Meme> items,RelativeLayout relativeLayout) {
        this.context = context;
        this.items = items;
        this.relativeLayout = relativeLayout;
    }


    @NonNull
    @Override
    public MovieViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.meme_view,parent,false);
        return new MovieViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieViewholder holder, int position) {
        final Meme item = items.get(position);
        holder.MemeTitle.setText(item.getTitle());

        Glide
                .with(context)
                .load(item.getUrl())
                .override(1000,1000)
                .fitCenter()
                .into(holder.MemeImage);
        holder.author.setText("Author: "+item.getAuthor());
        holder.upVotes.setText(""+item.getUps());
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.checkImageAwailable()){
                    shareMeme(holder.MemeImage);
                }else{
                    SnackBarCall("Meme Not Loaded Properly");
                }
            }
        });

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(holder.checkImageAwailable()){
                        saveImage(holder.MemeImage);
                    }else{
                        SnackBarCall("Meme Not Loaded Properly");
                    }


                } catch (IOException e) {
                    SnackBarCall("Error Occured");
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public class MovieViewholder extends RecyclerView.ViewHolder{
        ImageView MemeImage,download,share;
        TextView MemeTitle,author,upVotes;
        public MovieViewholder(@NonNull View itemView) {
            super(itemView);
            MemeImage = itemView.findViewById(R.id.memeImage);
            MemeTitle = itemView.findViewById(R.id.memeTitle);
            author = itemView.findViewById(R.id.author);
            upVotes = itemView.findViewById(R.id.upvotes);
            download = itemView.findViewById(R.id.download);
            share = itemView.findViewById(R.id.share);

        }
        public Boolean checkImageAwailable(){
            if(MemeImage.getDrawable() == null){
                return false;
            }
            return true;
        }
    }
    public void saveImage(ImageView memeImage) throws IOException {
        //Toast.makeText(context, ""+((MainActivity)context).permissionValidate, Toast.LENGTH_SHORT).show();

        if(((MainActivity)context).permissionValidate){
            GlideBitmapDrawable draw = (GlideBitmapDrawable) memeImage.getDrawable();
            Bitmap bitmap = draw.getBitmap();
            FileOutputStream outStream = null;
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/Dcim/MemeShare");
            dir.mkdirs();
            String fileName = String.format("%d.jpg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);
            try {
                outStream = new FileOutputStream(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

            try {
                outStream.flush();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            SnackBarCall("Meme Saved in "+dir+"/"+fileName);
        }else{

            ((MainActivity)context).checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
        }



    }
    private void shareMeme(ImageView memeImage){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        GlideBitmapDrawable drawable = (GlideBitmapDrawable) memeImage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        try {
            File file = new File(context.getExternalCacheDir(), File.separator +"temp.jpg");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photoURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID +".provider", file);

            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/jpg");

            context.startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
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
