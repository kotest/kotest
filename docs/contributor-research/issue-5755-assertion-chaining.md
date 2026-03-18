# Issue GH-5755: Make shouldHaveSingleElement and friends chainable

## 기본 정보

| 항목 | 내용 |
|------|------|
| 이슈 | [GH-5755](https://github.com/kotest/kotest/issues/5755) |
| 제목 | Make shouldHaveSingleElement and friends chainable |
| 작성자 | `jlous` (외부) |
| 작성일 | 2026-03-18 (오늘) |
| 라벨 | 없음 |
| 클레임 | **없음** (경쟁자 0) |
| 코멘트 | 0 |
| 모듈 | `kotest-assertions-core` |

## 문제

`shouldHaveSingleElement`와 유사한 어설션 함수들이 `Unit`을 반환해서 fluent 체이닝이 불가능함.

```kotlin
// 현재: 불가능
val element = list.shouldHaveSingleElement { it > 0 }

// 이슈 본문에 따르면 내부의 singleElement() 함수는 이미 matcher를 반환하지만,
// 래퍼 함수인 shouldHaveSingleElement가 Unit을 반환한다고 함
// → 실제 구현을 확인한 후 패치 설계를 확정해야 함
```

## 해결 방향

`shouldHaveSingleElement` 등의 래퍼 함수가 `Unit` 대신 의미 있는 값을 반환하도록 변경. 실제로 무엇을 반환해야 하는지(matcher, 컬렉션 자체, 추출된 요소 등)는 구현 확인 후 확정.

### 변경 범위

**첫 PR은 이슈 본문의 `shouldHaveSingleElement and friends` 범위로만 제한.** 비슷한 패턴의 다른 함수들까지 일괄 수정하는 sweep은 하지 않음.

1. `shouldHaveSingleElement` 리턴 타입 변경
2. 이슈에서 언급된 "friends" (관련 함수) 범위만 포함
3. 메인테이너가 확장을 원하면 후속 PR로 대응

### 하위 호환성

**완벽하게 호환됨.**

- `Unit`을 반환하던 함수가 값을 반환하게 바뀌는 것은 기존 코드를 깨뜨리지 않음
- 기존 코드: 반환값을 무시하고 있었음 → 여전히 동작
- 새 코드: 반환값을 활용한 체이닝 가능

## 기술적 분석

### 관련 파일 (예상)

- `kotest-assertions-core/src/commonMain/kotlin/io/kotest/matchers/collections/`
- `shouldHaveSingleElement`, `shouldContainExactly` 등 유사 패턴

### 구현 포인트

1. 현재 `shouldHaveSingleElement`가 어떻게 `Unit`을 반환하는지 확인
2. 내부 `singleElement()` 등 관련 함수의 실제 반환값 확인 — 이슈 본문의 설명을 신뢰하지 말고 코드로 확정
3. 반환 설계 결정 후 수정

### Binary Compatibility

- `./gradlew apiDump` 실행 필수
- `Unit` → `T` 변경은 ABI 호환성에 영향을 줄 수 있으므로 api dump 파일 업데이트 필요

## 머지 확률 평가

| 요소 | 평가 |
|------|------|
| 하위 호환성 | ✅ 완벽 |
| 변경 범위 | ✅ 좁음 (assertions 모듈만) |
| 테스트 부담 | ✅ 낮음 (기존 테스트 + 체이닝 테스트 추가) |
| 메인테이너 관심 | ⏳ 아직 코멘트 없음 (신규 이슈) |
| 경쟁자 | ✅ 없음 |
| **종합 머지 확률** | **매우 높음** |

## 포트폴리오 가치

- Kotlin assertions API 설계 이해 입증
- 하위 호환성을 유지하면서 API 개선하는 능력
- "사용자 경험 개선" 관점의 기여

## 실행 계획

1. 이슈에 코멘트: "이 문제 확인했고, `shouldHaveSingleElement and friends` 범위로 수정하겠다"
2. 실제 구현 코드 확인 — `singleElement()` 등의 반환값 타입 파악
3. 리턴 타입 변경 + 체이닝 테스트 추가 (이슈 본문 범위만)
4. `./gradlew apiDump` + `./gradlew check`
5. PR 제출

## 리스크

- `apiDump` 변경이 클 수 있음 → 범위를 이슈 본문의 "and friends"로만 제한하고, 메인테이너가 확장을 원하면 후속 PR로
- 멀티플랫폼 빌드 확인 필요 (commonMain에 있으면 모든 타겟에 영향)
