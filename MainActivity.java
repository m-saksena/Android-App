package uk.ac.ox.ibme.camerademo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;


public class MainActivity extends Activity {
    int TAKE_PHOTO_CODE = 1;

    private ImageView imgView;
    private Button viewjourney;
    private Button resetjourney;
    Button capture = null;

    int count=1;
    int show=0;
    // A variable that will keep the state of the last known location
    private Location lastKnownLocation;
    // A variable that will contains the information about
    // the Map we are receiving from the Google API
    private GoogleMap mMap;
    private LatLng previous_position = null;
    private boolean locationAvailable = false;
    private LocationManager locationManager = null;

    private int position_index ;
    private int displacementRadiusValue = 5;
    private int timeDelayValue = 5;

    SharedPreferences SP;

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            if (capture!= null) {
                capture.setActivated(true);
                capture.setClickable(true);
            }

            // Store location data every time the location changes
            lastKnownLocation = location;





            if (locationAvailable && lastKnownLocation != null) {
                Log.d(this.toString(), "geo: " + lastKnownLocation.getLongitude() + "," + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getAltitude());
            }


            // Check if the mMap is initialised
            if (mMap != null && lastKnownLocation != null) {

                // Get the Position as a specific LatLng object. This object represents a location.
                LatLng position = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                if (previous_position == null) {
                    previous_position = position;
                }
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view2);


        SP = getSharedPreferences("SP", MODE_PRIVATE);


        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);



        // Vibrate for 400 milliseconds
        v.vibrate(400);

        //attaching imgView with the layout
        imgView = (ImageView) findViewById(R.id.imgView);
        //creating a 'capture' Button and attaching it with the layout
        capture = (Button) findViewById(R.id.btnCapture);
        capture.setActivated(false);
        capture.setClickable(false);


        resetjourney = (Button) findViewById(R.id.resetjourney);

        // initialise button to allow us to change images


        //listener for the 'capture' button called as soon as the button is pressed
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                //create a directory in the external storage
                File imagesFolder = new File(Environment.getExternalStorageDirectory()+ "/MyImages");
                imagesFolder.mkdirs();

                File image = new File(imagesFolder, "project_" + count+ ".jpg");

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //create a URI that identifies the file
                Uri uriImage = Uri.fromFile(image);
                //package the URI created (uriImage) in the cameraIntent
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
                //TODO: (7.1) write code to call the activity using the camera intent, and a request code (TAKE_PHOTO_CODE)

                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
                Log.d("MyApp", "Yo, count is" +  count);

                //get location data



            }
        });


        resetjourney.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                SP.edit().clear().commit();

                imgView.setImageResource(android.R.color.transparent);

                count = 1;

                final TextView tw = (TextView) findViewById(R.id.abc);


                tw.setText("Welcome Back! You have 0 pictures");
                tw.setTextColor(Color.BLUE);
                tw.setTextSize(40);
                return true;
            }
        });




        viewjourney = (Button) findViewById(R.id.viewjourney);

        viewjourney.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));

                checkLocations();

            }
        });

    }


    public void checkLocations() {


        String [] Array_Of_longitudes = SP.getString("Location_Longitudes","").split(",");
        String [] Array_Of_latitudes = SP.getString("Location_Latitudes","").split(",");
        String [] Array_Of_pictures = SP.getString("Picture_Locations","").split(",");


        for (int ii=0; ii < Array_Of_latitudes.length; ii = ii +1) {
            Log.e("THIS PATH", "POSITION: " + Array_Of_longitudes[ii] + " " + Array_Of_latitudes[ii]);
            Log.e("THIS PATH", "IMAGE: " + Array_Of_pictures[ii]);
            Log.e("THIS PATH", "---------------------------------------------------------------");
        }


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK ) {

            //get a reference to the location of the image file
            File imagesFolder = new File(Environment.getExternalStorageDirectory()+ "/MyImages");
            File image = new File(imagesFolder, "project_" + count+ ".jpg");

            Bitmap bit = BitmapFactory.decodeFile(image.getAbsolutePath());



            Location l = lastKnownLocation;

            if (lastKnownLocation == null) {
                return;
            }


            double longitude = l.getLongitude();
            double latitude = l.getLatitude();


            String Array_Of_longitudes = SP.getString("Location_Longitudes","");
            Array_Of_longitudes = Array_Of_longitudes + longitude + ",";
            SP.edit().putString("Location_Longitudes", Array_Of_longitudes).commit();

            String Array_Of_latitudes = SP.getString("Location_Latitudes","");
            Array_Of_latitudes = Array_Of_latitudes + latitude + ",";
            SP.edit().putString("Location_Latitudes", Array_Of_latitudes).commit();


            String Array_Of_pictures = SP.getString("Picture_Locations","");
            Array_Of_pictures = Array_Of_pictures + image.getAbsolutePath() +",";
            SP.edit().putString("Picture_Locations",Array_Of_pictures).commit();



            imgView.setImageBitmap(bit);
            count++;
            Log.d("-------------------","" + count);

        }

    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d("my app", "In the onPause() event");

        // Here we do the same for the location manager
        locationManager.removeUpdates(locationListener);

        // and we do not forget to call the parent's method
        super.onPause();

    }


    @Override
    protected void onResume() {

        // Here we activate the location module.
        // It also 'attach' the location Listener
        activateLocationManager();

        super.onResume();

        count = SP.getString("Location_Longitudes","").split(",").length;
        // Let's grab the TextView inside the Layout...
        final TextView tw = (TextView) findViewById(R.id.abc);


        tw.setText("Welcome Back! You have " + (count) + " pictures");
        tw.setTextColor(Color.BLUE);
        tw.setTextSize(40);


        if (count < 0) {
            count = 1;
        }

    }




    /*
     * This method activates the LocationManager and
     * attach the listener for the location updates
     */
    private void activateLocationManager() {

        position_index = 0;

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Here we select the best provider of location available that fit some criteria and is available.

        // We set this flag to false and later we check the result to understand if the location is
        // available
        locationAvailable = false;


        // If the bestProvider is not accessible and/or enabled
        if ( !locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) ) {
            // .. we crerate a custom AlertDialog (as seen in Module 3) that will
            // initiate the Intent to activate the Location management for the phone
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your LOCATION provider seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick( final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }


        // In case the location manager and the bestProvider are not empty and previous checks succeeded...
        if ( locationManager != null ) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, timeDelayValue*1000, displacementRadiusValue*10, locationListener);
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locationAvailable = true;
        }

    }


}


