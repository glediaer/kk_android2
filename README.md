# KrossKomics (kk_android2)

웹툰/만화 스트리밍 Android 앱 - KrossKomics 2차 리뉴얼 버전

## 📱 프로젝트 개요

- **앱 이름**: KrossKomics
- **패키지**: `com.krosskomics`
- **버전**: 0.4.4 (versionCode 37)
- **언어**: Kotlin 88.1%, Java 11.9%
- **최소 SDK**: 21 (Android 5.0)
- **타겟 SDK**: 29 (Android 10)

## 🏗️ 기술 스택

### 핵심
- **Kotlin** 1.4.10
- **AndroidX** (AppCompat, Core-KTX, ConstraintLayout, ViewPager2, Material)
- **MVVM** 아키텍처 (Repository, ViewModel 패턴)
- **RxJava2** + **Kotlin Coroutines**

### 네트워킹 & 이미지
- **Retrofit2** + Gson
- **Glide** (이미지 로딩)
- **Fresco** (이미지 캐싱)
- **Okio**

### Firebase
- Firebase Auth (Google, Facebook 로그인)
- Firebase Messaging (FCM 푸시)
- Firebase Crashlytics
- Firebase Analytics
- Firebase Invites

### 미디어 & UI
- **ExoPlayer** (비디오 재생)
- **Lottie** (애니메이션)
- **Chuck** (디버그용 네트워크 모니터링)

### 기타
- **Google Play Billing** (인앱 결제)
- **Facebook SDK** (로그인/공유)
- **AppsFlyer** (앱 설치 추적)
- **TedPermission** (권한 처리)
- **Stetho** (디버깅)

## 📂 프로젝트 구조

```
app/src/main/java/com/krosskomics/
├── KJKomicsApp.kt          # Application 클래스
├── coin/                   # 코인/캐시/티켓 관리
│   ├── activity/           # CashHistoryActivity, CoinActivity, TicketHistoryActivity
│   ├── fragment/           # CashHistoryFragment
│   ├── repository/         # CashHistoryRepository, CoinRepository, TicketHistoryRepository
│   └── viewmodel/
├── comment/                # 댓글 기능
│   ├── activity/           # CommentActivity, CommentReportActivity
│   ├── repository/
│   └── viewmodel/
├── common/                 # 공통 컴포넌트
│   ├── activity/           # BaseActivity, RecyclerViewBaseActivity
│   ├── adapter/            # CommonPagerAdapter, RecyclerViewBaseAdapter
│   ├── data/               # Data 모델 (Book, Episode, Genre 등)
│   ├── fragment/           # BaseFragment
│   ├── model/              # 도메인 모델
│   ├── repository/         # CommonRepository
│   └── viewmodel/          # BaseViewModel
├── event/                  # 이벤트
├── fcm/                    # Firebase Cloud Messaging (MyFirebaseMessagingService)
├── genre/                  # 장르별 상세
├── home/                   # 메인 홈 화면
│   ├── activity/           # MainActivity, HomePopupActivity
│   ├── adapter/             # HomeAdapter, MainBannerPagerAdapter
│   └── repository/         # MainRepository
├── library/                # 서재
│   ├── activity/           # LibraryActivity, DownloadEpActivity, DownloadViewerActivity
│   ├── fragment/           # LibraryFragment, GiftBoxFragment
│   └── repository/
├── login/                  # 로그인 (LoginActivity, LoginIntroActivity)
├── mainmenu/               # 메인 메뉴
│   ├── fragment/           # GenreFragment, OnGoingFragment, RankingFragment, WaitFreeFragment
│   └── repository/
├── more/                   # 더보기
├── mynews/                 # 내 뉴스
├── notice/                 # 공지사항, FAQ
├── reader/                 # 웹툰 뷰어
├── search/                # 검색
├── series/                # 시리즈 상세
└── subscribe/              # 구독
```

## 🚀 시작하기

### 사전 요구사항

- Android Studio 4.0 이상
- JDK 8
- Android SDK 29

### 프로젝트 가져오기

```bash
# Git으로 클론
git clone https://github.com/glediaer/kk_android2.git
cd kk_android2
```

또는 **Sourcetree** 등 Git GUI 도구로 저장소를 클론할 수 있습니다.

### 빌드 및 실행

```bash
# 디버그 빌드
./gradlew assembleDebug

# 릴리스 빌드
./gradlew assembleRelease

# 클린 빌드
./gradlew clean assembleDebug
```

Android Studio에서 프로젝트를 열고 Run 버튼으로 에뮬레이터 또는 실제 기기에 설치할 수 있습니다.

### 설정

1. **google-services.json**: Firebase Console에서 프로젝트 생성 후 `app/` 디렉터리에 배치
2. **local.properties**: `sdk.dir` 경로 설정 (Android Studio가 자동 생성)

## 📋 주요 기능

| 기능 | 설명 |
|------|------|
| 홈 | 메인 배너, 이벤트, 장르/연재중/랭킹/무료특화 메뉴 |
| 서재 | 다운로드 웹툰, 선물함 |
| 뷰어 | 웹툰/만화 읽기 (이미지, 비디오) |
| 코인 | 캐시 충전, 티켓/코인 사용 내역 |
| 댓글 | 웹툰 댓글 작성 및 신고 |
| 검색 | 작품 검색 |
| 로그인 | Google, Facebook 소셜 로그인 |
| 구독 | 인앱 결제 구독 |
| 푸시 | FCM 알림 |

## 🧪 테스트

```bash
# 단위 테스트
./gradlew test

# Android Instrumented 테스트
./gradlew connectedAndroidTest
```

## 📄 라이선스

이 프로젝트의 라이선스 정보는 저장소 소유자에게 문의하세요.

## 📞 문의

- 저장소: [github.com/glediaer/kk_android2](https://github.com/glediaer/kk_android2)
- Repo owner 또는 admin에게 문의
