package com.jol.cosmicsymphony;

import static com.jol.cosmicsymphony.Constants.waterBodiesInfoList;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jol.cosmicsymphony.data.WaterBodiesInfo;
import com.jol.cosmicsymphony.databinding.ActivityMainBinding;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraBoundsOptions;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.observable.eventdata.CameraChangedEventData;
import com.mapbox.maps.extension.observable.eventdata.MapIdleEventData;
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor;
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin;
import com.mapbox.maps.plugin.animation.CameraAnimationsUtils;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.delegates.listeners.OnCameraChangeListener;
import com.mapbox.maps.plugin.delegates.listeners.OnMapIdleListener;
import com.mapbox.maps.plugin.gestures.GesturesUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    List<String> lakeNameList = new ArrayList<>();
    ActivityMainBinding binding;
    MapboxMap mapboxMap;
    double currentZoom = 16.0;
    double myLat = 0, myLng = 0;
    private static final int REQUEST_LOCATION = 1;
    WaterBodiesInfo.Location nearestLocation;

    Bitmap icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        mapboxMap = binding.mapView.getMapboxMap();


        mapboxMap.loadStyleUri(Style.MAPBOX_STREETS/*, new Style.OnStyleLoaded() {
            @Override public void onStyleLoaded(@NonNull Style style) {
                mapboxMap.setCamera(new CameraOptions.Builder().zoom(20.0).build());
                LocationComponentPlugin locationComponentPlugin = getLocationComponent(binding.mapView);
                locationComponentPlugin.setEnabled(true);
                LocationPuck2D locationPuck2D = new LocationPuck2D();
                locationPuck2D.setBearingImage(AppCompatResources.getDrawable(MapBoxActivity.this, R.drawable.baseline_location_on_24));
                locationComponentPlugin.setLocationPuck(locationPuck2D);
                //locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
                //locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                getGestures(binding.mapView).addOnMoveListener(new OnMoveListener() {
                    @Override
                    public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {

                    }

                    @Override
                    public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
                        return false;
                    }

                    @Override
                    public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

                    }
                });
            }
        }*/);
        binding.mapView.setMaximumFps(30);
        // binding.shimmerLayout.startShimmer();


        getLocation();


        Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.buspin);
        icon = bmp.copy(Bitmap.Config.ARGB_8888, true);


        CameraBoundsOptions cameraBoundsOptions = new CameraBoundsOptions.Builder()
                .maxZoom(17.5)
                .build();
        mapboxMap.setBounds(cameraBoundsOptions);

        mapboxMap.addOnMapIdleListener(new OnMapIdleListener() {
            @Override
            public void onMapIdle(@NonNull MapIdleEventData mapIdleEventData) {

            }
        });

        mapboxMap.addOnCameraChangeListener(new OnCameraChangeListener() {
            @Override
            public void onCameraChanged(@NonNull CameraChangedEventData cameraChangedEventData) {
                currentZoom = mapboxMap.getCameraState().getZoom();

            }

        });

        binding.nearestWaterBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myLat != 0) {
                    flyToNearestWaterBody();
                }
            }
        });


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("water_bodies");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (waterBodiesInfoList.size() > 0) {
                    waterBodiesInfoList.clear();
                    lakeNameList.clear();
                }
                for (DataSnapshot lakeSnap : snapshot.getChildren()) {
                    lakeNameList.add(lakeSnap.getKey());
                    WaterBodiesInfo.LakeInfo info = lakeSnap.getValue(WaterBodiesInfo.LakeInfo.class);
                    WaterBodiesInfo waterBodiesInfo = new WaterBodiesInfo();
                    Map<String, WaterBodiesInfo.LakeInfo> water_bodies = new HashMap<>();
                    water_bodies.put(lakeSnap.getKey(), info);
                    waterBodiesInfo.setWater_bodies(water_bodies);
                    waterBodiesInfoList.add(waterBodiesInfo);
                }

                if (binding.animationView.isAnimating()) {
                    binding.animationView.cancelAnimation();
                    fadeOutAndHideView(binding.animationView);
                    fadeInAndShowView(binding.mapView);
                }

                updateData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void flyToNearestWaterBody() {
        CameraAnimationsPlugin camera = CameraAnimationsUtils.getCamera(binding.mapView);
        Point point = Point.fromLngLat(nearestLocation.getLng(), nearestLocation.getLat());
        camera.flyTo(
                new CameraOptions.Builder()
                        .zoom(17.0)
                        .center(point)
                        .build(),
                new MapAnimationOptions.Builder()
                        .interpolator(new AccelerateDecelerateInterpolator())
                        .duration(1100).build()
        );
    }

    private void getLocation() {
        LocationManager locationManager;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                    myLat = bestLocation.getLatitude();
                    myLng = bestLocation.getLongitude();
                    Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                            R.drawable.person);
                    icon = bmp.copy(Bitmap.Config.ARGB_8888, true);

                    Point point = Point.fromLngLat(myLng, myLat);
                    CameraAnimationsPlugin camera = CameraAnimationsUtils.getCamera(binding.mapView);
                    camera.easeTo(
                            new CameraOptions.Builder()
                                    .zoom(currentZoom)
                                    .center(point)
                                    .build(),
                            new MapAnimationOptions.Builder()
                                    .interpolator(new AccelerateDecelerateInterpolator())
                                    .duration(1000).build()
                    );

                    PointAnnotationOptions options = new PointAnnotationOptions()
                            .withIconImage(icon)
                            .withIconSize(0.1)
                            .withIconAnchor(IconAnchor.BOTTOM)
                            .withPoint(point);

                    AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(binding.mapView);
                    PointAnnotationManager manager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, new AnnotationConfig());
                    manager.setIconKeepUpright(true);
                    manager.create(options);


                }
            }


        }
    }

    private void updateData() {
        HashMap<String, Float> unsortedMap = new HashMap<String, Float>();
        for (WaterBodiesInfo info : waterBodiesInfoList) {
            int position = waterBodiesInfoList.indexOf(info);
            String waterBodyName = lakeNameList.get(waterBodiesInfoList.indexOf(info));
            Log.wtf("Water Body:", waterBodyName);
            double latitude = info.getWater_bodies().get(lakeNameList.get(waterBodiesInfoList.indexOf(info))).getLocation().getLat();
            double longitude = info.getWater_bodies().get(lakeNameList.get(waterBodiesInfoList.indexOf(info))).getLocation().getLng();

            Location locationA = new Location("point A");
            locationA.setLatitude(myLat);
            locationA.setLongitude(myLng);

            Location locationB = new Location("point B");
            locationB.setLatitude(latitude);
            locationB.setLongitude(longitude);


            float distance = locationA.distanceTo(locationB);
            distance = distance / 1000;
            Log.wtf("Distance", "" + distance);
            unsortedMap.put(waterBodyName, distance);


            Point point = Point.fromLngLat(longitude, latitude);
            PointAnnotationOptions options = new PointAnnotationOptions()
                    .withIconImage(icon)
                    .withIconSize(0.1)
                    .withIconAnchor(IconAnchor.BOTTOM)
                    .withTextField(waterBodyName)
                    .withTextColor(Color.RED)
                    .withTextHaloColor(Color.WHITE)
                    .withTextHaloWidth(10)
                    .withTextHaloBlur(25)
                    .withPoint(point);

            AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(binding.mapView);
            PointAnnotationManager manager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, new AnnotationConfig());
            manager.setIconKeepUpright(true);
            manager.create(options);
            manager.addClickListener(new OnPointAnnotationClickListener() {
                @Override
                public boolean onAnnotationClick(@NonNull PointAnnotation pointAnnotation) {
                    Log.wtf("Clicked", pointAnnotation.getTextField());

                    Intent intent = new Intent(MainActivity.this, WaterBodyActivity.class);
                    intent.putExtra("lakeTitle", waterBodyName);
                    intent.putExtra("position", position);
                    startActivity(intent);

                    return false;
                }
            });


            GesturesUtils.getGestures(binding.mapView)
                    .setPinchToZoomDecelerationEnabled(true);


        }

        Log.wtf("Unsorted", unsortedMap.toString());

        List<Map.Entry<String, Float>> list = new ArrayList<>(unsortedMap.entrySet());

        // Sort the list using a custom Comparator
        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                // Compare the values (numerical data) in ascending order
                return Float.compare(o1.getValue(), o2.getValue());
            }
        });

        // Create a new LinkedHashMap to preserve the sorted order
        Map<String, Float> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Float> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        Log.wtf("Unsorted", sortedMap.toString());
        Map.Entry<String, Float> firstEntry = sortedMap.entrySet().iterator().next();
        String firstKey = firstEntry.getKey();
        Log.wtf("First Key: ", firstKey);

        prepareNearestBody(firstKey);


    }

    private void prepareNearestBody(String nearestWaterBody) {

        int position = lakeNameList.indexOf(nearestWaterBody);
        nearestLocation = waterBodiesInfoList.get(position).getWater_bodies().get(nearestWaterBody)
                .getLocation();


    }

    private void fadeInAndShowView(final View view) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(500);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        view.startAnimation(fadeIn);
    }

    private void fadeOutAndHideView(final View view) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        view.startAnimation(fadeOut);
    }
}