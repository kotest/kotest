# Issue GH-5202: Data classes that inherit from map or collection will use wrong comparison

## 기본 정보

| 항목 | 내용 |
|------|------|
| 이슈 | [GH-5202](https://github.com/kotest/kotest/issues/5202) |
| 제목 | Data classes that inherit from map or collection will use wrong comparison |
| 작성자 | 외부 기여자 |
| 작성일 | 2025-11-19 |
| 라벨 | `assertions` |
| 클레임 | **없음** |
| 코멘트 | 7 (근본 원인 분석 완료) |
| 모듈 | `kotest-assertions-core` (EqResolver) |

## 문제

`Map`이나 `Collection`을 구현하는 data class를 비교할 때, `MapEq`/`CollectionEq`가 `DataClassEq`보다 우선 적용되어 비-Map/Collection 필드가 무시됨.

```kotlin
data class MyMap(
    val name: String,
    val entries: Map<String, Int>
) : Map<String, Int> by entries

val a = MyMap("Alice", mapOf("x" to 1))
val b = MyMap("Bob", mapOf("x" to 1))

a.shouldBe(b)  // ✅ 통과 (잘못됨!) — name이 다른데 Map으로만 비교
```

## 근본 원인

`EqResolver` 우선순위 체계에서 `MapEq` > `DataClassEq` 순서로 적용.

### EqResolver 우선순위 (현재)

1. `CustomEq` (사용자 정의)
2. `MapEq`
3. `CollectionEq`
4. `DataClassEq`
5. `DefaultEq`

→ data class가 Map을 구현하면 `MapEq`가 먼저 걸려서 Map 내용물만 비교.

## 해결 방향

### 접근법 1: DataClassEq 우선순위 올리기

`DataClassEq`를 `MapEq`/`CollectionEq` 앞으로 이동.

- 장점: 직관적
- 위험: Map/Collection을 비교할 때 DataClassEq가 잘못 적용될 수 있음

### 접근법 2: 복합 판단

`isDataClass && isMap` 케이스를 별도 처리 — DataClassEq에서 Map/Collection 필드도 재귀 비교.

- 장점: 정확
- 위험: 구현 복잡도 증가

### 접근법 3: DataClassEq에서 위임 비교기 위임

data class 필드를 비교할 때 각 필드의 타입에 맞는 Eq를 재귀적으로 사용.

## 머지 확률 평가

| 요소 | 평가 |
|------|------|
| 하위 호환성 | ⚠️ 비교 동작이 바뀌므로 기존 테스트 영향 가능 |
| 변경 범위 | ⚠️ EqResolver 핵심부 변경 |
| 테스트 부담 | ⚠️ 다양한 조합 시나리오 필요 |
| 메인테이너 관심 | ✅ assertions 라벨 + 7개 코멘트 |
| 경쟁자 | ✅ 없음 (4개월간) |
| **종합 머지 확률** | **중간** |

## 포트폴리오 가치

**높음** — Kotlin 타입 시스템과 비교 전략 패턴에 대한 깊은 이해를 입증:

1. 방문자 패턴 / 전략 패턴 이해
2. data class 메커니즘과 위임 패턴 이해
3. 기존 아키텍처 내에서 안전하게 수정하는 능력

## 실행 계획

1. EqResolver 체인 코드 전체 읽기
2. 7개 코멘트의 분석 내용 숙지
3. 이슈에 접근법 제안 코멘트 남기기 (메인테이너 방향 확인)
4. 합의 후 구현
5. 엣지 케이스 테스트 다수 작성

## 리스크

- EqResolver는 모든 `shouldBe` 호출에 영향 → 회귀 리스크 높음
- 우선순위 변경 시 다른 사용자의 테스트가 깨질 수 있음
- **첫 기여로는 부적합** — GH-5755, GH-5728 이후 신뢰 쌓은 후 진행

## 전제 조건

- Kotest에 최소 1개 PR 머지 경험
- EqResolver 아키텍처 완전 이해
- 메인테이너와 설계 방향 사전 합의
