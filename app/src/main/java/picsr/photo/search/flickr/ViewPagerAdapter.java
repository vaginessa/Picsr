package picsr.photo.search.flickr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Kashif on 3/3/2017.
 */

public class ViewPagerAdapter extends PagerAdapter {

    final String TAG = this.getClass().getSimpleName();
    Context activity;
    ArrayList<DataModel> dataList = new ArrayList<>();
    LayoutInflater inflater;

    public ViewPagerAdapter(Activity activity, ArrayList<DataModel> dataList){
        this.activity = activity;
        this.dataList = dataList;
        Log.d(TAG, "ViewPagerAdapter dataList.size()="+dataList.size() );
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position){

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate( R.layout.full_image, container, false );

        Log.d( TAG, "URL="+dataList.get(position).constructURL() );

        PhotoView image = (PhotoView) rootView.findViewById(R.id.image);

        Glide.with(activity)
                .load( dataList.get(position).constructURL() )
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(image);

        ImageButton shareButton = (ImageButton) rootView.findViewById(R.id.ibShare);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadBitmap( dataList.get(position).constructURL(), ShareAction.SHARE );
            }
        });

        ImageButton downloadButton = (ImageButton) rootView.findViewById(R.id.ibDownload);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadBitmap( dataList.get(position).constructURL(), ShareAction.DOWNLOAD );
            }
        });

        ((ViewPager) container).addView(rootView);
        return rootView;
    }

    enum ShareAction{
      SHARE,
        DOWNLOAD
    };

    private void downloadBitmap(String url, final ShareAction action){
        Glide.with(activity)
                .load( url )
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        shareBitmap( resource, action );
                    }
                });
    }

    private void shareBitmap (Bitmap bitmap, ShareAction action) {
        try {

            Random random = new Random();
            String filename = "Picsr_"+ random.nextInt(9999) +".jpg";

            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), activity.getString(R.string.app_name) );
            if(!folder.exists())
                folder.mkdir();

            File file = new File(folder, filename);
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            Log.d(TAG, "FILE saved to"+file.getPath() );

            if( action == ShareAction.SHARE ) {

                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setType("image/png");

                activity.startActivity(intent);
            }else{
                Toast.makeText(activity, "Picture saved in Pictures directory", Toast.LENGTH_SHORT).show();
                //for showing image in gallery
                MediaScannerConnection.scanFile(activity, new String[]{file.getPath()}, new String[]{"image/png"}, null );
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error - "+e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

}
