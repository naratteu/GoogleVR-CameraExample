# 보다 직접적인 1인칭 동기화 시점 촬영&스트리밍 연구

## 준비사항
* 적정사양의 스마트폰
* 머리에 고정가능한 Cardboard
* VR 라이브러리
* 스트리밍 라이브러리

## 현 구현 구조
* 후면카메라 -> 카드보드 랜더링 -> 화면 송출

## 추후 구현 가이드
* 촬영영상에 대한 카드보드랜더링과 송출을 병렬처리
* 양눈 시야에 표시되는 카메라 구간을 각각 조정해 보다 넓은 시야 지원
* 손목시계를 보는 제스쳐로 가상시계 소환
  * QR코드나 마커를 활용하면 시연용으로 보다 단순 구현 가능
    * https://docs.opencv.org/trunk/d5/dae/tutorial_aruco_detection.html
  * VRchat 등에서 사용되는 비슷한 유틸 사전조사 필요
* 카메라 줌, 촛점갱신, 촛점수동, 노출수동, 등을 조작하는 컨트롤러 대응
  * 가장 OTG로 유선마우스 활용하면 가장 저렴하게 구현 예상

## 현제 한계점
* 느린 카메라 표시속도 -> 고급장비 필요
* 안구와 카메라의 위치차이로 인해 비틀리는 공간지각 -> 화면보정을 해도 완전 극복은 어려움
* 카메라가 하나라 부족한 원근감 -> 전용장비설계제작필요
* 배터리, 데이터사용량 등의 통속적인 문제 -> 돈으로 해결가능
* 오래사용시 스마트폰 발열로 인한 강제셧다운 -> 방열판? 본격적인 현업사용시라면 꽤 신경쓰게될 사항

## 구현 업로드
* https://youtu.be/vyFZMkzfTS0 1차 비공개촬영본
* https://youtu.be/rx1IfjN8O4M 2차 공개촬영본
* https://skfhddlg.blog.me/221840374219
* https://excf.com/nonhcg/12706541
## 포크한 코드&연구하면서 탐색한 주요 코드
* https://github.com/kjw0723/GoogleVR-CameraExample
* https://github.com/begeekmyfriend/yasea
* https://github.com/googlevr/gvr-android-sdk -> https://github.com/naratteu/gvr-android-sdk

## 기타
* 코드내의 스트림키 폐기되어 사용시 변경필요.
