# In Hanghae99 my blog

항해99 주특기 기본 챕터에서 진행하는

Spring Boot 기반 CRUD 기능이 포함된 나만의 블로그 만들기 프로젝트 입니다.

## API

| 기능             | Method | URL             | 반환          |
| ---------------- | ------ | --------------- | ------------- |
| 전체 게시글 조회 | GET    | /api/posts      | List<Post>    |
| 특정 게시글 조회 | GET    | /api/posts      | List<PostDto> |
| 게시글 작성      | POST   | /api/posts      | Post          |
| 게시글 수정      | PUT    | /api/posts/{id} | id            |
| 게시글 삭제      | DELETE | /api/posts/{id} | id            |

## 3계층 설계

1. Controller

- PostRestController : 게시글 관련 컨트롤러

2. Service

- PostService: 게시글 수정

3. Repository

- Post : 게시글 테이블 - 제목, 작성자명, 작성 날짜
- PostRepository : 게시글 조회, 저장
- PostRequestDto : 게시글 수정
- PostDto : 특정 게시글 테이블 - 제목, 작성자명, 작성 날짜, 작성 내용
