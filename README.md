# shuliAI
使用Kotlin Multiplatform实现一款Ai对话App，里面需要支持主流大模型接口调用例如deepseek、GPT、Ginimi、文心一言、通义千问等大语言模型，完整代码
# 完整项目结构
Shuli_AI-Chat-KMM/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── shared/
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/
│       │   ├── kotlin/
│       │   │   ├── di/
│       │   │   │   └── Koin.kt
│       │   │   ├── data/
│       │   │   │   ├── local/
│       │   │   │   │   ├── ChatDatabase.kt
│       │   │   │   │   └── Dao.kt
│       │   │   │   ├── remote/
│       │   │   │   │   ├── ApiClient.kt
│       │   │   │   │   └── providers/
│       │   │   │   └── repository/
│       │   │   ├── domain/
│       │   │   │   ├── model/
│       │   │   │   └── usecase/
│       │   │   ├── presentation/
│       │   │   │   ├── viewmodel/
│       │   │   │   └── screen/
│       │   │   └── utils/
│       │   └── resources/
│       ├── androidMain/
│       │   └── kotlin/
│       │       └── Platform.kt
│       └── iosMain/
│           └── kotlin/
│               └── Platform.kt
├── androidApp/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/ai_chat/
│       │   └── MainActivity.kt
│       └── res/
└── iosApp/
├── build.gradle.kts
└── src/
└── iosApp/
├── AppDelegate.swift
└── ContentView.swift