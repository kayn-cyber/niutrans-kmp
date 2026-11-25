# NiuTrans KMP (Kotlin Multiplatform + Compose Multiplatform)

一套 Kotlin 代码覆盖 **Windows / macOS（置顶透明悬浮窗）+ Android + iOS** 的小牛翻译应用。

## 目录结构
- `composeApp/src/commonMain`: 共享核心（小牛翻译 Ktor 客户端、模型、配置接口、Compose 跨端界面与日/夜主题）
- `composeApp/src/desktopMain`: Windows / macOS 桌面入口（无边框、透明、置顶悬浮窗 + 迷你收起模式）
- `composeApp/src/androidMain`: Android 入口（Edge-to-Edge + SharedPreferences 存储）
- `composeApp/src/iosMain`: iOS Compose 视图控制器 + NSUserDefaults 存储
- `iosApp/`: Xcode SwiftUI 壳工程入口

## 运行与构建

### 1. 环境准备
- 安装 **JDK 17+**（桌面与 Android 构建必需）
- 推荐使用 **Android Studio** 或 **IntelliJ IDEA**（安装 Kotlin Multiplatform 插件）直接打开本项目根目录，会自动下载 Gradle Wrapper 并同步依赖。
- 若需编译 **iOS**，需要在 **macOS + Xcode** 下打开 `iosApp` 并链接 `ComposeApp.framework`。

### 2. 常用命令（配置好 JDK + Gradle 后）
- **运行桌面端 (Windows / macOS)**：
  ```bash
  ./gradlew :composeApp:run
  ```
- **打包桌面安装包 (`.exe` / `.msi` / `.dmg`)**：
  ```bash
  ./gradlew :composeApp:packageDistributionForCurrentOS
  ```
- **运行共享单元测试**：
  ```bash
  ./gradlew :composeApp:desktopTest
  ```
- **编译 Android Debug APK**：
  ```bash
  ./gradlew :composeApp:assembleDebug
  ```
