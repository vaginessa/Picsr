package picsr.photo.search.flickr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
        protected Context context;

        public ContactViewHolder(View v) {
            super(v);
            imageView1 = (ImageView) v.findViewById(R.id.imageView1);
            context = imageView1.getContext();

        }


        public void bindDataWithViewHolder(final int position) {

            //Glide library is used for faster loading of pictures, first half of quality is loaded then full quality is loaded, placeholder image is not shown for neatness, loading indicator is not supported in Glide hence it is not used.
            Glide.with(context)
                    .load(allDataList.get(position).constructURL())
                    .centerCrop()
                    .crossFade()
                    .thumbnail(0.5f)
                    .into(imageView1);

            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

        }


    }//ViewHolder


}


