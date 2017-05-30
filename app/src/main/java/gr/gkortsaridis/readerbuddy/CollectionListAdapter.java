package gr.gkortsaridis.readerbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by yoko on 06/06/16.
 */
public class CollectionListAdapter extends BaseAdapter{

    ArrayList<String> collectionNames;
    LayoutInflater layoutInflater;

    public CollectionListAdapter(ArrayList<String> collectionNames, Context context){
        this.collectionNames = collectionNames;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return collectionNames.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class CollectionItem{
        ImageView itemImage;
        TextView itemName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = layoutInflater.inflate(R.layout.collection_item_list,null);

        CollectionItem item = new CollectionItem();
        item.itemImage = (ImageView) rowView.findViewById(R.id.iv_icon);
        item.itemName = (TextView) rowView.findViewById(R.id.tv_name);

        File temp = new File(collectionNames.get(position));
        item.itemName.setText(temp.getName());

        return rowView;
    }

}
