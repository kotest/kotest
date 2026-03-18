# Issue GH-5755: Make shouldHaveSingleElement and friends chainable

## 기본 정보

| 항목 | 내용 |
|------|------|
| 이슈 | [GH-5755](https://github.com/kotest/kotest/issues/5755) |
| 제목 | Make shouldHaveSingleElement and friends chainable |
| 작성자 | `jlous` (외부) |
| 작성일 | 2026-03-18 |
| 라벨 | 없음 |
| 클레임 | **없음** (경쟁자 0) |
| 코멘트 | 0 |
| 모듈 | `kotest-assertions-core` |

## 문제

`shouldHaveSingleElement`의 일부 오버로드가 `Unit`을 반환해서 fluent 체이닝이 불가능함.

```kotlin
// Collection 오버로드: Unit 반환 → 체이닝 불가
val element = collection.shouldHaveSingleElement { it > 0 }

// 이슈 본문에 따르면 내부의 singleElement() 함수는 이미 matcher를 반환하지만,
// 래퍼 함수인 shouldHaveSingleElement가 Unit을 반환한다고 함
// → 실제 구현을 확인한 후 패치 설계를 확정해야 함
```

## 실제 API dump 분석 (kotest-assertions-core.api)

**핵심 발견: 반환 타입이 오버로드마다 다르다.**

| 오버로드 | 반환 타입 | ABI 시그니처 | 상태 |
|----------|----------|-------------|------|
| `Iterable` + 값 | `Iterable` | `…)Ljava/lang/Iterable;` | ✅ 이미 체이닝 가능 |
| `Iterable` + 람다 | `Iterable` | `…)Ljava/lang/Iterable;` | ✅ 이미 체이닝 가능 |
| `Collection` + 값 | **`Unit`** | `…)V` | ❌ 수정 대상 |
| `Collection` + 람다 | **`Unit`** | `…)V` | ❌ 수정 대상 |
| `Array` + 값 | `Array` | `…)[Ljava/lang/Object;` | ✅ 이미 체이닝 가능 |
| `Array` + 람다 | **`Unit`** | `…)V` | ❌ 수정 대상 |
| `Sequence` + 값 | **`Unit`** | `…)V` | ❌ 수정 대상 |

→ `Iterable`과 `Array(값)` 버전은 이미 체이닝 가능. **`Collection`, `Array(람다)`, `Sequence` 오버로드만 `Unit` 반환.**

## 해결 방향

`Unit`을 반환하는 4개 오버로드를 해당 컬렉션 타입을 반환하도록 변경. 이미 `Iterable`/`Array(값)` 버전에 선례가 있으므로 동일 패턴 적용.

### 변경 범위

**첫 PR은 `shouldHaveSingleElement`만 먼저 패치.** 같은 파일/같은 패턴의 다른 함수("friends")는 구현 확인 후 포함 여부 결정. 범위 확장은 메인테이너가 요청하면 후속 PR로.

### 반환값 설계 우선순위

1. **1순위: 내부 함수가 이미 반환하는 타입 그대로 노출** — 같은 함수의 다른 오버로드(`Iterable` 버전)가 이미 반환하는 패턴을 따라감
2. **2순위: 체이닝이 가능한 최소한의 반환값** — 수신 객체(컬렉션 자체) 반환
3. **3순위: 새 타입 도입은 금지** — wrapper나 새 sealed class 등 과설계 방지

### 하위 호환성

기존 호출 코드는 대부분 유지되지만, 반환 타입 변경은 ABI 관점에서 maintainer가 민감하게 볼 수 있으므로 **apiDump 검증이 필수.** 소스 호환성은 거의 보장됨 (Unit 반환값을 사용하던 코드는 사실상 없음), 단 ABI 호환성은 별도 확인 필요.

## 기술적 분석

### 관련 파일

- `kotest-assertions-core/src/commonMain/kotlin/io/kotest/matchers/collections/` — 소스
- `kotest-assertions-core/api/kotest-assertions-core.api` — API dump

### 구현 포인트

1. `shouldHaveSingleElement`의 `Collection`/`Array(람다)`/`Sequence` 오버로드 확인
2. 같은 함수의 `Iterable` 오버로드가 어떻게 반환값을 처리하는지 패턴 파악
3. 동일 패턴 적용 (내부 함수가 반환하는 타입을 그대로 노출)
4. "friends" 함수들은 구현 확인 후 포함 여부 판단 — 첫 PR에서는 보류

### Binary Compatibility

- `./gradlew apiDump` 실행 필수
- `Unit(V)` → 컬렉션 타입 변경은 ABI 변경 — api dump 파일이 변경됨
- 변경된 `.api` 파일을 PR에 반드시 포함

## 머지 확률 평가

| 요소 | 평가 |
|------|------|
| 하위 호환성 | ⚠️ 소스 호환은 거의 보장, ABI 변경은 apiDump 검증 필요 |
| 변경 범위 | ✅ 좁음 (assertions 모듈, shouldHaveSingleElement만) |
| 선례 | ✅ 같은 함수의 Iterable 오버로드가 이미 반환값을 가짐 |
| 테스트 부담 | ✅ 낮음 (기존 테스트 + 체이닝 테스트 추가) |
| 메인테이너 관심 | ⏳ 아직 코멘트 없음 (신규 이슈) |
| 경쟁자 | ✅ 없음 |
| **종합 머지 확률** | **매우 높음** |

## 포트폴리오 가치

- Kotlin assertions API 설계 이해 입증
- ABI 호환성을 의식하면서 API 개선하는 능력
- "사용자 경험 개선" 관점의 기여

## 실행 계획

1. 이슈에 코멘트: "`shouldHaveSingleElement`의 `Collection`/`Array(람다)`/`Sequence` 오버로드가 `Unit`을 반환하는 것을 확인했고, `Iterable` 오버로드와 동일 패턴으로 수정하겠다"
2. 소스 코드에서 `Iterable` 오버로드의 반환 패턴 확인
3. 동일 패턴으로 4개 오버로드 수정 + 체이닝 테스트 추가
4. `./gradlew apiDump` + `./gradlew check`
5. PR 제출 (shouldHaveSingleElement만, friends는 후속 PR로)

## 리스크

- `apiDump` 변경이 있으므로 메인테이너가 ABI 변경에 민감하게 반응할 수 있음 — 단, 같은 함수의 다른 오버로드에 선례가 있어서 설득력 있음
- "friends" 범위를 첫 PR에서 너무 넓히면 리뷰 부담 → shouldHaveSingleElement만으로 시작
- 멀티플랫폼 빌드 확인 필요 (commonMain에 있으면 모든 타겟에 영향)
