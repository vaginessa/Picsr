package picsr.photo.search.flickr;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class ImageViewActivity extends AppCompatActivity {

    final String TAG = this.getClass().getSimpleName();
    ArrayList<DataModel> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        try {

            dataList = (ArrayList<DataModel>) getIntent().getSerializableExtra("dataList");
            Log.d(TAG, "intent data dataList.size() = "+dataList.size() );
            int position = getIntent().getExtras().getInt("position");

            if( dataList!=null && dataList.size()>0 ){
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, dataList);
                viewPager.setAdapter(viewPagerAdapter);
                viewPager.setCurrentItem(position);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show();
            finish();
        }

    }


}
