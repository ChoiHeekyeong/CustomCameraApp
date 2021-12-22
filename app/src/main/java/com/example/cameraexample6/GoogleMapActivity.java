package com.example.cameraexample6;

import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.maps.android.clustering.ClusterManager;

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

    HashMap<String, ArrayList> markerMap = new HashMap<>(); //마커 id값과 사진uri값을 저장할 해시맵

    private ClusterManager<DataDTO> clusterManager;
    int i = 0; //각 마커의 id값 일부에 쓰일 int값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlemap);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //firebase값 읽어오기
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
                    dataDTO.setTitle("m"+i); //title = marker Id값
                    i++;

                    markerId = dataDTO.getTitle();
                    Log.d("마커아이디: ",markerId);

                    ArrayList valueList = new ArrayList();
                    valueList.add(dataDTO.getPictureUri());
                    valueList.add(dataDTO.getLatitude());
                    valueList.add(dataDTO.getLongitude());
                    valueList.add(dataDTO.getTemperature());
                    valueList.add(dataDTO.getWeather());
                    markerMap.put(markerId,valueList);

                }


                /*마커 클러스터링*/
                clusterManager = new ClusterManager<>(GoogleMapActivity.this,mMap);
                mMap.setOnCameraIdleListener(clusterManager);
                mMap.setOnMarkerClickListener(clusterManager);


                /*마커 추가*/
                for(int A = 0 ; A < markerMap.size(); A++) {
                    double markerMapLat =(Double) markerMap.get("m"+A).get(1);
                    double markerMapLan =  (Double) markerMap.get("m"+A).get(2);
                    String markerTitle = "m"+A;

                    clusterManager.addItem(new DataDTO( new LatLng(markerMapLat,markerMapLan),markerTitle)); //마커 추가

                }

                clickMaker(clusterManager); //마커를 클릭했을 때 모달창이 나오게 함



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
        mMap.animateCamera(CameraUpdateFactory.zoomTo(6));




        UiSettings mapUiSettings = mMap.getUiSettings();
        mapUiSettings.setZoomControlsEnabled(true);// 지도 줌버튼
        mapUiSettings.setTiltGesturesEnabled(false);//두손으로 맵 기울이기 동작 막기



    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }



    public void clickMaker(ClusterManager clusterManager){
        /*마커 클릭했을 때*/
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<DataDTO>() {
            @Override
            public boolean onClusterItemClick(DataDTO dataDTO) {

                //커스텀 모달창으로 띄울 xml
                View modal = View.inflate(GoogleMapActivity.this, R.layout.modal, null);
                ImageView modalImg = modal.findViewById(R.id.modalimg);

                TextView latitudeTextView = modal.findViewById(R.id.latitudeTextView);
                TextView longitudeTextView = modal.findViewById(R.id.longitudeTextView);
                TextView temperatureTextView = modal.findViewById(R.id.temperatureTextView);
                TextView weatherTextView = modal.findViewById(R.id.weatherTextView);
                TextView addressTextView = modal.findViewById(R.id.addressTextView);
                ImageView weatherImageView = modal.findViewById(R.id.weathericon);


                String modalId = dataDTO.getTitle();    //선택한 마커 id값 가져오기
                String modalUri = (String) markerMap.get(modalId).get(0);
                Double modalLat = (Double) markerMap.get(modalId).get(1);
                Double modalLon = (Double) markerMap.get(modalId).get(2);
                Double modalTemp = (Double) markerMap.get(modalId).get(3);
                String modalWeather = (String) markerMap.get(modalId).get(4);

                switch (modalWeather){//icon
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

                //모달창 id값 매칭
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



                AlertDialog.Builder dlg = new AlertDialog.Builder(GoogleMapActivity.this);
                dlg.setMessage("사진 정보")
                        .setView(modal)
                        .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create()
                        .show();

                return true;
            }
        });

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
