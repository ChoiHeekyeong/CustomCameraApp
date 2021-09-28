package com.example.cameraexample6;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.HashMap;


public class GoogleMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    DataDTO dataDTO = new DataDTO();
    MarkerOptions mOptions = new MarkerOptions();
    View marker_googlemap;
    ImageView markerimg;
    HashMap<String, String> markerMap = new HashMap<>(); //마커 id값과 사진uri값을 저장할 해시맵

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlemap);

        marker_googlemap = LayoutInflater.from(this).inflate(R.layout.marker_googlemap, null);
        markerimg = marker_googlemap.findViewById(R.id.markerimg);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //firebase값 읽어오기
        //databaseReference.child("users2").addChildEventListener(
        databaseReference.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String markerId;

                HashMap<String, HashMap<String, String>> picInfo = new HashMap<>(); //해시맵 선언

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
//                    Log.d("key",""+key.toString());
//                    Log.d("value",""+postSnapshot.getValue());
                    HashMap<String, String> value = (HashMap<String, String>) postSnapshot.getValue();
                    picInfo.put(key, value);
//                    Log.d("key",""+picInfo.get(key).get("latitude").toString());
                    String stringLat = String.valueOf(picInfo.get(key).get("latitude"));
                    String stringLon = String.valueOf(picInfo.get(key).get("longitude"));
                    String pictureUri = picInfo.get(key).get("pictureUri");
                    //위도경도는 Double로 변환
                    Double latitude = Double.parseDouble(stringLat);
                    Double longitude = Double.parseDouble(stringLon);

                    dataDTO.setLatitude(latitude);
                    dataDTO.setLongitude(longitude);
                    dataDTO.setPictureUri(pictureUri);
//                    Log.d("Pic",""+dataDTO.getPictureUri());
//                    markerimg.setImageDrawable();
                    Glide.with(getApplicationContext()).load(pictureUri).override(700, 200).into(markerimg);
                    //getApplicationContext() 대신 GoogleActivity.this써도 O


                    mOptions.position(new LatLng(dataDTO.getLatitude(), dataDTO.getLongitude())) //위치 셋팅
                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(marker_googlemap))); //커스텀마커 적용

                    // 마커(핀) 추가
//                    mMap.addMarker(mOptions);
                    Marker marker = mMap.addMarker(mOptions);
                    markerId = marker.getId(); //마커 아이디
                    markerMap.put(markerId, dataDTO.getPictureUri()); //(마커 아이디 : 사진uri) 해시맵에 넣기

                }

                Log.d("총갯수?@@@", "" + markerMap.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        LatLng seoul = new LatLng(37.5654401, 126.9459492);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));

        mMap.setOnMarkerClickListener(this); //onMarkerClick 실행을 위한 메소드



    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        //커스텀 모달창으로 띄울 xml
        View modal = View.inflate(this, R.layout.modal, null);
        ImageView modalImg = modal.findViewById(R.id.modalimg);


        String modalId = marker.getId();    //선택한 마커 id값 가져오기
        String modalUri = markerMap.get(marker.getId());    //이미지주소
//        System.out.println("id값:"+modalId);

        for (int i = 0; i < markerMap.size(); i++) {
            if (modalId.equals("m" + i)) {
                Glide.with(getApplicationContext()).load(modalUri).override(700, 200).into(modalImg);
            }
        }


        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setMessage("View")
                .setView(modal)
                .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();


        return false;
    }


    // View를 Bitmap으로 변환
    private Bitmap createDrawableFromView(View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        (GoogleMapActivity.this).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


}