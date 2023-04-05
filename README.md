[![Typing SVG](https://readme-typing-svg.demolab.com?font=Fira+Code&pause=1000&width=435&lines=Socket-Multi-Chatting+%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%A8+%EB%A7%8C%EB%93%A4%EA%B8%B0)](https://git.io/typing-svg)

<div>
  <img src="https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=java&logoColor=white">
</div>

# 소개

**Java**에서 배운 Socket 통신을 활용한 Socket - Multi - Chatting 프로그램을 제작

# 📑 <span style="background-color:indianred">Socket 통신이란?</span>

- 소켓(Socket)은 TCP/IP 기반 네트워크 통신에서 데이터 송수신의 마지막 접점을 의미
- `소켓통신`은 이러한 소켓을 통해 Server - Client 간 데이터를 주고받는 양방향 연결 지향성 통신
- 보통 지속적으로 연결을 유지하면서 `실시간`으로 데이터를 주고받아야 하는 경우 사용

## 📑 <span style="background-color:CadetBlue">프로그램 구성</span>
### 📑 <span style="background-color:DarkOrchid">**Server**</span>

   - 📑 **_Dto_**
      - Request
      - Response
   - 📑 **_Entity_**
      - Room
   - 📑 **_Main_**
      - ConnectedSocket
      - ServerApplication

### 📑 <span style="background-color:DarkOrchid">**Client**</span>
   - 📑 **_Dto_**
      - Request
      - Response
   - 📑 **_Views_**
      - ClientApplication
      - ClientRecive

요구사항
- UI (Java Swing 으로 구현) - 로그인 UI, 채팅방 목록 UI, 채팅방 UI 
- 로그인 (Admin), 방생성 시, 바로 입장
- 로그인 (User), 채팅방 목록 생성
- User 방 입장
- Admin, User 방생성, 입장 메세지 출력
- 유저간 채팅
- 퇴장시, 메세지 출력 
- 방장 퇴장시, 유저도 같이 퇴장, 유저 퇴장시, 유저만 방 퇴장
-----------------------

### 서버 실행 UI
![image](https://user-images.githubusercontent.com/121993153/226783353-cdc75549-8268-4ea7-8067-c5ceff44c43b.png)

### 실행 UI (로그인), 입장 UI
![image](https://user-images.githubusercontent.com/121993153/226792948-e5258bba-c389-46b7-afa7-a75a4206f349.png)

### 채팅방 생성 UI (admin, user)
![image](https://user-images.githubusercontent.com/121993153/226793156-255e1082-b853-4908-a617-902eb4db4d3b.png)

### 채팅방 입장 완료 UI
![image](https://user-images.githubusercontent.com/121993153/226784671-c878338a-2929-45fd-8a23-6e34bdaf2a3d.png)

### 채팅 구현 UI
![image](https://user-images.githubusercontent.com/121993153/226784985-12464477-ed6d-4c05-b1be-c3641fce0731.png)

### 방장이 나갔을 때
![image](https://user-images.githubusercontent.com/121993153/226785141-904f4016-bb62-4b83-be0d-bd73da3683ab.png)

### 방장이 나갔을 때 ( 유저도 같이 나가짐 )
![image](https://user-images.githubusercontent.com/121993153/226785251-6bb148e1-cf80-4aaa-b71b-8644efe720ba.png)

### 유저만 나갔을 때 ( 방장은 방에 유지 )
![image](https://user-images.githubusercontent.com/121993153/226785468-791742ca-35ef-4c4c-89b9-34cfac9f09e6.png)



-----------------------

# 제작 후기

Java를 공부하면서 가장 어려웠던 프로젝트였던거 같다. </br>

실제로는 소켓통신 채팅프로그램을 세개 정도 만들었지만, java의 모든 개념을 이해하기에는 다소 부족한 점이 많은거 같다. </br>

아래는 Socket-Multi-Chatting 프로그램을 학습하며 기록했던 자료이며, 해당 내용은 벨로그에서 자세히 확인하실 수 있습니다. </br>

[![Velog's GitHub stats](https://velog-readme-stats.vercel.app/api?name=leesfact&tag=project&color=)](https://velog.io/@leesfact/AWS-BACK-DAY-40.-MultiChat-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8)
