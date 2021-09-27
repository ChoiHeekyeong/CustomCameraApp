package com.example.cameraexample6;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.HashMap;


public class GoogleMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    DataDTO dataDTO = new DataDTO();
    MarkerOptions mOptions = new MarkerOptions();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlemap);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        //firebase값 읽어오기
        //databaseReference.child("users2").addChildEventListener(
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                HashMap<String, HashMap<String, String>> picInfo = new HashMap<>(); //해시맵 선언

                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
//                    Log.d("key",""+key.toString());
//                    Log.d("value",""+postSnapshot.getValue());
                    HashMap<String, String> value = (HashMap<String, String>) postSnapshot.getValue();
                    picInfo.put(key,value);
//                    Log.d("key",""+picInfo.get(key).get("latitude").toString());
                    String stringLat = String.valueOf(picInfo.get(key).get("latitude"));
                    String stringLon = String.valueOf(picInfo.get(key).get("longitude"));
                    String pictureUri = picInfo.get(key).get("pictureUri");
                    //위도경도는 Double로 변환
                    Double latitude = Double.parseDouble(stringLat);
                    Double longitude = Double.parseDouble(stringLon);

                    dataDTO.setLatitude( latitude );
                    dataDTO.setLongitude( longitude );
                    dataDTO.setPictureUri( pictureUri );

                    mOptions.position(new LatLng(dataDTO.getLatitude(), dataDTO.getLongitude()));

                    // 마커(핀) 추가
                    mMap.addMarker(mOptions);

                }




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

        // Add a marker in Seoul and move the camera
        LatLng seoul = new LatLng(37.5654401, 126.9459492);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
    }



//    @Override
//    public void onMapReady(final GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // 맵 터치 이벤트 구현 //
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
//            @Override
//            public void onMapClick(LatLng point) {
//                MarkerOptions mOptions = new MarkerOptions();
//                // 마커 타이틀
//                mOptions.title("마커 좌표");
//                Double latitude = point.latitude; // 위도
//                Double longitude = point.longitude; // 경도
//                // 마커의 스니펫(간단한 텍스트) 설정
//                mOptions.snippet(latitude.toString() + ", " + longitude.toString());
//                // LatLng: 위도 경도 쌍을 나타냄
//                mOptions.position(new LatLng(latitude, longitude));
//                // 마커(핀) 추가
//                googleMap.addMarker(mOptions);
//            }
//        });
//        ////////////////////
//
//        // Add a marker in Seoul and move the camera
//        LatLng seoul = new LatLng(37.5654401, 126.9459492);
//        mMap.addMarker(new MarkerOptions().position(seoul).title("Marker in Seoul"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
//    }




}