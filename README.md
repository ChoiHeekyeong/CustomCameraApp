졸업 프로젝트 : 인공지능 기능이 있는 커스텀 카메라 APP 📸
==================
#### DKU 4학년 캡스톤디자인(졸업작품)
> ##### 프로젝트 기간 : 2021.03~2021.11

### TEAM MEMBER
이름 | 담당 파트 |
---|---|
박효원 | 초해상도 딥러닝(Tensorflow ESRGAN모델), 저조도향상 딥러닝(Tensorflow Mirnet모델), 카메라 기능 구현(Java)
최희경 | 카메라 제작 및 기본 기능 구현, FireBase 연동, 구글맵 API, 날씨 API 통신(Java)
이현욱 | 커스텀 카메라 UI (XML)

> ### 개발 환경
> * 안드로이드 스튜디오
> 
> ### 언어
> * Python
> * Java

------------

> ### 아이디어
> * 우리는 **딥러닝을 활용한 이미지 처리 기술**이 들어간 **어플리케이션**을 만들고 싶었습니다.
> * 그중에서도 **초해상도**와 **저조도 향상** 기술을 모바일에서 구현하고자 하였습니다.
> * 이외에도 자신이 **그동안 사진들을 어디에서 찍었는지를 지도로 한눈에 확인 할 수 있는 기능**도 구현하고자 하였습니다.


## 🔧 기능 소개
### 1. 초해상도 기능
* 초해상도 기능을 ON하고 사진을 찍으면 해상도를 높여서 저장
* Tensorflow를 이용한 ESRGAN 모델을 사용하며 모델을 트레이닝 하고, tflite로 변환 후 Android 환경에 이용
![image](https://user-images.githubusercontent.com/64201163/147079929-48db0eaa-ad3c-4d8d-acf5-e936c1d8d862.png)


### 2. 저조도 향상 기능
* 저조도 향상 기능을 ON하고 사진을 찍으면 어두운 사진도 밝게 바꾸어 저장
* Tensorflow를 이용한 Mirnet 모델을 사용하며 모델을 트레이닝 하고, tflite로 변환 후 Android 환경에 이용
![image](https://user-images.githubusercontent.com/64201163/147080020-a40ac128-b561-48cd-a7c6-1d6a94fb52eb.png)
![image](https://user-images.githubusercontent.com/64201163/147080049-b66f6c75-c7c3-4674-9f56-e2e1145a5233.png)
![image](https://user-images.githubusercontent.com/64201163/147080076-bd50adae-1347-4d01-a02a-72bc0d85dfd3.png)


### 3. Place 기능
* 공유 버튼을 ON하고 사진을 찍으면 FireBase에 사진Uri, 위치정보, 날씨정보가 업로드
* Map 아이콘을 눌러 구글맵에 들어가면 FireBase에 있는 정보들이 Pin(마커)로 표시
* 이때 클러스터링 기능을 도입하여 일정 범위 안에서 다중 Pin(마커)들은 숫자 아이콘으로 합산되어 표시
* 날씨 정보는 OpenWeatherMap API를 사용했으며 해당 날씨 정보를 JSON구조로 받아오는 HTTP통신은 안드로이드 라이브러리 Retrofit2를 이용

![ezgif com-gif-maker](https://user-images.githubusercontent.com/64201163/147081912-c25d7af0-6a2a-440b-b29f-7e1a8032ca25.gif)


![image](https://user-images.githubusercontent.com/64201163/147080194-7ab6daae-f423-4634-a635-633d49e14ec8.png)


### 4. 그 외
* Zoom(Bar & Pinch)
* Flash
* 전/후면 전환
* 자동 초점 기능
![image](https://user-images.githubusercontent.com/64201163/147085583-7e020813-eb54-4dc7-b861-6f0572a358f2.png)


--------------
> ### 한계점
> 1. 지속적인 핸드폰 기술의 발전으로 휴대폰 카메라의 기본 해상도가 이미 약 3024x4032이다. 모바일 환경은 컴퓨터만큼의 사양이 되지 않아 초해상도 기능을 사용할 때 3024x4032을 처리하기엔 연산량의 한계가 있었다. 때문에 초해상도 기능을 모바일에서 돌리기 위해서는 원본 파일의 해상도를 임의로 낮춰야만했다. 640x480의 크기로 줄인 후에 tflite파일로 넘겨서 2560x1920 사이즈로 처리하였다. 이렇다보니 기본 카메라로 찍은 해상도를 따라오지 못한다. tflite을 모바일 환경에서 실행하여 이미지 처리를 했다는 것에 의의를 두기로 하였다.
> 2. 회원가입/로그인을 구현하지 않아 다른 기기에서 공유기능을 사용하면 같은 FireBase에 업로드가 되어 같은 Map을 공유하게 된다.







