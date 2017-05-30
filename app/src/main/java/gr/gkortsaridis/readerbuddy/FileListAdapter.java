package gr.gkortsaridis.readerbuddy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by yoko on 05/06/16.
 */
public class FileListAdapter extends BaseAdapter {

    String[] names;
    Context context;
    LayoutInflater layoutInflater;
    int dirsLength;

    public FileListAdapter(String[] names, Context context, int dirsLength){
        this.names = names;
        this.context = context;
        this.dirsLength = dirsLength;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Item {
        TextView name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = new Item();
        View rowView;
        rowView = layoutInflater.inflate(R.layout.file_list_item,null);

        item.name = (TextView) rowView.findViewById(R.id.itemName);
        item.name.setText(names[position]);

        if(position > dirsLength+1){
            rowView.setEnabled(false);
            rowView.setClickable(false);
            rowView.setOnClickListener(null);
            item.name.setEnabled(false);
        }

        return rowView;
    }
}
