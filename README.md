## :pushpin: 기획 의도
- AWS CloudWatch의 경우 AWS EC2와 연계하기 쉽고 대시보드, 알림, 통합 로그 등 다양한 기능을 지원하지만 추가적인 비용이 청구됨. 비용 절감 목적으로 Prometheus 그리고 Grafana를 사용하여 서버 메트릭 수집 및 시각화를 직접 구현이 필요한 상황.
- 프로메테우스 사용해 모니터링하기 위한 애플리케이션으로, 간단하게 유저 생성/조회/삭제 로직 구현

## :octocat: 프로젝트 정보
- 개발 도구 : `SpringBoot`, `JPA`, `MYSQL`, `Prometheus`, `grafana`, `Docker`, `Nginx`, `Jmeter`


## :hammer: 백엔드 아키텍처
![image](![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/e286c22f-2d6f-4a74-b9b2-e2366d4b45be/7aa39b95-1f71-4d2f-9fb4-456141cd8c18/image.png))


## :construction: Commit Convention
- add : 새로운 기능 추가
- fix : 버그 수정
- docs : 문서 수정
- style : 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
- refactor : 코드 리펙토링
- test : 테스트 코드, 리펙토링 테스트 코드 추가
- chore : 빌드 업무 수정, 패키지 매니저 수정



