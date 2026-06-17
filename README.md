# 규즈의 마법

갤럭시 폰에서 구동하는 APK. Wearable 앱과 Watch Face를 자동으로 컨트롤합니다.

## 프로젝트 구조

```
app/
├── src/main/
│   ├── java/com/gugze/magic/
│   │   ├── MainActivity.kt
│   │   └── service/
│   │       └── WearableControlService.kt
│   ├── res/
│   │   ├── layout/
│   │   ├── values/
│   │   └── xml/
│   └── AndroidManifest.xml
├── build.gradle
└── proguard-rules.pro
```

## 주요 기능

- **Wearable 앱 실행** — Galaxy Watch와 연동
- **AccessibilityService** — UI 요소 자동 제어
- **Watch Face 컨트롤** — 자동화 작업 수행

## 빌드 및 설치

### 필수 요구사항
- Android Studio (최신 버전)
- Android SDK 34+
- Java 17+

### 빌드
```bash
./gradlew assembleRelease
```

### 설치
```bash
./gradlew installRelease
```

## 개발 노트

- AccessibilityService는 설정에서 수동으로 활성화해야 합니다
- Wearable 앱의 패키지명: `com.samsung.android.app.watchmanager`
- UI 요소는 AccessibilityNodeInfo를 통해 접근합니다

## 라이선스

MIT
