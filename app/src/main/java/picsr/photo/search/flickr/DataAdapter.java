package picsr.photo.search.flickr;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ContactViewHolder> {

    //This is a recyclerview adapter to bind data to recyclerview

    Context context;
    public static ArrayList<DataModel> allDataList = new ArrayList<>();
    final String TAG = "MyLog " + this.getClass().getSimpleName();

    public DataAdapter(Context context, ArrayList<DataModel> pdataList1) {

        this.allDataList = pdataList1;
        this.context = context;
    }


    @Override
    public int getItemCount() {
        return allDataList.size();
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder paramHolder, final int position) {

        //data is binded like this instead of traditional method to avoid recycling issue
        paramHolder.bindDataWithViewHolder(position);
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.pic_item, viewGroup, false);

        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView1;
        protected TextView textView1;
        protected ImageView imageView2;
        protected TextView textView2;

        protected Context context;

        AnimatorSet setRightOut;
        AnimatorSet setLeftIn;

        public ContactViewHolder(View v) {
            super(v);
            imageView1 = (ImageView) v.findViewById(R.id.imageView1);
            textView1 = (TextView) v.findViewById(R.id.textView1);
            imageView2 = (ImageView) v.findViewById(R.id.imageView2);
            textView2 = (TextView) v.findViewById(R.id.textView2);

            context = imageView1.getContext();

            setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                    R.animator.flight_right_out);

            setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                    R.animator.flight_left_in);


        }

        public void bindDataWithViewHolder(final int position) {



            //Glide library is used for faster loading of pictures, first half of quality is loaded then full quality is loaded, placeholder image is not shown for neatness, loading indicator is not supported in Glide hence it is not used.
            Glide.with(context)
                    .load(allDataList.get(position).constructURL())
                    .centerCrop()
                    .crossFade()
                    .thumbnail(0.5f)
                    .into(imageView1);

            //sedond column image
            Glide.with(context)
                    .load(allDataList.get(position).constructURL())
                    .centerCrop()
                    .crossFade()
                    .thumbnail(0.5f)
                    .into(imageView2);



            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                }
            });


            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    

                }
            });


        }


    }//ViewHolder


}


