## :pushpin: 기획 의도
- Prometheus 그리고 Grafana를 사용하여 Admin이 유저의 API 호출량과 과금, 서버의 부하 정도를 대시보드를 통해 쉽게 모니터링 할 수 있게하고,  User가 자신의 API 호출량과 과금 정도를 대시보드를 통해 쉽게 파악할 수 있게함


## :heart: 로직
- JWT 기반으로 애플리케이션에 회원가입 &rarr; Grafana 에서 해당 유저에 대한 계정 자동 생성 &rarr; Grafana 에서 해당 계정에 대한 조직 자동 생성  &rarr; Grafana 에서 해당 조직에 대한 서비스 계정 자동 생성  &rarr; Grafana 에서 해당 서비스 계정에 대한 API 키 자동 생성  &rarr; Grafana 에서 해당 API 키를 활용하여 자동으로 해당 유저에 대한 대시보드 자동 생성
  
  
## :octocat: 프로젝트 정보
- 개발 도구 : `SpringBoot`, `JPA`, `MYSQL`, `Prometheus`, `grafana`, `Docker`, `Nginx`, `groq`


## :hammer: 백엔드 아키텍처
![image](https://github.com/user-attachments/assets/7eb5a179-1dd3-4860-b5f2-78fa834c480c)



## :construction: Commit Convention
- add : 새로운 기능 추가
- fix : 버그 수정
- docs : 문서 수정
- style : 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
- refactor : 코드 리펙토링
- test : 테스트 코드, 리펙토링 테스트 코드 추가
- chore : 빌드 업무 수정, 패키지 매니저 수정



