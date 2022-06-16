🌄 여기어디 🌄
============

<br>

## 👊 Team 선샘주
<h4>🐲 홍주희 &nbsp;
<a href="https://github.com/wngml317">
    <img src="https://img.shields.io/badge/GitHub-%23181717?style=flat-square&logo=GitHub&logoColor=white">
</a>
  
<h4>🐲 이샘미 &nbsp;
<a href="https://github.com/saemmilee">
    <img src="https://img.shields.io/badge/GitHub-%23181717?style=flat-square&logo=GitHub&logoColor=white">
</a>
  
<h4>🐲 한선희 &nbsp;
<a href="https://github.com/tjshee39">
    <img src="https://img.shields.io/badge/GitHub-%23181717?style=flat-square&logo=GitHub&logoColor=white">
</a>
  
<br>
<br>
  
## 👀 개요
  > 등산 중 적지 않은 수의 사고가 발생하고 있으며 움직임이 힘든 경우 또는 동행자가 없는 상황에서 의식을 잃을 경우에는 신고하는 것 조차 어려움이 있을 수 있다. '여기어디'는 이러한 사고를 사전에 예방하기 위한 애플리케이션이다.
<br>  
  
## ✨ 주요 기능
  > ### ☝ 움직임 감지
  > * 스마트폰에 내장된 중력 센서 사용   
  > * 센서값에 이상이 감지되면 현재위치와 🔥Firebase의 사용자의 정보를 119에게 전송   
  > * 이 기능은 애플리케이션이 종료되어도 백그라운드에서 실행(Service) 
  <br>
  
  > ### ✌ 문자 전송
  > * 스마트폰에 내장된 GPS 센서 사용
  > * 위성에서 보내는 신호를 수신해 사용자의 현재 위치 계산
  > * 보호자에게 30분마다 현재 위치를 담아 문자 메세지 전송
  <br>
  
  > ### 👌 NFC 태그
  > * 등산 중 길을 자주 잃는 경로에 NFC 태그 설치
  > * NFC 태그에 현재 위치와 올바른 등산 경로를 담은 이미지 URL 인코딩
  > * 스마트폰을 NFC 태그에 가까이 하면 저장된 URL로 이동
  
  <br>
  
## 🛠 Tech
    
 OS :
```
Android(SDK Version 31)
```
Database :
```
Firebase
```
Language :
```
Java
```
    
 ## 📌 Dependencies
  
  ```
    implementation 'androidx.appcompat:appcompat:1.4.1'
    implementation 'com.google.android.material:material:1.5.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.3'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
    
    // Splash
    implementation 'androidx.core:core-splashscreen:1.0.0-beta02'  
    
    // img
    implementation 'com.google.firebase:firebase-storage:19.1.0'
    implementation 'com.github.bumptech.glide:glide:4.11.0'
    implementation platform('com.google.firebase:firebase-bom:29.0.0')

    // GPS
    implementation 'com.google.android.gms:play-services-location:15.0.1'

    // SwitchButton
    implementation 'com.kyleduo.switchbutton:library:2.1.0'

    // circleImageView
    implementation 'de.hdodenhof:circleimageview:3.1.0'

    implementation 'androidx.work:work-runtime:2.7.1'
  ```
