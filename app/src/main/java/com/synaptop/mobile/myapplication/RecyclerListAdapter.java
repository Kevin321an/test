package com.synaptop.mobile.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.synaptop.mobile.myapplication.data.Places;

import java.util.ArrayList;
import java.util.Locale;

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder> {
    public static final String LOG_TAG = RecyclerListAdapter.class.getSimpleName();

    public static final int PAGE_TYPE_LIST = 1;
    public static final int PAGE_TYPE_GRID = 2;

    private Context mContext;
    private int pageType;
    ArrayList<Places> mCursor;
    private Activity activity;

    View rootView;
    public RecyclerListAdapter(Context context, int pageType) {
        this.pageType = pageType;
        mContext = context;
        rootView = ((Activity) mContext).getWindow().getDecorView().findViewById(android.R.id.content);
    }


    /**
     * create a new RecyclerView.ViewHolder and initializes some private fields to be used by RecyclerView.
     *
     * @param viewGroup parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType  viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using onBindViewHolder(ViewHolder, int, List).
     * Since it will be re-used to display different items in the data set,
     * it is a good idea to cache references to sub views of the View to avoid unnecessary findViewById(int) calls.
     */
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        if (pageType == PAGE_TYPE_LIST) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);
        } else if (pageType == PAGE_TYPE_GRID) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_grid, viewGroup, false);
        } else {
            Log.v("the pageType no get", LOG_TAG);
            return null;
        }
        return new ItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        Places is = mCursor.get(position);
        holder.title.setText(is.title);
        holder.url.setText(is.url);

        final String url = is.url;
        final float lat = is.lat;
        final float lng = is.lng;
        //open url link in browser
        holder.cardV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                mContext.startActivity(i);
            }
        });

        //open spot in Map by lat and lng
        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, lng);
                Log.v(LOG_TAG, "lat and lng in view adapter" + lat + "" + lng);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mContext.startActivity(intent);
            }
        });

        // load pictures
        switch (is.title) {
            case "Ripley's Aquarium":
                Picasso.with(mContext).load(R.drawable.aquarium).resize(80, 80).into(holder.img);
                break;
            case "CN Tower":
                Picasso.with(mContext).load(R.drawable.cntower).resize(80, 80).into(holder.img);
                break;
            case "Toronto Zoo":
                Picasso.with(mContext).load(R.drawable.torontozoo).resize(80, 80).into(holder.img);
                break;
            case "Royal Ontario Museum":
                Picasso.with(mContext).load(R.drawable.rom).resize(80, 80).into(holder.img);
                break;
            case "Art Gallery of Ontario":
                Picasso.with(mContext).load(R.drawable.artgalleryontario).resize(80, 80).into(holder.img);
                break;
            case "Yorkdale Mall":
                Picasso.with(mContext).load(R.drawable.yorkdalemall).resize(80, 80).into(holder.img);
                break;
            case "Eaton Center":
                Picasso.with(mContext).load(R.drawable.torontoeatoncentre).resize(80, 80).into(holder.img);
                break;
            case "City Hall":
                Picasso.with(mContext).load(R.drawable.torontocityhall).resize(80, 80).into(holder.img);
                break;
            case "Hockey Hall of Fame":
                Picasso.with(mContext).load(R.drawable.images).resize(80, 80).into(holder.img);
                break;
            case "Air Canada Center":
                Picasso.with(mContext).load(R.drawable.images).resize(80, 80).into(holder.img);
                break;
            default:
                break;
        }

    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.size();
        //return mItems.size();
    }
    //get the data from main activity
    public void swapCursor(ArrayList newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public CardView cardV;
        private TextView url, title;
        private ImageView img, location;

        public ItemViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            cardV = (CardView) itemView.findViewById(R.id.card_view);
            url = (TextView) itemView.findViewById(R.id.url);
            title = (TextView) itemView.findViewById(R.id.title);
            location = (ImageView) itemView.findViewById(R.id.location);
        }
    }


}