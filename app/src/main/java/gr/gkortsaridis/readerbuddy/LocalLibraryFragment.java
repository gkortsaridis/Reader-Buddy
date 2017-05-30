package gr.gkortsaridis.readerbuddy;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 *
 * @Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
WebView mWebView=new WebView(MyPdfViewActivity.this);
mWebView.getSettings().setJavaScriptEnabled(true);
mWebView.getSettings().setPluginsEnabled(true);
mWebView.loadUrl("https://docs.google.com/gview?embedded=true&url="+LinkTo);
setContentView(mWebView);
}
 *
 */
public class LocalLibraryFragment extends Fragment {

    GridView booklist;
    SharedPreferences sharedprefs;
    ArrayList<String> collections;
    Set<String> bookCollections;
    View view;
    AdapterView.OnItemClickListener listener;

    public LocalLibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Proceed with your code execution
            view.findViewById(R.id.fabLocal).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processFile();
                }
            });

        }else{
            view.findViewById(R.id.fabLocal).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),"You need to Grand READ_EXTERNAL_STORAGE permission first...",Toast.LENGTH_LONG).show();
                }
            });
        }

        //booklist = (ListView) view.findViewById(R.id.boolist);
        sharedprefs = getActivity().getSharedPreferences("mybooks", Context.MODE_PRIVATE);

        collections = new ArrayList<>();
        collections = loadArray();

        if(collections == null){
            Toast.makeText(getContext(),"No Book Collections Yet...",Toast.LENGTH_SHORT).show();
        }else {
            booklist.setAdapter(new CollectionListAdapter(collections, getContext()));
            booklist.setOnItemClickListener(listener);
            booklist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                    final AdapterView.OnItemClickListener click = booklist.getOnItemClickListener();
                    booklist.setOnItemClickListener(null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setMessage("Delete this book collection?");

                    alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            booklist.setOnItemClickListener(listener);
                            collections.remove(position);
                            saveArray(collections);
                            booklist.setAdapter(new CollectionListAdapter(collections, getContext()));

                        }
                    });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            booklist.setOnItemClickListener(listener);
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                    return false;
                }
            });
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_local_library, container, false);
        booklist = (GridView) view.findViewById(R.id.booklist);

        listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(),MainActivity.class);
                intent.putExtra("path",collections.get(position));
                startActivity(intent);
            }
        };

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Local Book Collections");


        return view;
    }

    private void processFile(){
        final FileChooser filechooser = new FileChooser(getActivity());
        filechooser.setFileListener(new FileChooser.FileSelectedListener() {
            @Override
            public void fileSelected(final File file) {
                // ....do something with the file
                String filename = file.getAbsolutePath();
                filename = filename.substring(0,filename.length()-"SELECT THIS FOLDER".length());
                Log.d("FOLDER", filename);
                filechooser.cancelDialog();

                Intent intent = new Intent(getContext(),MainActivity.class);
                intent.putExtra("path",filename);
                startActivity(intent);
            }
        });
        filechooser.showDialog();
    }

    public ArrayList<String> loadArray(){

        SharedPreferences file = getActivity().getSharedPreferences("mybooks", 0);
        ArrayList<String> list = new ArrayList<>();
        int size = file.getInt("list_size", 0);
        if(size == 0) return null;

        for(int i = 0; i<size;i++){
            String x = file.getString("list_"+i, "");
            list.add(x);
        }
        return list;
    }

    public void saveArray(ArrayList<String> list){

        SharedPreferences settings = getActivity().getSharedPreferences("mybooks", 0);
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

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }




}
