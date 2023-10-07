package com.jol.cosmicsymphony;

import static com.jol.cosmicsymphony.Constants.waterBodiesInfoList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.mapbox.maps.extension.style.layers.properties.generated.TextTransform;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    List<String> lakeNameList = new ArrayList<>();
    ActivityMainBinding binding;
    MapboxMap mapboxMap;
    double currentZoom = 16.0;
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

    private void updateData() {
        for (WaterBodiesInfo info : waterBodiesInfoList) {
            int position=waterBodiesInfoList.indexOf(info);
            String waterBodyName = lakeNameList.get(waterBodiesInfoList.indexOf(info));
            Log.wtf("Water Body:", waterBodyName);
            double latitude = info.getWater_bodies().get(lakeNameList.get(waterBodiesInfoList.indexOf(info))).getLocation().getLat();
            double longitude = info.getWater_bodies().get(lakeNameList.get(waterBodiesInfoList.indexOf(info))).getLocation().getLng();
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

                    Intent intent=new Intent(MainActivity.this, WaterBodyActivity.class);
                    intent.putExtra("lakeTitle",waterBodyName);
                    intent.putExtra("position",position);
                    startActivity(intent);

                    return false;
                }
            });

            CameraAnimationsPlugin camera = CameraAnimationsUtils.getCamera(binding.mapView);
            camera.flyTo(
                    new CameraOptions.Builder()
                            .zoom(currentZoom)
                            .center(point)
                            .build(),
                    new MapAnimationOptions.Builder()
                            .interpolator(new AccelerateDecelerateInterpolator())
                            .duration(1000).build()
            );

            GesturesUtils.getGestures(binding.mapView)
                    .setPinchToZoomDecelerationEnabled(true);


        }
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