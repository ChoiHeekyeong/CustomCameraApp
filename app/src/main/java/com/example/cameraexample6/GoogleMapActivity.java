package com.example.cameraexample6;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class GoogleMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    DataDTO dataDTO = new DataDTO();
    MarkerOptions mOptions = new MarkerOptions();
    View marker_googlemap;
    ImageView markerimg;
    //    HashMap<String, String> markerMap = new HashMap<>(); //마커 id값과 사진uri값을 저장할 해시맵
    HashMap<String, ArrayList> markerMap = new HashMap<>(); //마커 id값과 사진uri값을 저장할 해시맵





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlemap);

        marker_googlemap = LayoutInflater.from(this).inflate(R.layout.marker_googlemap, null);
        markerimg = marker_googlemap.findViewById(R.id.markerimg);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(GoogleMapActivity.this);




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

                    HashMap<String, String> value = (HashMap<String, String>) postSnapshot.getValue();
                    picInfo.put(key, value);

                    String stringLat = String.valueOf(picInfo.get(key).get("latitude"));
                    String stringLon = String.valueOf(picInfo.get(key).get("longitude"));
                    String pictureUri = picInfo.get(key).get("pictureUri");
                    String stringTemp = String.valueOf(picInfo.get(key).get("temperature"));
                    String weather = picInfo.get(key).get("weather");

                    //위도,경도,온도는 Double로 변환
                    Double latitude = Double.parseDouble(stringLat);
                    Double longitude = Double.parseDouble(stringLon);
                    Double temperature = Double.parseDouble(stringTemp);

                    dataDTO.setLatitude(latitude);
                    dataDTO.setLongitude(longitude);
                    dataDTO.setPictureUri(pictureUri);
                    dataDTO.setTemperature(temperature);
                    dataDTO.setWeather(weather);


                    Glide.with(getApplicationContext()).load(pictureUri).override(700, 230).into(markerimg);
                    //getApplicationContext() 대신 GoogleActivity.this써도 O

                    mOptions.position(new LatLng(dataDTO.getLatitude(), dataDTO.getLongitude())) //위치 셋팅
                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(marker_googlemap))); //커스텀마커 적용


                    // 마커(핀) 추가
                    Marker marker = mMap.addMarker(mOptions);
                    markerId = marker.getId(); //마커 아이디

                    ArrayList valueList = new ArrayList();
                    valueList.add(dataDTO.getPictureUri());
                    valueList.add(dataDTO.getLatitude());
                    valueList.add(dataDTO.getLongitude());
                    valueList.add(dataDTO.getTemperature());
                    valueList.add(dataDTO.getWeather());
                    markerMap.put(markerId,valueList);

                }

//                Log.d("총갯수?@@@", "" + markerMap.size());
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

        TextView latitudeTextView = modal.findViewById(R.id.latitudeTextView);
        TextView longitudeTextView = modal.findViewById(R.id.longitudeTextView);
        TextView temperatureTextView = modal.findViewById(R.id.temperatureTextView);
        TextView weatherTextView = modal.findViewById(R.id.weatherTextView);
        TextView addressTextView = modal.findViewById(R.id.addressTextView);
        ImageView weatherImageView = modal.findViewById(R.id.weathericon);


        String modalId = marker.getId();    //선택한 마커 id값 가져오기
//        String modalUri = markerMap.get(marker.getId());    //이미지주소 //ㅁㅁㅁㅁㅁㅁ
        String modalUri = (String) markerMap.get(marker.getId()).get(0);
        Double modalLat = (Double) markerMap.get(marker.getId()).get(1);
        Double modalLon = (Double) markerMap.get(marker.getId()).get(2);
        Double modalTemp = (Double) markerMap.get(marker.getId()).get(3);
        String modalWeather = (String) markerMap.get(marker.getId()).get(4);

        switch (modalWeather){
            case "Clear":
                weatherImageView.setImageResource(R.drawable.icon_clear);
                break;
            case "Clouds":
                weatherImageView.setImageResource(R.drawable.icon_clouds);
                break;
            case "Rain":
                weatherImageView.setImageResource(R.drawable.icon_rain);
                break;
            case "Snow":
                weatherImageView.setImageResource(R.drawable.icon_snow);
                break;
            default :
                weatherImageView.setImageResource(R.drawable.icon_clear);
                break;

        }

        //지번주소
        GpsTracker gpsTracker = new GpsTracker(GoogleMapActivity.this);
        String address = getCurrentAddress(modalLat, modalLon);


        for (int i = 0; i < markerMap.size(); i++) {
            if (modalId.equals("m" + i)) {
                Glide.with(getApplicationContext()).load(modalUri).override(700, 200).into(modalImg);
                latitudeTextView.setText("위도: "+modalLat);
                longitudeTextView.setText("경도: "+modalLon);
                temperatureTextView.setText("온도: "+modalTemp);
                weatherTextView.setText("날씨: "+modalWeather);
                addressTextView.setText(address);
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


    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString();

    }




}
