package picsr.photo.search.flickr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ImageViewActivity extends AppCompatActivity {

    TouchImageView ivFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        ivFull = (TouchImageView) findViewById(R.id.tivFull);

        try {

            String data = getIntent().getExtras().getString("data");

            if (data != null && !data.isEmpty()) {
                Glide.with(this)
                        .load(data)
                        .centerCrop()
                        .crossFade()
                        .thumbnail(0.5f)
                        .into(ivFull);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show();
            finish();
        }

    }


}
