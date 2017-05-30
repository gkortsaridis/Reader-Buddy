package gr.gkortsaridis.readerbuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.tekle.oss.android.animation.AnimationFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by yoko on 04/06/16.
 */
public class BookAdapter extends BaseAdapter {

    ArrayList<String> bookPath;
    ArrayList<String> bookName;
    ArrayList<String> bookAuthor;
    ArrayList<Bitmap> bookCovers;
    ArrayList<Integer> bookPages;
    ArrayList<Integer> bookProgress;
    ArrayList<Book> books;
    Context context;
    LayoutInflater layoutInflater;

    public BookAdapter(ArrayList<String> bookPath, ArrayList<String> bookName, ArrayList<String> bookAuthor,ArrayList<Bitmap> bookCovers,ArrayList<Integer> bookPages,ArrayList<Integer> bookProgress, Context context){
        this.bookPath = bookPath;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.context = context;
        this.bookCovers = bookCovers;
        this.bookPages = bookPages;
        this.bookProgress = bookProgress;
        this.books = new ArrayList<>();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return bookPath.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Book{
        TextView bookName;
        TextView bookAuthor;
        TextView bookPage;
        ImageView bookCover;
        ProgressBar bookProgress;
        ViewFlipper flipper;
    }

    Book book;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        book = new Book();
        books.add(book);

        View rowView;
        rowView = layoutInflater.inflate(R.layout.book_list_item,null);
        book.bookName = (TextView) rowView.findViewById(R.id.bookTitle);
        book.bookAuthor = (TextView) rowView.findViewById(R.id.bookAuthor);
        book.bookCover = (ImageView) rowView.findViewById(R.id.bookCover);
        book.bookPage = (TextView) rowView.findViewById(R.id.bookPages);
        book.bookProgress = (ProgressBar) rowView.findViewById(R.id.progressBar);
        book.flipper = (ViewFlipper) rowView.findViewById(R.id.my_view_flipper);

        book.bookName.setText(new File(bookPath.get(position)).getName());
        book.bookAuthor.setText(bookAuthor.get(position));
        book.bookPage.setText(bookPages.get(position)+" pages");
        book.bookCover.setImageBitmap(bookCovers.get(position));
        book.bookProgress.setMax(bookPages.get(position)-1);
        book.bookProgress.setProgress(bookProgress.get(position)-1);

        setFlipper();

        return rowView;
    }

    public void setFlipper(){
        for(int position=0; position<books.size(); position++){
            final int finalPosition = position;
            books.get(position).flipper.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if(books.get(finalPosition).flipper.getDisplayedChild() == 0){
                        for(int i=0; i<books.size(); i++){
                            if(i != finalPosition){
                                if(books.get(i).flipper.getDisplayedChild() == 1){
                                    AnimationFactory.flipTransition(books.get(i).flipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
                                    books.get(i).flipper.setDisplayedChild(0);
                                }
                            }
                        }
                        AnimationFactory.flipTransition(books.get(finalPosition).flipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
                        books.get(finalPosition).flipper.setDisplayedChild(1);
                    }
                    else{
                        AnimationFactory.flipTransition(books.get(finalPosition).flipper, AnimationFactory.FlipDirection.LEFT_RIGHT);
                        books.get(finalPosition).flipper.setDisplayedChild(0);
                    }

                    return true;
                }
            });

            books.get(position).flipper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(books.get(finalPosition).flipper.getDisplayedChild() == 0) {
                        Intent intent = new Intent(context, BookReadingActivity.class);
                        intent.putExtra("book", bookPath.get(finalPosition-1));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

}
