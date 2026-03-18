# Issue GH-5620: Make action test-kotest-examples alert

## 기본 정보

| 항목 | 내용 |
|------|------|
| 이슈 | [GH-5620](https://github.com/kotest/kotest/issues/5620) |
| 제목 | Make action test-kotest-examples alert |
| 작성자 | `alfonsoristorato` (메인테이너) |
| 작성일 | 2026-01-25 |
| 라벨 | `good-first-issue`, `maintenance`, `pinned` |
| 클레임 | **없음** (52일간 미클레임) |
| 코멘트 | 0 |
| 모듈 | `.github/workflows/` (CI/CD) |

## 문제

`test-kotest-examples` GitHub Action이 매일 cron으로 실행되지만, 실패 시 알림이 없어서 깨진 예제를 놓칠 수 있음.

## 해결 방향

GitHub Actions 워크플로우에 Slack 알림 스텝 추가.

### 변경 범위

- `.github/workflows/test-kotest-examples.yml` 수정
- 실패 시 `kotest-devs` Slack 채널로 알림 전송

### 구현 포인트

1. `slackapi/slack-github-action` 또는 유사 GitHub Action 사용
2. `if: failure()` 조건으로 실패 시에만 알림
3. Slack webhook URL은 GitHub Secrets로 관리 (이미 설정되어 있는지 확인 필요)

## 머지 확률 평가

| 요소 | 평가 |
|------|------|
| 하위 호환성 | ✅ 해당 없음 (CI/CD만) |
| 변경 범위 | ✅ 매우 좁음 (YAML 1파일) |
| 테스트 부담 | ✅ 없음 |
| 메인테이너 관심 | ✅ 메인테이너가 직접 올린 이슈 + pinned |
| 경쟁자 | ✅ 없음 (52일간) |
| **종합 머지 확률** | **매우 높음** |

## 포트폴리오 가치

- **낮은 코드 시그널, 높은 진입 가치** — Kotlin 코드 역량 어필은 약하지만, maintainer가 직접 올린 pinned + good-first-issue라서 **"빠른 첫 Kotest 머지 확보용"**으로서의 전략적 가치가 큼
- 머지 1개가 쌓이면 다음 PR(GH-5755, GH-5728)의 신뢰도가 올라감
- GH-5755와 병행해서 두 방향(코드 + CI/CD) 동시 진행

## 실행 계획

1. 이슈에 코멘트: "이거 하겠다. 기존에 사용하는 Slack action 패턴이 있나요?"
2. `test-kotest-examples.yml` 확인
3. 기존 Kotest 워크플로우에서 Slack 알림 패턴이 있는지 확인 (있으면 동일 패턴 사용)
4. 없으면 `slackapi/slack-github-action` 사용, 메인테이너에게 선호 확인
5. PR 제출

## 리스크

- Slack webhook secret이 이미 설정되어 있지 않으면 메인테이너에게 설정 요청 필요
- **maintainer에게 먼저 existing org-standard Slack action pattern이 있는지 물어야 함** — 이게 없으면 PR이 기술적으로 맞아도 운영 선호 때문에 늦어질 수 있음
- 알림 포맷(메시지 내용, 채널)에 대한 선호가 있을 수 있음 → 이슈에 먼저 물어보기
