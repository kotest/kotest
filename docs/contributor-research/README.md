# Kotest 기여 전략 리서치

## 목표

Kotlin 백엔드 엔지니어가 `kotest/kotest`에 기여할 후보를 정리한 문서.

단순히 "쉬운 것"만 고르는 게 아니라, **"백엔드 채용팀에 강하게 어필하면서도 실제 머지 가능성이 높은 것"**을 기준으로 선별함.

KSentinel 프로젝트에서 Kotest DescribeSpec을 실제 테스트 프레임워크로 사용 중이므로, **"사용자로서 발견한 개선"** 스토리가 자연스럽게 만들어짐.

## 저장소 상태

- 업스트림: `kotest/kotest`
- 포크: `PreAgile/kotest`
- 최종 업데이트: 2026-03-18

활발하게 관리되고 있다는 신호:

- `good-first-issue` 라벨이 아직 사용 중
- 2025 후반 ~ 2026 초반까지 외부 기여자 PR 포함 다수 머지
- 전체 커밋 6,249개, 기여자 442명
- 2025년에만 967개 커밋 기록

최근 머지된 PR 예시:

- JUnit4 `@Rule` 지원 추가
- `DataClassEq` comparator 수정
- Gradle 필터 정규화
- Power Assert 스코프 수정
- Docusaurus 문서 업그레이드

## 완료된 PR

| PR | 이슈 | 상태 | 날짜 |
|----|------|------|------|
| — | — | 아직 없음 | — |

## 메인테이너 성향 분석

### 핵심 메인테이너

| 이름 | GitHub | 역할 | 커밋 수 | 특징 |
|------|--------|------|---------|------|
| Sam Samuel | `sksamuel` | BDFL (사실상 1인 의사결정) | 3,119 (50%) | 원하는 기능을 직접 이슈로 올림, 외부 PR 빠르게 머지 |
| Alfonso Ristorato | `alfonsoristorato` | 멤버, 리뷰어 | 30 | `good-first-issue` 생성, CI/CD 관련 작업 |
| Alex Kuznetsov | `AlexCue987` | 빈번한 기여자 | 192 | 어설션 모듈 집중, 활발한 PR 제출 |

### 머지 패턴

최근 머지된 PR에서 관찰된 패턴:

- **버그 수정 > 기능 추가** (CONTRIBUTING.md에 명시적으로 선호)
- 작고 집중된 변경
- 외부 기여자의 버그 수정 PR을 **매우 빠르게** 머지 (일 단위)
- PR 하나에 이슈 하나
- 코드 변경 시 테스트 포함 필수
- Binary Compatibility Validator 통과 필수 (`./gradlew apiDump`)

### 코딩 컨벤션 (주의!)

- **3-space 인덴트** (표준 4가 아님!)
- 불변성(immutability) 우선
- 자기설명적 네이밍
- Kotlin Coding Conventions 준수

### Stale 정책

- 60일 무활동 → stale 라벨
- 7일 후 자동 닫힘
- 예외: `pinned`, `security`, `blocked` 라벨

## 현재 TOP 5 후보 (2026-03-18)

| 순위 | 이슈 | 유형 | 머지 확률 | 포트폴리오 가치 | 상세 문서 |
|------|------|------|----------|---------------|----------|
| 1 | `GH-5755` shouldHaveSingleElement 체이닝 | 어설션 개선 | 매우 높음 | 높음 | [issue-5755-assertion-chaining.md](issue-5755-assertion-chaining.md) |
| 2 | `GH-5620` test-kotest-examples Slack 알림 | CI/CD | 매우 높음 | 낮은 코드 시그널, 높은 진입 가치 | [issue-5620-ci-slack-alert.md](issue-5620-ci-slack-alert.md) |
| 3 | `GH-5728` shouldForNone 성능 문제 | 퍼포먼스 | 중상 | 매우 높음 | [issue-5728-shouldfornone-perf.md](issue-5728-shouldfornone-perf.md) |
| 4 | `GH-5589` shouldEq 타입 안전성 | API 설계 | 중상 | 매우 높음 | [issue-5589-shouldeq-type-safety.md](issue-5589-shouldeq-type-safety.md) |
| 5 | `GH-5202` DataClass + Map 비교 우선순위 | 어설션 설계 | 중간 | 높음 | [issue-5202-dataclass-map-eq.md](issue-5202-dataclass-map-eq.md) |

## 추천 실행 순서

### 1단계: 빠른 성과 (이번 주)

1. **GH-5755 shouldHaveSingleElement 체이닝** — 첫 코드 PR. 2026-03-18 등록된 신선한 이슈, 경쟁자 없음, 하위 호환성 완벽. **첫 PR은 이슈 본문의 `shouldHaveSingleElement and friends` 범위로만 제한.** 비슷한 함수 일괄 수정은 메인테이너가 확장을 원하면 후속 PR로 — 처음부터 sweep하면 PR이 커져서 리뷰 부담 증가
2. **GH-5620 CI Slack 알림** — 첫 빠른 머지 확보용. 메인테이너가 직접 올린 `good-first-issue` + `pinned`, 52일간 미클레임. Kotlin 코드 시그널은 약하지만, **Kotest 첫 머지를 빠르게 확보하는 전략적 가치가 큼.** GH-5755와 병행 가능

### 2단계: 강한 시그널 퍼포먼스 PR (다음 주)

3. **GH-5728 shouldForNone 성능** — 두 번째 강한 기술 시그널 PR. 이슈 신고자 환경에서 `shouldForNone` → `shouldForAll` 교체만으로 대폭 성능 개선 보고됨 (재현 환경에 따라 차이 가능). 어설션 평가 엔진 내부의 에러 수집 로직이 병목으로 추정. 단, **메인테이너가 "실패 리포팅 메시지 품질 보존"을 요구할 수 있어 머지 확률은 중상.** 벤치마크 결과 첨부가 필수. **"성능 최적화"는 백엔드 엔지니어 핵심 역량**

### 3단계: 설계 참여 (신뢰 쌓인 후)

4. **GH-5589 shouldEq** — 설계 참여형. sksamuel 본인이 올린 이슈. 8개 코멘트로 설계 논의 진행 중. **설계 합의 전 코딩 절대 금지** — 먼저 논의를 전부 읽고 의견을 남긴 후, 합의가 확정된 다음에만 구현
5. **GH-5202 DataClass + Map 비교** — 신뢰 쌓인 뒤 도전형. EqResolver 우선순위 체계를 이해해야 함. 7개 코멘트로 근본 원인이 이미 분석됨. 사이드 이펙트 주의

## PR 전략: 묶지 말고 하나씩

각 이슈는 **별도 PR**로 올려야 함. 이유:

- sksamuel은 좁고 리뷰하기 쉬운 PR을 **매우 빠르게** 머지
- 관련 없는 변경을 묶으면 리뷰 부담 증가
- 하나에 수정 요청이 오면 나머지도 막힘
- 별도 PR이 기여 이력을 보기 좋게 만듦
- 머지된 PR 하나하나가 다음 PR의 신뢰도를 올려줌

## 주의할 이슈 (피해야 할 것들)

| 이슈 | 이유 |
|------|------|
| `GH-5621` Spring lifecycle hooks | `tigermint`가 이미 클레임, 활발히 작업 중 |
| `GH-5601` CustomEq data class 실패 | `scottdfedorov`가 이미 PR GH-5602 제출 (3/16 업데이트) |
| `GH-5108` withData native 이벤트 | `sksamuel`이 직접 assign, 프레임워크 핵심부 |
| `GH-4782` IntelliJ Rerun Failed | `sksamuel` assign, 플러그인 영역 |
| `GH-5103` 테스트 메타데이터 저장 | 프레임워크 코어 변경, 첫 기여로 부적합 |
| `GH-4932` Arb.generate 성능 | 프로퍼티 테스팅 엔진 깊숙한 내부, 난이도 매우 높음 |

## 추가 관심 후보 (Tier 3)

| 이슈 | 유형 | 난이도 | 비고 |
|------|------|--------|------|
| `GH-4905` kotest_filter_tests 문서 | 문서 + 버그 가능성 | 낮음-중간 | 9개월 미클레임, 문서 기여 + 숨겨진 버그 발견 가능 |
| `GH-5688` IntelliJ 작은따옴표 테스트 이름 | 플러그인 버그 | 낮음-중간 | 이스케이핑 문제, 29일 미클레임 |

## Testcontainers vs Kotest 비교

| 기준 | Testcontainers-java | Kotest |
|------|-------------------|-------|
| 머지 속도 | 느림 (주 단위) | **빠름 (일 단위)** |
| 메인테이너 수 | 2명 (eddumelendez, kiview) | 1명 BDFL (sksamuel) + 2명 |
| 코드 기여 난이도 | 모듈 단위, 격리됨 | 멀티플랫폼 고려 필요 |
| 포트폴리오 어필 | "인프라/테스팅 도구" | **"Kotlin 전문성"** |
| 경쟁 | 낮음 | 낮음 |
| 진입 전략 | 문서 → 버그 수정 → 기능 | **버그 수정 → 성능 → 설계** |

## PR 제출 전 체크리스트

- [ ] 이슈에 먼저 코멘트 (선점)
- [ ] CONTRIBUTING.md 규칙 확인
- [ ] **3-space 인덴트** 사용 (4가 아님!)
- [ ] `./gradlew apiDump`로 Binary Compatibility 확인
- [ ] `./gradlew check`로 테스트 통과 확인
- [ ] PR 하나에 이슈 하나
- [ ] 코드 변경에는 테스트 포함
- [ ] PR 본문에 `Closes #XXXX`로 이슈 번호 참조
- [ ] 문서 변경 시 versioned_docs의 **모든 버전** 업데이트
