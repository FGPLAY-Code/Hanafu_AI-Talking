# 🌸 hanafu (花谱)

**AI情感陪伴对话应用** — 基于 Android + Jetpack Compose + Material3

## ✨ 功能特性

- 🤖 **AI对话** — 流式返回聊天界面，打字机效果逐字显示
- 🎨 **双主题** — 花昼模式（粉色渐变）🌞 / 花夜模式（暗粉渐变）🌙
- 🔧 **可配置服务器** — 支持自定义 Base URL、API 密钥、模型选择
- 💾 **本地存储** — Room 数据库保存聊天记录和设置
- 🌊 **渐变背景** — 从上到下的粉色渐变渲染
- 🎯 **Material3 动态取色** — 支持 Android 12+ 动态取色

## 🏗 技术栈

| 技术 | 用途 |
|------|------|
| Kotlin | 开发语言 |
| Jetpack Compose | UI 框架 |
| Material3 | 设计系统 |
| Retrofit2 + OkHttp | 网络请求 |
| Room | 本地数据库 |
| Navigation Compose | 页面导航 |
| Coroutines | 异步处理 |
| Gson | JSON 解析 |

## 📁 项目结构

```
com.hanafu.app/
├── data/
│   ├── local/          # Room 数据库 (DAO, Entity)
│   ├── remote/         # 网络层 (ApiService, DTO, Interceptor)
│   └── repository/     # 数据仓库
├── di/                 # 依赖注入
├── model/              # 数据模型
└── ui/
    ├── navigation/     # 导航路由
    ├── theme/          # 主题系统 (Color, Theme, Type)
    ├── components/     # 通用组件
    ├── chat/           # 聊天页面
    ├── settings/       # 设置页面
    └── splash/         # 启动页
```

## 🚀 快速开始

### 1. 选择模型

支持 `qwen2.5`.

### 2. 开始对话

返回聊天页面，输入消息即可开始与 AI 对话。

## 🎨 主题系统

### 花昼模式 (Light)
- 主色调：樱花粉 → 玫瑰粉渐变
- 背景：白 → 淡粉渐变
- 用户气泡：粉紫渐变
- AI气泡：白色 + 淡粉边框

### 花夜模式 (Dark)
- 主色调：暗粉红 → 深紫红渐变
- 背景：深灰紫 → 暗粉渐变
- 用户气泡：暗粉渐变
- AI气泡：深灰 + 暗粉边框

## 📦 构建要求

- Android Studio Hedgehog (2023.1.1) 或更高
- JDK 17
- Gradle 8.5+
- Kotlin 2.0+
- Min SDK: 26 (Android 8.0)
- Target SDK: 34 (Android 14)

## 📄 License

MIT License
