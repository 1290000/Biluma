# Biluma · 羽哩

An independently maintained third-party Bilibili Android client based on [BiliPai](https://github.com/jay3-yy/BiliPai). Biluma aims to simplify settings and non-essential features while developing clearer defaults and a focused interface.

[简体中文](README.md) · [Repository](https://github.com/1290000/Biluma) · [Issues](https://github.com/1290000/Biluma/issues) · [Releases](https://github.com/1290000/Biluma/releases)

> **This project is in early development and has no independent Biluma release yet.** All nine initial UI and settings reductions are implemented in source, but tests, compilation, and on-device verification have not been performed. It is not an official release by the BiliPai authors or Bilibili.

## Project identity

The Chinese name, 羽哩, combines elements of 羽毛笔 (La Pluma) and 哔哩哔哩. The English and repository name is **Biluma**. Launcher names use English; the naming inspiration does not imply authorization or affiliation with the relevant rights holders.

| `:app` variant | Launcher name | Application ID |
| --- | --- | --- |
| `release` | `Biluma` | `com.biluma.app` |
| `dev` | `Biluma Dev` | `com.biluma.app.dev` |

The separate IDs distinguish release and test installations from each other and from BiliPai. Private app data is not migrated automatically. The source namespace remains `com.android.purebilibili`; changing the application ID does not require renaming the entire source tree.

These settings apply only to the main `:app` module. The retained `app-tv` module has not undergone Biluma branding migration; whether to retain or adapt it is undecided.

## Current status

| Area | Status |
| --- | --- |
| Repository and history | Independent public repository, preserving the baseline's complete ancestor history |
| App identity | Release and Dev application IDs, English launcher names, and matching shortcut targets are configured |
| Updates and feedback | Update checks, release links, About entries, and issue reporting point to Biluma |
| Empty release list | Informational handling is implemented, without empty changelog dialogs or fallback to BiliPai downloads |
| Upstream attribution | The About page retains BiliPai attribution and contributor credits |
| Favorites page | Restored direct folder-content browsing, the top folder dropdown, and checkbox selection from upstream [v0.2.3-alpha.5](https://github.com/jay3-yy/BiliPai/releases/tag/v0.2.3-alpha.5), retaining the four-page switch animation cap from [PR #840](https://github.com/jay3-yy/BiliPai/pull/840); tests and compilation have not been run |
| Feature and settings reduction | All nine initial UI reductions, six playback settings/informational-entry reductions, and six home/navigation setting reductions are implemented in source; favorites are adjusted, without runtime verification |
| Icons and other branding | Not replaced; some copy and assets still come from upstream |
| APK naming and publication | Upstream export names remain; independent signing and version policy are pending |
| Automation | GitHub Actions is currently disabled in this repository's settings |

Changes have only received static checks and an update API connectivity check. Regression test code has been added, but tests, compilation, installation, and on-device verification have not been performed. Configuration and command examples do not establish that a build succeeds.

## UI simplification progress

All nine items have been implemented in batches and merged into `main`. Approved targets and source implementation status are separate; neither implies a release or successful runtime verification.

| Item | Fixed target | Source status |
| --- | --- | --- |
| 1. UI preset | Miuix only; remove MD3-specific UI | Implemented; removed the user-facing preset entry and related resources, fixed runtime selection to Miuix, and retained Material 3 infrastructure for compatibility and fallback |
| 2. Liquid glass | Enabled; retain non-glass UI and compatibility/performance fallbacks | Implemented; removed the global switch, search entry, and old-backup share entry; runtime is fixed on, with non-glass fallback on unsupported devices |
| 3. Single-choice presentation | Anchored popup | Implemented; centered single-choice dialog, setting, and preference access removed |
| 4. Splash icon mask animation | Disabled | Implemented; removed the toggle, animation styles, and startup-only assets; system splash icons are transparent, while wallpaper, preloading, and launcher compatibility entries remain |
| 5. Native Miuix popups | Enabled | Implemented; switch and its disabled alternatives removed; Miuix dialogs no longer fall back to Material through this switch |
| 6. Video tag size | Smallest | Implemented; video tags use the smallest size without settings or backup overrides; removed the unused intermediate size and retained standard tags elsewhere |
| 7. Compact player controls | Enabled | Implemented; removed the toggle, preference, and classic layout branches; compact spacing and scrims are fixed, with sharing retained in the More menu |
| 8. Floating bottom bar | Enabled | Implemented; removed the switch, preference, old-backup field, and docked renderers; search integration, the playback mini-bar, and large-screen side navigation remain |
| 9. Navigation icon cross-scale | Enabled | Implemented; removed the switch and disabled branch; icons scale during indicator transitions and return to their authored size when settled |

All nine options no longer have user-facing selection entries; their dedicated alternatives and inactive child settings have been removed. Legacy theme and liquid-glass keys remain only at necessary migration compatibility boundaries, while runtime selection is fixed to Miuix with liquid glass enabled. Confirmation/input dialogs, slider dialogs, and hinge-safe layouts are outside this batch's removal scope; Material 3 infrastructure and unsupported-device non-glass fallbacks are also retained.

## Fixed playback behavior

- Cross-part/collection resume suggestions no longer open a dialog; playback history and saved-position restoration for the current video remain available.
- Video descriptions start expanded and can still be collapsed manually. Video notes start as an entry that opens the full note list on demand; the separate note-visibility setting remains.
- Double-tap liking is removed; regular liking and other double-tap playback gestures remain.
- Brightness gestures affect only the current playback window, without changing system brightness or requesting write-system-settings permission.
- The settings-page comment IP-location explanation is removed; comments still show location data when available.

Related settings entries, search aliases, preference accessors, and dedicated logic are removed. Legacy preferences no longer control these behaviors, and settings-share imports skip retired fields. This batch has received static checks only; regression test code is updated but has not been run.

## Fixed home and navigation behavior

The main app no longer exposes these switches. Legacy preferences, shared settings, and restored backups cannot override the fixed behavior:

- The home cover carousel is removed, including its autoplay option and dedicated implementation; recommendations use the complete regular video feed.
- Bottom-bar search is enabled and the home top search bar is retained. Scrolling down no longer merges the search control, navigation dock, and playback strip.
- Favorites, history, and watch-later use compact page-scoped search when hosted in the main navigation with an available bottom search entry. Search results can still be inspected and cleared. Standalone search pages, sidebar layouts, and lists without bottom search retain a top search entry.
- The sidebar account-switch entry is always available when using the sidebar; sidebar navigation itself is not forced on.

Unused legacy bottom-search auto-expansion and layout modes are also removed. Active search input, search-page transitions, playback-strip state transitions, and shared navigation visuals remain. The TV module is unchanged. Regression tests are added and static checks are performed; tests, compilation, and on-device verification have not been run for this batch.

## Downloads and feedback

- [Biluma Releases](https://github.com/1290000/Biluma/releases) is the only Biluma release channel and is currently empty. Do not use a BiliPai APK as a Biluma update.
- Report issues and suggestions to [Biluma Issues](https://github.com/1290000/Biluma/issues), rather than sending derivative-specific problems directly upstream.
- Include device and OS details, build version, reproduction steps, and relevant logs or screenshots. Remove cookies, tokens, and personal information before submitting.

## Baseline and upstream relationship

- Upstream: [jay3-yy/BiliPai](https://github.com/jay3-yy/BiliPai).
- Starting tag: [v0.2.8](https://github.com/jay3-yy/BiliPai/releases/tag/v0.2.8).
- Baseline commit: [`edb3d596d381464f02feba646eadc3f023911a75`](https://github.com/jay3-yy/BiliPai/commit/edb3d596d381464f02feba646eadc3f023911a75).
- Biluma development continues on this repository's `main`. For development, `origin` points to Biluma and `upstream` to BiliPai. Keep a separate BiliPai fork and local directory for contributing upstream.

The source still uses `versionName = 0.2.8` and `versionCode = 435`; this does not mean Biluma has published version 0.2.8. Its independent version policy is undecided. See [app/build.gradle.kts](app/build.gradle.kts) for the actual configuration.

## Development and build reference

Read [AGENTS.md](AGENTS.md) before making changes. It defines repository boundaries, development constraints, verification authorization, and commit expectations. Examples in documentation do not authorize automatically running a build.

The current configuration uses **JDK 21, AGP 9.3.1, Gradle 9.5.0, Kotlin 2.4.0, and compileSdk 37**. The main app requires at least **Android 8.0 / API 26**. The sources of truth are the [version catalog](gradle/libs.versions.toml), [Gradle wrapper](gradle/wrapper/gradle-wrapper.properties), and app build configuration.

Some Miuix dependencies use GitHub Packages. Supply credentials with the appropriate read access through user-level Gradle properties `gpr.user` / `gpr.key` or environment variables `GITHUB_ACTOR` / `GITHUB_TOKEN`. Never commit credentials to the repository.

When a developer explicitly chooses to generate an installable local test APK, use the Dev variant:

```powershell
.\gradlew.bat :app:assembleDev
```

The macOS / Linux equivalent is `./gradlew :app:assembleDev`. Do not package, install, or distribute debug / smooth APKs. A formal Release publication still needs independent signing and release workflow configuration.

The currently configured Dev export path is `app/build/outputs/bilipai/dev/BiliPai-<versionName>-dev.apk`; Release uses `app/build/outputs/bilipai/release/BiliPai-<versionName>.apk`. These are inherited naming rules, not generated artifacts. `<versionName>` is the main app's base version name. The Gradle root project name also remains `BiliPai`.

## Main directories

| Directory | Contents |
| --- | --- |
| `app/` | Main app, phone and tablet features, navigation, and UI |
| `app-tv/` | Retained upstream TV app, not yet migrated |
| `settings-core/`, `network-core/`, `core-data/`, `core-player/` | Shared settings, networking, data, and player code |
| `design-system/`, `design-tokens/`, `miuix-navigation/` | UI components, design tokens, and navigation support |
| `plugin-sdk/`, `plugins/` | Plugin interfaces and related code |
| `danmaku-engine/`, `dolby-ffmpeg-decoder/` | Danmaku and audio decoding components |
| `baselineprofile/` | Upstream performance benchmarks and baseline configuration |
| `docs/`, `artwork/`, `scripts/` | Documentation, assets, and tools; inherited assumptions need review before use |

## Documentation and historical references

- Current collaboration rules: [AGENTS.md](AGENTS.md).
- Pinned upstream baseline documentation: [Chinese README](https://github.com/jay3-yy/BiliPai/blob/edb3d596d381464f02feba646eadc3f023911a75/README.md) · [English README](https://github.com/jay3-yy/BiliPai/blob/edb3d596d381464f02feba646eadc3f023911a75/README_EN.md).
- Retained technical references: [Architecture](docs/wiki/ARCHITECTURE.md) · [Code structure](STRUCTURE_GUIDELINES.adoc) · [Plugin development](docs/PLUGIN_DEVELOPMENT.md).

Screenshots, version badges, download links, and community links in upstream READMEs are historical references. [CHANGELOG.md](CHANGELOG.md), the Wiki, old release instructions, `AI.txt`, and `llms.txt` have not all been rewritten and must not be treated as Biluma's released feature list or approved roadmap.

## License and acknowledgments

This project retains the [GNU GPL v3.0](LICENSE). Use, modification, and distribution must comply with the applicable license. When distributing modified versions or binaries, provide corresponding source as required by GPLv3 and preserve license and copyright notices. Third-party code and assets remain subject to their respective licenses; see [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

Thanks to **BiliPai's original author YangY, its maintainers, and all contributors**, along with the open-source projects it uses and references. The complete upstream acknowledgments remain in the [v0.2.8 baseline documentation](https://github.com/jay3-yy/BiliPai/blob/edb3d596d381464f02feba646eadc3f023911a75/README.md#致谢). Biluma's new name does not erase these sources or their Git history.

This project is not officially affiliated with Bilibili or the rights holders of the works that inspired its name. Follow applicable law, platform rules, and permissions when using services, content, and assets.
