# Biluma · 羽哩

基于 [BiliPai](https://github.com/jay3-yy/BiliPai) 独立维护的第三方 Bilibili Android 客户端。希望通过精简设置和非必要功能，逐步形成更明确的默认行为与界面体验。

[English](README_EN.md) · [项目主页](https://github.com/1290000/Biluma) · [问题反馈](https://github.com/1290000/Biluma/issues) · [版本发布](https://github.com/1290000/Biluma/releases)

> **项目处于改造初期，尚无 Biluma 独立发行版。** 完整精简清单仍在逐步确定，当前不能视为已经完成的精简版。本项目不是 BiliPai 原作者或哔哩哔哩的官方发行版。

## 项目身份

中文名「羽哩」结合了羽毛笔与哔哩哔哩的名称元素，英文名及仓库名为 **Biluma**。桌面名称统一使用英文；名称灵感不代表相关权利方的授权或官方关联。

| `:app` 变体 | 桌面名称 | 安装包名（`applicationId`） |
| --- | --- | --- |
| 正式版 `release` | `Biluma` | `com.biluma.app` |
| 测试版 `dev` | `Biluma Dev` | `com.biluma.app.dev` |

两个包名用于区分正式版与测试版，也与 BiliPai 原版隔离；应用私有数据不会自动迁移。源码命名空间仍为 `com.android.purebilibili`，不需要为更换安装包名而全面重命名源码。

这些配置仅适用于主应用 `:app`。保留的 `app-tv` 模块尚未纳入 Biluma 品牌迁移，是否保留或改造需另行确定。

## 当前状态

| 范围 | 状态 |
| --- | --- |
| 仓库与历史 | 已建立独立公开仓库，保留上游基线的完整祖先历史 |
| 应用身份 | 已配置正式版与 Dev 的包名、英文桌面名称和对应快捷方式目标 |
| 更新与反馈 | 更新检查、发布入口、关于页和问题反馈已指向 Biluma |
| 空发布列表 | 已实现暂无可用发行版提示，不打开空白更新日志，不回退到 BiliPai 下载 |
| 上游归属 | 关于页保留 BiliPai 来源和贡献者署名 |
| 功能与设置精简 | 已完成首批首页与导航设置收敛；其他保留、删除和改造范围待确定 |
| 图标与其他品牌资源 | 尚未更换，部分文案和资源仍来自上游 |
| APK 命名与发布 | 仍使用上游导出命名；独立签名和版本策略待确定 |
| 自动化 | GitHub Actions 在本仓库设置中暂时关闭 |

目前的改动仅经过静态检查及更新 API 连通性检查。已补充回归测试代码，但尚未运行测试、编译或进行安装与真机验证；文档中的配置和命令不代表构建已经通过。

## 已固定的首页与导航行为

主应用不再提供以下开关，旧偏好、设置分享与备份恢复不能改变这些行为：

- 首页顶部轮播封面关闭，推荐页直接显示完整普通视频流；轮播默认播放及专用实现一并移除。
- 底栏搜索联动开启，首页顶部搜索条保留；下滑不再触发搜索、导航与播放条合体。
- 收藏、历史、稍后再看在支持底栏页内搜索的主导航页面使用精简搜索，关键词仍可查看和清除。独立搜索页、侧栏模式及没有可用底栏搜索入口的列表保留顶部搜索入口。
- 侧边栏账号切换入口始终提供，但不强制开启侧边栏。

同时移除不再使用的旧底栏搜索自动展开模式与布局模式。仍在使用的搜索输入、搜索页转场、播放条状态切换和共享导航视觉保留；没有调整 `app-tv`。此批修改已补充测试代码并做静态检查，尚未运行测试、编译或真机验证。

## 下载与反馈

- 唯一的 Biluma 版本发布入口是 [Biluma Releases](https://github.com/1290000/Biluma/releases)，当前为空；请勿把 BiliPai 安装包当作 Biluma 更新包。
- 问题与建议请提交至 [Biluma Issues](https://github.com/1290000/Biluma/issues)，不要将改版特有问题直接提交给上游。
- 反馈时注明设备、系统、构建版本与复现步骤，并附适当日志或截图；提交前移除 Cookie、令牌和个人信息。

## 开发起点与上游关系

- 上游项目：[jay3-yy/BiliPai](https://github.com/jay3-yy/BiliPai)。
- 起点标签：[v0.2.8](https://github.com/jay3-yy/BiliPai/releases/tag/v0.2.8)。
- 起点提交：[`edb3d596d381464f02feba646eadc3f023911a75`](https://github.com/jay3-yy/BiliPai/commit/edb3d596d381464f02feba646eadc3f023911a75)。
- Biluma 改动在本仓库 `main` 维护。开发时 `origin` 指向 Biluma，`upstream` 指向原项目；用于向上游贡献代码的 BiliPai fork 应保留在独立目录。

当前源码仍沿用 `versionName = 0.2.8`、`versionCode = 435`，这不是 Biluma 已发布版本的声明。后续独立版本策略尚未确定，实际配置以 [app/build.gradle.kts](app/build.gradle.kts) 为准。

## 开发与构建参考

开始修改前阅读 [AGENTS.md](AGENTS.md)。它说明仓库边界、修改约束、验证授权和提交约定；文档示例不是自动执行构建的授权。

当前配置使用 **JDK 21、AGP 9.3.1、Gradle 9.5.0、Kotlin 2.4.0、compileSdk 37**；主应用最低系统版本为 **Android 8.0 / API 26**。配置来源分别为 [版本目录](gradle/libs.versions.toml)、[Gradle wrapper](gradle/wrapper/gradle-wrapper.properties) 与应用构建文件。

部分 Miuix 依赖来自 GitHub Packages，需要通过用户级 Gradle 属性 `gpr.user` / `gpr.key` 或环境变量 `GITHUB_ACTOR` / `GITHUB_TOKEN` 提供具备相应读取权限的凭据。不要将凭据写入仓库。

开发者明确选择生成本地可安装测试包时，使用 Dev 变体：

```powershell
.\gradlew.bat :app:assembleDev
```

macOS / Linux 对应命令为 `./gradlew :app:assembleDev`。不打包、安装或交付 debug / smooth APK；正式 Release 发布还需完成独立签名和发布流程配置。

当前脚本配置的 Dev 导出路径为 `app/build/outputs/bilipai/dev/BiliPai-<versionName>-dev.apk`，Release 对应 `app/build/outputs/bilipai/release/BiliPai-<versionName>.apk`。这是尚未迁移的上游命名，不是已生成的产物；`<versionName>` 取主应用的基础版本名。Gradle 根项目名也仍为 `BiliPai`。

## 主要目录

| 目录 | 内容 |
| --- | --- |
| `app/` | 主应用、手机和平板功能、导航与界面 |
| `app-tv/` | 上游保留的 TV 应用，尚未迁移 |
| `settings-core/`、`network-core/`、`core-data/`、`core-player/` | 设置、网络、数据与播放器共享代码 |
| `design-system/`、`design-tokens/`、`miuix-navigation/` | UI 组件、设计标记与导航支持 |
| `plugin-sdk/`、`plugins/` | 插件接口和插件相关代码 |
| `danmaku-engine/`、`dolby-ffmpeg-decoder/` | 弹幕与音频解码组件 |
| `baselineprofile/` | 上游性能基准与基线配置 |
| `docs/`、`artwork/`、`scripts/` | 文档、素材与工具，其中仍有上游假设，使用前需核对 |

## 文档与历史参考

- 本项目当前协作规则：[AGENTS.md](AGENTS.md)。
- 上游基线文档：[中文 README](https://github.com/jay3-yy/BiliPai/blob/edb3d596d381464f02feba646eadc3f023911a75/README.md) · [English README](https://github.com/jay3-yy/BiliPai/blob/edb3d596d381464f02feba646eadc3f023911a75/README_EN.md)。
- 保留的技术资料：[架构说明](docs/wiki/ARCHITECTURE.md) · [代码结构规范](STRUCTURE_GUIDELINES.adoc) · [插件开发](docs/PLUGIN_DEVELOPMENT.md)。

上游 README 中的截图、版本徽章、下载链接和社区入口仅供追溯。仓库中的 [CHANGELOG.md](CHANGELOG.md)、Wiki、旧发布流程、`AI.txt` 与 `llms.txt` 也尚未全部改写，不能作为 Biluma 的已发布功能清单或既定路线图。

## 许可证与致谢

本项目沿用 [GNU GPL v3.0](LICENSE)。使用、修改和分发时应遵守适用许可证；分发修改版本或二进制时须按 GPLv3 提供对应源码，并保留许可证与版权声明。第三方代码和素材各自的许可仍然适用，详见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。

感谢 **BiliPai 原作者 YangY、维护者及所有贡献者**，以及项目依赖和参考的开源社区。完整上游致谢可见 [v0.2.8 基线文档](https://github.com/jay3-yy/BiliPai/blob/edb3d596d381464f02feba646eadc3f023911a75/README.md#致谢)；这些来源与 Git 历史不会因 Biluma 更名而抹去。

本项目与哔哩哔哩及名称灵感涉及的作品权利方无官方关联。使用服务、内容与素材时，请遵守相应法律、平台规则及权利方许可。
