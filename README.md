# Todo App Prototype

### 기술 스택
- **Language:** Kotlin
- **Database:** Room
- **Asynchronous:** Coroutines, Flow
- **UI & Architecture:** ViewBinding, RecyclerView (ListAdapter)

### 주요 기능
- **할 일 조회 및 필터링**
  - 등록된 할 일 목록을 마감일 오름차순으로 조회
  - 우측 상단 메뉴를 통해 '완료된 할 일 보기' 상태를 토글하여 미완료 항목만 보거나 전체 항목 보기 
- **할 일 추가 및 수정**
  - 할 일 내용, 메모, 카테고리(개인, 학업, 쇼핑), 마감일 입력하여 저장
  - 리스트 아이템을 길게 클릭 시 나타나는 팝업 메뉴를 통해 기존 내용을 수정
- **상세 보기**
  - 리스트 아이템을 짧게 클릭하면 AlertDialog 창이 생성되며 할 일의 상세 정보 표시
- **할 일 삭제 및 완료 처리**
  - 체크박스를 통해 완료 여부 변경 
  - 할 일을 개별 삭제하거나, 메뉴를 통해 완료된 할 일들을 일괄 삭제

### 프로젝트 구조
* **`data/`**
  * `entity/TodoItem.kt`: DB 테이블에 매핑되는 데이터 모델
  * `dao/TodoDao.kt`: 데이터 삽입, 수정, 삭제, 조회 쿼리
  * `database/TodoDatabase.kt`: Room Database 객체를 생성/관리 
* **`ui/`**
  * `view/MainActivity.kt`: 할 일 목록(RecyclerView)을 보여주고 전체적인 상태 및 메뉴 이벤트 관리
  * `view/AddActivity.kt`: 새로운 할 일 정보를 입력받아 DB에 추가
  * `view/EditActivity.kt`: 선택한 할 일의 기존 정보를 불러와 수정하고 DB에 반영
  * `adapter/TodoAdapter.kt`: ListAdapter를 상속받아 아이템 바인딩 및 클릭/롱클릭 이벤트 처리
