# shuliAI
使用Kotlin Multiplatform实现一款Ai对话App，里面需要支持主流大模型接口调用例如deepseek、GPT、Ginimi、文心一言、通义千问等大语言模型，完整代码
# 完整项目结构
核心模块说明：

shared 模块（关键部分）：

commonMain：包含业务逻辑、数据访问、视图模型等跨平台代码

di/Koin.kt：使用Koin实现依赖注入

data/：采用分层架构（local=Room/SQLDelight，remote=Ktor）

presentation/：包含与Compose/Multiplatform兼容的UI逻辑

平台特定模块：

Android：通过MainActivity启动Compose界面

iOS：通过SwiftUI的ContentView显示共享逻辑

构建系统：

使用Gradle Kotlin DSL配置多平台构建

共享模块通过androidMain和iosMain实现平台适配