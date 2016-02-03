package ly.kite.photopicker.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import ly.kite.facebookphotopicker.R;

import java.util.List;

public class PhotoAdaptor extends PagingBaseAdaptor<Photo> {

    public PhotoAdaptor(List<Photo> itemList) {
        super(itemList);
    }

    public PhotoAdaptor() {
        super();
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public Photo getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Photo item = getItem(position);

        View view = null;

        if (convertView != null) {
            view = convertView;
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_photo, null);
            ViewHolder holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.imageview);
            holder.checkbox = (ImageView) view.findViewById(R.id.checkbox);
            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        ImageView imageView = holder.imageView;
        Picasso.with(parent.getContext()).load(item.getThumbnailURL().toString()).into(imageView);

        GridView gridView = (GridView) parent;

        holder.checkbox.setImageResource(gridView.isItemChecked(position) ? R.drawable.checkbox_on : R.drawable.checkbox_off);

        return view;
    }


    private static final class ViewHolder {
        ImageView imageView;
        ImageView checkbox;
    }


}
