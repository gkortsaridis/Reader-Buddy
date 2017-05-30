package gr.gkortsaridis.readerbuddy;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;
import java.util.ArrayList;

public class BookReadingActivity extends AppCompatActivity {

    Toolbar toolbar;
    SharedPreferences sharedprefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_reading);
        String filePath = getIntent().getExtras().getString("book");
        final File file = new File(filePath);

        toolbar = (Toolbar) findViewById(R.id.bookToolbar);
        setSupportActionBar(toolbar);

        PDFView pdfView = (PDFView) findViewById(R.id.pdfview);
        pdfView.fromFile(file)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        getSupportActionBar().setTitle(page+"/"+pageCount+" "+file.getName());
                        savePage(file,page);
                    }
                })
                .defaultPage(loadPage(file))
                .showMinimap(false)
                .enableSwipe(true)
                .load();

    }

    public int loadPage(File book){
        sharedprefs = getSharedPreferences("mybooks", 0);
        return sharedprefs.getInt(book.getAbsolutePath()+"_lastPage", 0);
    }

    public void savePage(File book,int page){
        sharedprefs = getSharedPreferences("mybooks", 0);
        SharedPreferences.Editor editor = sharedprefs.edit();
        editor.putInt(book.getAbsolutePath()+"_lastPage",page);
        editor.commit();
    }

}
