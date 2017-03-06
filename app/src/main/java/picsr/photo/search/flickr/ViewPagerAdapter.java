package picsr.photo.search.flickr;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

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
    public Object instantiateItem(ViewGroup container, int position){

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate( R.layout.full_image, container, false );

        Log.d( TAG, "URL="+dataList.get(position).constructURL() );

        TouchImageView touchImageView = (TouchImageView) rootView.findViewById(R.id.tivFull);
        Glide.with(activity)
                .load( dataList.get(position).constructURL() )
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(touchImageView)
        ;

        ((ViewPager) container).addView(rootView);
        return rootView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

}
