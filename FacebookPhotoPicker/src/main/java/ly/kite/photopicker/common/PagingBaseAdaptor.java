package ly.kite.photopicker.common;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;


public abstract class PagingBaseAdaptor<T> extends BaseAdapter {

    protected List<T> mItemList;

    public PagingBaseAdaptor() {
        mItemList = new ArrayList<T>();
    }

    public PagingBaseAdaptor(List<T> items) {
        mItemList = items;
    }

    public void addMoreItems(List<T> newItems) {
        mItemList.addAll(newItems);

        notifyDataSetChanged();
    }

    public void removeAllItems() {
        mItemList.clear();

        notifyDataSetChanged();
    }

    public List<T> getItems() {
        return mItemList;
    }
}
