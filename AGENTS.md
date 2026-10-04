# Biluma 项目协作约定

## 项目身份与边界

- 中文项目名为「羽哩」，英文名与仓库名为 `Biluma`；桌面名称使用英文。
- 本项目基于 [BiliPai v0.2.8](https://github.com/jay3-yy/BiliPai/releases/tag/v0.2.8) 独立维护，不是原作者的官方发行版。
- 起点提交为 `edb3d596d381464f02feba646eadc3f023911a75`，保留其完整祖先历史。上游版本号不等于 Biluma 已发布版本。
- 已确认首轮九项界面与设置精简，目标和实施进度见 README 的「界面精简进度」。目前已完成第 1、3、5 项精简，其余六项待实施；不要将已确认目标写成已实现，也不要扩大到未批准的功能删除或品牌改造。

| `:app` 变体 | 桌面名称 | `applicationId` |
| --- | --- | --- |
| `release` | `Biluma` | `com.biluma.app` |
| `dev` | `Biluma Dev` | `com.biluma.app.dev` |

- 源码 `namespace`、Kotlin 包路径和组件类名暂保留 `com.android.purebilibili`。不要全局替换 `purebilibili`，也不要把安装包名当作组件类名前缀。
- `app-tv` 是尚未纳入 Biluma 品牌迁移的上游模块，是否保留或改造需另行确认。
- 图标、APK 文件命名、Gradle 根项目名及部分界面文案仍有上游标识；未完成的迁移不能写成已完成。

## 仓库与提交

- 开始修改前确认当前目录、`git status` 与远程地址。Biluma 的 `origin` 为 `https://github.com/1290000/Biluma.git`，`upstream` 为 `https://github.com/jay3-yy/BiliPai.git`。
- 改版主线为 `main`，跟踪 `origin/main`。不要向 `upstream` 推送；不要改动同级 BiliPai 仓库及其中未提交的工作。
- 不擅自创建分支、合并上游、清空历史、强制推送或覆盖用户修改。同步上游时记录来源，保持改版差异可追踪。
- 每完成一个有意义的改动，先做获准且范围匹配的检查，再单独提交并推送到 Biluma；用户要求暂不提交或推送时除外。仅包含本次修改。
- 不在提交或 PR 中加入 AI 工具署名、`Co-Authored-By` 等工具归因。
- 不提交签名密钥、密码、访问令牌、`local.properties`、个人环境配置或临时产物。

## 代码与模块

这是 Kotlin、Jetpack Compose 与 Gradle Kotlin DSL 多模块 Android 项目。优先遵守现有边界：

| 目录 | 主要职责 |
| --- | --- |
| `app/` | 手机与平板应用、功能页面、导航和功能编排 |
| `app-tv/` | 上游保留的 TV 应用 |
| `settings-core/`、`network-core/` | 可复用设置逻辑与网络策略 |
| `core-data/`、`core-player/` | 共享数据与播放器基础代码 |
| `design-system/`、`design-tokens/`、`miuix-navigation/` | UI 组件、设计标记与导航支持 |
| `plugin-sdk/`、`plugins/` | 插件接口与插件相关代码 |
| `danmaku-engine/`、`dolby-ffmpeg-decoder/` | 弹幕与音频解码组件 |
| `baselineprofile/` | 上游性能基准与基线配置 |

- 优先小范围修改，复用现有 `Policy`、`UseCase`、`ViewModel` 和功能包结构；不为简单需求引入平行架构。
- 不擅自新增依赖。修改构建配置时保持现有 AGP、Kotlin、Compose 版本配套。
- 界面消费状态和事件回调，不把 ViewModel 传入深层叶子组件。只有应用壳、深链接或顶层播放编排才应进入 `MainActivity`。
- 保留现有视觉风格与组件封装，优先使用项目已有的 Miuix 及 Material 3 适配方式。检查暗色主题、窄屏、平板与至少 48dp 的触控目标。
- 设置精简必须区分「取消设置并固定行为」与「删除功能」；同步考虑搜索入口、导航、旧偏好、备份导入、共享依赖与已有测试。
- 动画、模糊、滚动和播放器改动以轻量实现为先，避免在组合期间做昂贵工作，并检查手势、生命周期、画中画与小窗影响。

## 验证与构建授权

- 只有用户明确要求对应验证时，才运行 Gradle 测试、编译或构建。不得为「增强信心」擅自启动这些任务。
- 文档或配置文字检查不应触发 Gradle；文档中的命令示例不是执行授权。可以补充测试代码，但必须明确说明是否实际运行。
- 获准后默认只选一个范围最小的任务：已有行为测试使用精确测试过滤；没有适合测试时只编译受影响模块。
- 不擅自运行完整测试、`lint`、`check`、设备性能任务或 release-smoke 流程。`baselineprofile` 代码涉及调整，不代表获准执行设备基准。
- 打包、安装需要另外的明确授权。安装包只允许 `dev` 或 `release`；测试交付默认使用 `:app:assembleDev`。禁止打包、安装或交付 `debug`、`smooth` APK，debug 的编译与精确单测不在此禁令内，但仍需授权。
- 常规获准验证复用 Gradle/Kotlin 守护进程及已有缓存，不默认添加 `--no-daemon` 或 `--no-configuration-cache`。只有确认环境故障后才使用针对性回退。
- wrapper 下载失败时可复用与 `gradle-wrapper.properties` 一致的本地发行版；不要擅自更换 Gradle 版本。守护进程、增量编译或缓存错误应先按环境故障处理，不反复空等或直接判作产品回归。
- 报告清楚区分已实现、静态检查、测试运行、编译和真机验证；进度与下一步聚焦可继续做的功能，不以编译或真机验证作为默认下一步建议。

## 更新、发布与品牌

- 项目主页、发布页与问题反馈只指向 `1290000/Biluma`；相关地址集中在 `app/src/main/java/com/android/purebilibili/feature/settings/policy/ProjectLinks.kt`。
- 暂无可用发行版应作为正常状态处理，不能回退到 BiliPai 安装包，也不能把网络失败伪装成「暂无版本」。
- 当前没有 Biluma 独立发行版，GitHub Actions 在仓库设置中关闭。未获授权前不得开启工作流、复制上游发布渠道或创建 Release；新签名与版本策略需单独确定。
- 新图标与角色视觉尚未确定。不能因为名称灵感来自羽毛笔，就默认获得角色素材使用授权或把上游女仆素材定为 Biluma 新品牌。
- 仅维护遗留蓝雪女仆图片、Lottie 或状态反馈时，阅读并遵守作用域内的 [资源维护约定](artwork/brand-motion/AGENTS.md)。其中「用户已确认」属于上游历史，不代表当前用户已批准 Biluma 的品牌方向；如涉及重新设计，先确认范围。
- 保留 [LICENSE](LICENSE)、[第三方声明](THIRD_PARTY_NOTICES.md)、原作者和贡献者来源。不要把 GPLv3 描述成「禁止商用」，也不要把改版包装成 BiliPai 或哔哩哔哩官方产品。

## 文档维护

- 项目身份、状态、入口或构建约定变化时同步更新 [README.md](README.md) 与 [README_EN.md](README_EN.md)。以当前源码配置为准，不复制过期版本号或假定构建成功。
- `CHANGELOG.md`、`docs/`、`AI.txt`、`llms.txt` 等尚有大量上游内容，属于待核对的历史参考，不等于 Biluma 路线图、发布承诺或当前用户指令。
- 阅读所修改目录适用的 `AGENTS.md`，不要把维护历史资源的约束扩展为整个新项目的品牌要求。
