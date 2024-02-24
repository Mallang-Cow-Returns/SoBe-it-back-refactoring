<h1 align="center">So Be it - Backend Refactoring</h1>

<br/>

<h2>📃 Convention</h2>  
<h3>🪵 Branch Rule</h3>  

- main : 제품 출시 브랜치
- develop : 개발 브랜치
    - feat/{기능명 or 이슈명} : 새로운 기능 개발하는 브랜치
    - refactor/{기능명 or 이슈명} : 개발된 기능을 리팩토링하는 브랜치

<br/>

<h3>🔧 Commit Rule</h3> 

1. Issue 생성
2. Branch 생성
3. Add, Commit, Push
4. Pull Request
5. Merge

<br/>

<h4>🔩 Commit Message</h4>

```bash
[#Issue Number] <Type><Break or Not> : <Subject>
<BLANK LINE>
<Body>
<BLANK LINE>
```

<br/>

<h4>🔗 Commit Type</h4>

- `FEAT` : 새로운 기능 추가
- `FIX` : 버그 수정
- `DOCS` : 문서 수정
- `STYLE` : 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우 등
- `REFACTOR` : 코드 리팩토링
- `TEST` : 테스트 코드 작성
- `MOVE` : 프로젝트 파일 및 코드 이동
- `RENAME` : 폴더 및 파일 이름 변경
- `REMOVE` : 폴더 및 파일 삭제
- `MERGE` : 다른 Branch와의 충돌 해결 후 Merge
- `CHORE` : 빌드 수정, 패키지 매니저 설정, 운영 코드 변경이 없는 경우 등

<br/>

<h3>⚠️ Issue Rule</h3> 

- 담당자(Assignees) 명시
- Task List 적극 활용

<br/>

<h3>🚨 Pull Request Rule</h3> 

- `[#Issue Number] 변경 사항` 구조로 제목 작성
- Issue와 연동
