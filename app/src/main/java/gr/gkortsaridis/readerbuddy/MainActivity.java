package gr.gkortsaridis.readerbuddy;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.Toast;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> bookPaths,bookNames,bookAuthors;
    ArrayList<Bitmap> bookCover;
    ArrayList<Integer> bookPages,bookProgress;
    ProgressDialog pd;
    File startingFolder;
    SharedPreferences sharedprefs;
    GridView books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedprefs = getSharedPreferences("mybooks",Context.MODE_PRIVATE);
        pd = new ProgressDialog(MainActivity.this);

        bookPaths = new ArrayList<>();
        bookNames = new ArrayList<>();
        bookAuthors = new ArrayList<>();
        bookPages = new ArrayList<>();
        bookCover = new ArrayList<>();
        bookProgress = new ArrayList<>();

        books = (GridView) findViewById(R.id.books);

        startingFolder = new File(getIntent().getStringExtra("path"));
        AsyncTaskRunner aa = new AsyncTaskRunner();
        aa.execute(startingFolder);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(startingFolder.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    class AsyncTaskRunner extends AsyncTask<File, String, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setTitle("Loading books");
            pd.setCancelable(false);
            if(!pd.isShowing())pd.show();
        }

        @Override
        protected Integer doInBackground(File... params) {
            Log.i("DO","IN BAck");
            String pdfPattern = ".pdf";
            final File listFile[] = params[0].listFiles();

            if (listFile != null) {
                boolean found_at_least_one_pdf = false;
                for (int i = 0; i < listFile.length; i++) {

                    if (listFile[i].getName().endsWith(pdfPattern)){
                        Log.i("PDF",listFile[i].getAbsolutePath());
                        bookPaths.add(listFile[i].getAbsolutePath());
                        bookProgress.add(sharedprefs.getInt(listFile[i].getAbsolutePath()+"_lastPage",1));

                        PdfReader pdf = null;
                        try {
                            pdf = new PdfReader(listFile[i].getAbsolutePath());
                            String bookName = pdf.getInfo().get("Title");
                            if(bookName == null){
                                bookNames.add("UNKNOWN TITLE");
                                publishProgress("UNKNOWN TITLE");
                            }else{
                                bookNames.add(bookName);
                                publishProgress(bookName);
                            }

                            String bookAuthor = pdf.getInfo().get("Author");
                            if(bookAuthor == null){
                                bookAuthors.add("UNKNOWN AUTHOR");
                            }else{
                                bookAuthors.add(bookAuthor);
                            }

                            bookPages.add(pdf.getNumberOfPages());

                            PdfReaderContentParser parser = new PdfReaderContentParser(pdf);
                            parser.processContent(1, new RenderListener() {

                                boolean first = true;
                                @Override
                                public void beginTextBlock() {
                                    if(first){
                                        first = false;
                                        Bitmap icon = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.not_available);
                                        bookCover.add(icon);
                                    }
                                }

                                @Override
                                public void renderText(TextRenderInfo renderInfo) {
                                    if(first){
                                        first = false;
                                        Bitmap icon = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.library);
                                        bookCover.add(icon);
                                    }
                                }

                                @Override
                                public void endTextBlock() {
                                    if(first){
                                        first = false;
                                        Bitmap icon = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.library);
                                        bookCover.add(icon);
                                    }
                                }

                                @Override
                                public void renderImage(ImageRenderInfo renderInfo) {

                                    if (first) {
                                        PdfImageObject image = null;
                                        try {
                                            image = renderInfo.getImage();
                                            //bookCovers.add(image.getImageAsBytes());
                                            BitmapFactory.Options options = new BitmapFactory.Options();
                                            options.inScaled = false;
                                            ByteArrayInputStream in = new ByteArrayInputStream(image.getImageAsBytes());
                                            Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(in), 250, 400, false);
                                            bookCover.add(myBitmap);

                                            if (image == null) {
                                                return;
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                }
                            });

                            pdf.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.i("PDF","CATCH");

                        }
                        found_at_least_one_pdf = true;
                    }
                }

                if(!found_at_least_one_pdf){
                    return -1;
                }

            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer == -1){
                Toast.makeText(MainActivity.this,"No PDFs Found in this folder",Toast.LENGTH_SHORT).show();
            }else {

                ArrayList<String> collection;
                collection = loadArray();
                boolean foundInCollention = false;
                Log.i("PDF",collection.toString());
                for(int i=0; i<collection.size(); i++){
                    if(collection.get(i).equals(startingFolder.getAbsolutePath())){
                        saveArray(collection);
                        foundInCollention = true;
                        break;
                    }
                }
                if(!foundInCollention) {
                    Log.i("PDF","Gonna Save collection");
                    collection.add(startingFolder.getAbsolutePath());
                    saveArray(collection);
                }


                books.setAdapter(new BookAdapter(bookPaths,bookNames,bookAuthors,bookCover,bookPages,bookProgress,getBaseContext()));
                books.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        File file = new File(bookPaths.get(position));

                        if (file.exists()) {
                            /*Uri path = Uri.fromFile(file);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(path, "application/pdf");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(MainActivity.this, "No Application Available to View PDF", Toast.LENGTH_SHORT).show();
                            }*/
                            Intent intent = new Intent(MainActivity.this,BookReadingActivity.class);
                            intent.putExtra("book",file.getAbsolutePath());
                            startActivity(intent);

                        }else{
                            Toast.makeText(getBaseContext(),"File no longer exists",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                /*
                books.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        return false;
                    }
                });*/
            }
            pd.dismiss();
        }

        public void saveArray(ArrayList<String> list){

            SharedPreferences settings = getBaseContext().getSharedPreferences("mybooks", 0);
            SharedPreferences.Editor editor = settings.edit();

            int size = list.size();
            editor.putInt("list_size", size);

            for (int i = 0; i < size; i++) {
                editor.remove("list_"+i);
            }
            for (int i = 0; i < size; i++) {
                editor.putString("list_"+i, list.get(i));
            }
            editor.commit();
        }

        public ArrayList<String> loadArray(){

            SharedPreferences file = getBaseContext().getSharedPreferences("mybooks", 0);
            ArrayList<String> list = new ArrayList<>();
            int size = file.getInt("list_size", 0);

            for(int i = 0; i<size;i++){
                String x = file.getString("list_"+i, "");
                list.add(x);
            }
            return list;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pd.setMessage(values[0]);
        }
    }

}
