# Issue GH-5589: Add shouldEq with only input types

## 기본 정보

| 항목 | 내용 |
|------|------|
| 이슈 | [GH-5589](https://github.com/kotest/kotest/issues/5589) |
| 제목 | Add shouldEq with only input types |
| 작성자 | `sksamuel` (메인테이너/BDFL) |
| 작성일 | 2026-01-20 |
| 라벨 | `assertions` |
| 클레임 | **없음** |
| 코멘트 | 8 (설계 논의 진행 중) |
| 관련 토론 | GH-5279 |
| 모듈 | `kotest-assertions-core` |

## 문제

현재 `shouldBe`는 `Any?`를 받아서 타입 안전하지 않음.

```kotlin
val count: Int = 5
count.shouldBe("5")  // 컴파일은 되지만 런타임에 실패 — 타입 미스매치
```

## 해결 방향

같은 타입만 받는 새로운 어설션 함수 `shouldEq` 추가.

```kotlin
val count: Int = 5
count.shouldEq(5)      // ✅ 같은 타입 — OK
count.shouldEq("5")    // ❌ 컴파일 에러 — 타입 불일치
```

### 설계 포인트

- `shouldBe`를 변경하는 것이 아니라 **새로운 함수 추가**
- 멀티플랫폼 지원 필요 (commonMain)
- 8개 코멘트로 아직 설계 논의 중 → 최종 방향 확인 후 구현

### 논의 상태 확인 필요

- 함수 이름: `shouldEq` vs 다른 이름?
- 제네릭 시그니처: `infix fun <T> T.shouldEq(expected: T)` 형태?
- `shouldNotEq` 도 같이?
- null 처리?

## 머지 확률 평가

| 요소 | 평가 |
|------|------|
| 하위 호환성 | ✅ 새 함수 추가 (기존 코드 영향 없음) |
| 변경 범위 | ⚠️ 새 API 도입이라 설계 합의 필요 |
| 테스트 부담 | ⚠️ 다양한 타입 시나리오 테스트 필요 |
| 메인테이너 관심 | ✅ **sksamuel 본인이 올린 이슈** |
| 경쟁자 | ⏳ 8개 코멘트로 관심은 있지만 아직 PR 없음 |
| **종합 머지 확률** | **중상** (설계 합의 후에는 높음) |

## 포트폴리오 가치

**매우 높음** — API 설계 역량을 보여주는 기여:

1. **타입 시스템 활용** — Kotlin 제네릭, 타입 안전성에 대한 이해
2. **메인테이너와의 설계 협업** — BDFL과 직접 논의
3. **멀티플랫폼 API 설계** — commonMain에 작성해야 하므로 KMP 이해 필요

## 실행 계획

1. GH-5589의 8개 코멘트와 GH-5279 논의 전체 읽기
2. 설계 방향이 확정되지 않았으면 의견 남기기
3. 설계가 확정되면 구현 시작
4. `shouldEq` + `shouldNotEq` 구현 (commonMain)
5. 다양한 타입 시나리오 테스트 작성
6. `./gradlew apiDump` + `./gradlew check`
7. PR 제출

## 리스크

- **설계 합의 전 코딩 절대 금지.** 8개 코멘트로 논의가 진행 중이며, 방향이 확정되지 않은 상태에서 구현하면 전부 버려질 수 있음. 코멘트를 전부 읽고, 합의가 명시적으로 확인된 후에만 코딩 시작
- 메인테이너가 직접 구현할 수도 있음 (본인이 올린 이슈) → 이슈에 "구현하겠다"고 먼저 의사 표시하고 반응을 보기
- GH-5755, GH-5728보다 **후순위**가 적절 — 신뢰 쌓은 후 진행
