package uk.ac.ox.ibme.camerademo;


//imports from mod 7 (image)
        import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.media.Image;
        import android.net.Uri;
        import android.os.Environment;
        import android.provider.MediaStore;
        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.Toast;
        import android.content.SharedPreferences;

        import java.io.File;
        import java.io.IOException;

//imports from mod 6 (map)
        import android.app.AlertDialog;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.graphics.Color;
        import android.location.Criteria;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.os.Bundle;
        import android.support.v4.app.FragmentActivity;
        import android.util.Log;
        import android.widget.Button;
        import android.widget.SeekBar;
        import android.widget.TextView;

        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.android.gms.maps.model.PolylineOptions;

        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.FileReader;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.OutputStreamWriter;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.List;
        import java.util.Map;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Environment;
        import android.support.v4.app.FragmentManager;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.AdapterView.OnItemClickListener;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.Toast;
        import android.os.Vibrator;

//-----------------------------------MAIN ACTIVITY------------------------------------------------
public class SecondActivity extends FragmentActivity {
    SharedPreferences SP;
    private LatLng previous_position = null;
    String[] Array_Longitudes;  //longitude of each point
    String[] Array_Latitudes;   //latitude of each point
    String[] Array_Imgs;        //contains absolute paths of each image
    private Button nextButton;
    private Button back;
    int i = 0;



    //---------------------------Retriving Locations and Images------------------------

    String Longitudes;

    String Latitudes;

    String ImgPaths;


    Location PreviousLocation = null;
    //-------------------------------------------------------------------------------




    //-----------------SET UP IMAGE AND MAP VIEW-------------------------------------
    private ImageView imgView2;
    private GoogleMap mMap;

    //--------------------------initialize map---------------------------
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {

            SupportMapFragment smf = null;
            //TODO EX 2: Get the SupportMapFragment from the getSupportFragmentManager()
            // by using the findFragmentById and passing the R.id.map
            smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


            //TODO EX 2: Get the map from the SupportMapFragment.
            smf.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;

                    // This method initialise the blue dot that show our current location on the map
                    if (mMap != null) {
                        mMap.setMyLocationEnabled(true);

                    }
                }
            });


        }

    }
    //-------------------------------------------------------------------------------






    //-----------------------------ON CREATE------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SP = getSharedPreferences("SP", MODE_PRIVATE);



        back = (Button) findViewById(R.id.back);

        Longitudes = SP.getString("Location_Longitudes", "");
        Array_Longitudes = Longitudes.split(",");

        Latitudes = SP.getString("Location_Latitudes", "");
        Array_Latitudes = Latitudes.split(",");

        ImgPaths = SP.getString("Picture_Locations", "");
        Array_Imgs = ImgPaths.split(",");



        //-------set up map and image-----------
        setUpMapIfNeeded();
        imgView2 = (ImageView) findViewById(R.id.imgView2);




        //--------"NEXT LOCATION" BUTTON---------------
        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Array_Longitudes.length == 0 || (Array_Longitudes.length == 1 && Array_Longitudes[0] == "")) {
                    return;
                }


                i = (i+1) % (Array_Longitudes.length);

                // Get instance of Vibrator from current Context
                Vibrator v = (Vibrator) getSystemService(SecondActivity.this.VIBRATOR_SERVICE);



                // Vibrate for 400 milliseconds
                v.vibrate(400);




                //------------------------------MAP----------------------------------------
                //co-ordinates on map
                double longitude = Double.parseDouble(Array_Longitudes[i]);
                double latitude = Double.parseDouble(Array_Latitudes[i]);


                Location l = new Location("");
                l.setLongitude(longitude);
                l.setLatitude(latitude);


                LatLng position = new LatLng(l.getLatitude(), l.getLongitude());

                if (PreviousLocation == null) {
                    PreviousLocation = l;
                }

                //----------------map------------------
                //move the map into new position
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
                MarkerOptions mo = null;

                mo = new MarkerOptions();
                mo.title("Location " + position);
                mo.position(position);



                if (mo != null) {
                    mMap.addMarker(mo);
                }

                if (previous_position != null) {
                    // plotting the line between points
                    PolylineOptions options = new PolylineOptions();

                    options.add(position);
                    options.add(previous_position);

                    options.color(Color.BLUE);
                    mMap.addPolyline(options);



                }
                //This is so that the previous_position is advanced to the new value
                previous_position = position;


                //------------------------------------------------------------------------


                //------------------------------IMAGE----------------------------------------
                //TODO: (7.2) write code to acquire a Bitmap object of the image
//                BitmapFactory.Options opts=new BitmapFactory.Options();
//                opts.inDither=false;                     //Disable Dithering mode
//                opts.inTempStorage=new byte[32 * 1024];
//
//
//                Bitmap bm = BitmapFactory.decodeFile(Array_Imgs[i]);

                Uri uri = Uri.parse(Array_Imgs[i]);


                imgView2.setImageURI(uri);
                //------------------------------------------------------------------------



            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondActivity.this, MainActivity.class));
            }
        });


    }

}