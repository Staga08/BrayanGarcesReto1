    package com.example.brayangarcesreto2;

    import android.Manifest;
    import android.annotation.SuppressLint;
    import android.app.AlertDialog;
    import android.content.pm.PackageManager;
    import android.location.Address;
    import android.location.Geocoder;
    import android.location.Location;
    import android.location.LocationManager;
    import android.os.Build;
    import android.os.Bundle;
    import android.os.Looper;
    import android.view.Gravity;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.FrameLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.RequiresApi;
    import androidx.core.app.ActivityCompat;
    import androidx.fragment.app.FragmentActivity;

    import com.google.android.gms.location.FusedLocationProviderClient;
    import com.google.android.gms.location.LocationCallback;
    import com.google.android.gms.location.LocationRequest;
    import com.google.android.gms.location.LocationResult;
    import com.google.android.gms.location.LocationServices;
    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.OnMapReadyCallback;
    import com.google.android.gms.maps.SupportMapFragment;
    import com.google.android.gms.maps.model.BitmapDescriptorFactory;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.Marker;
    import com.google.android.gms.maps.model.MarkerOptions;
    import com.google.android.material.floatingactionbutton.FloatingActionButton;
    import com.google.android.material.snackbar.Snackbar;
    import com.google.maps.android.SphericalUtil;

    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Locale;

    public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

        private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 1;
        private GoogleMap mMap;
        private Geocoder geocoder;
        private Location myLocation;
        private LocationManager locationManager;
        private FloatingActionButton addMarker;
        private FusedLocationProviderClient mFusedLocationClient;
        private LocationRequest locationRequest;
        private LocationCallback locationCallback;
        private boolean isThere = false;
        private Marker myMarker;
        private Marker customM;
        private LatLng customP;
        private ArrayList<Marker> markers;
        private Location customL;
        private TextView locationTxt;
        SupportMapFragment mapFragment;
        private boolean btnClicked = false;

        private String markerName;
        AlertDialog dialog;

    //RECORDAR QUE SE PUEDEN QUITAR LOS CONDICIONALES DE LOS REQUEST DE PERMISOS, NO SON NECESARIOS-DOMI



        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            addMarker = findViewById(R.id.addMarker);
            locationTxt=findViewById(R.id.locationTxt);
            addMarker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    btnClicked=true;
                    openDialog();

                    Snackbar snack = Snackbar.make(view, "Por favor señala la ubicación en la que quieres poner tu marcador", Snackbar.LENGTH_LONG);
                    View view2 = snack.getView();
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view2.setLayoutParams(params);
                    snack.show();

                }
            });


            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            geocoder = new Geocoder(this, Locale.getDefault());
            markers= new ArrayList();

            askForPermission();


            }




        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            List<Address> myAdress = null;
            String address = null;


            mMap.setOnMapClickListener((v) -> {
                if(btnClicked){

                    customP = v;
                    customL= new Location("custom Location");
                    customL.setLongitude(v.longitude);
                    customL.setLatitude(v.latitude);
                    try {

                        List<Address> myAdres = geocoder.getFromLocation(customL.getLatitude(), customL.getLongitude(), 1);
                        //String addres = myAdres.get(0).getAddressLine(0);
                        LatLng myCurrentLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

                        customM = mMap.addMarker(new MarkerOptions().position(customP).title(markerName).icon(BitmapDescriptorFactory.fromResource((R.drawable.pin1))));
                        double distance = SphericalUtil.computeDistanceBetween(customM.getPosition(), myCurrentLocation);
                        customM.setSnippet("Usted esta a "+ String.format("%.1f", distance)+" mts de aqui.");
                        btnClicked=false;
                        markers.add(customM);
                         updateInfo();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }


            });


        }

    public void openDialog(){
        AlertDialog.Builder mBuilder= new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.layout_dialog, null);
        EditText customMarker= (EditText) mView.findViewById(R.id.markerName);
        Button buttonOk =(Button) mView.findViewById(R.id.buttonOk);

        buttonOk.setOnClickListener((v)->{

            if(!customMarker.getText().toString().isEmpty()){
                Toast.makeText(this, "Por favor señala la ubicación en la que quieres poner tu marcador", Toast.LENGTH_SHORT).show();
                markerName=customMarker.getText().toString();
                dialog.cancel();

            }else{
                Toast.makeText(this, "No le has puesto aun un nombre a tu marcador", Toast.LENGTH_SHORT).show();
            }
        });

        mBuilder.setView(mView);
         dialog=mBuilder.create();
        dialog.show();

    }



        @Override
        public void onClick(View v) {

        }

        @SuppressLint("NewApi")
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_READ_LOCATION: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Gracias por aceptar el permiso, disfruta de la experiencia", Toast.LENGTH_SHORT).show();
                        //Si tengo los permisos luego ya cargo el getMapAsync() IMPORTANTISIMO
                        mapFragment.getMapAsync(this);
                        getToMe();


                    } else {
                        Toast.makeText(this, "El permiso ha sido negado, no es posible ejecutar la app", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

            }
        }


        public void askForPermission() {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_LOCATION);

        }


        public void getToMe() {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(500);
            mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    myLocation = locationResult.getLastLocation();

                    List<Address> myAdress = null;
                    String address = null;

                    try {
                        myAdress = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
                        address = myAdress.get(0).getAddressLine(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LatLng myPos = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    if (!isThere) {
                        myMarker = mMap.addMarker(new MarkerOptions().position(myPos).title(address).icon(BitmapDescriptorFactory.fromResource((R.drawable.marker1))));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 18));
                        isThere = true;
                    } else {
                        myMarker.setPosition(myPos);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 18));
                    }

                }

            }, Looper.myLooper());
        }





        public String theDistanceIs(){

            String nearest = "";
            double[] distances = new double[markers.size()];

            LatLng myCurrentLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            for (int i = 0; i < distances.length; i++) {

                distances[i] = SphericalUtil.computeDistanceBetween(myCurrentLocation, markers.get(i).getPosition());

            }

            double min = distances[0];
            int indexMin = 0;

            for (int i = 1; i < distances.length; i++) {

                if (distances[i] < min) {

                    min = distances[i];
                    indexMin = i;

                }

            }


            if (distances[indexMin] < 20) {

                nearest += "\n Usted se encuentra en " + markers.get(indexMin).getTitle();

            } else {

                nearest += "\n El lugar más cercano es " + markers.get(indexMin).getTitle();

            }


            return nearest;

        }

        public void updateInfo() {

            if (markers.size() > 0) {
                locationTxt.setText(theDistanceIs());
            } else {

                locationTxt.setText("\n No hay marcadores agregados.");

            }

        }
    }



