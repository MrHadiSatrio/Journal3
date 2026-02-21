# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Structure

Kotlin Multiplatform (KMM) project targeting Android, with shared common code and Android-specific implementations. JVM toolchain: **Java 17**.

### Modules

- **`app-android-journal3`** — Android application shell. Activities declare and invoke a `UseCase`; all wiring lives in `RealJournal3Application`. No business logic here.
- **`app-kmm-journal3`** — Shared business logic. Domain packages: `story/`, `moment/`, `geography/`, `sentiment/`, `token/`, `alert/`. Each feature has a `UseCase` class.
- **`lib-kmm-foundation`** — Core abstractions: `UseCase`, `EventSource`, `EventSink`, `Event`, `Presenter`, `Decor`. Android-specific implementations of these (view bindings, lifecycle sources, activity sinks).
- **`lib-kmm-geography`** — Place/coordinates domain: `Place`, `Places`, `NearbyPlaces`, `Coordinates`. Implementations for HERE Maps, Google Places SDK, and GMS location.
- **`lib-kmm-paraphrase`** — `Paraphraser` interface for AI text refinement (OpenAI-backed).
- **`lib-kmm-json`** — `JsonFile` abstraction for Okio-based JSON key-value storage.
- **`lib-kmm-io`** — `Sources` interface for URI-based I/O with scheme routing.
- **`lib-kmm-collection`** — Lazy iteration utilities (`IteratorIterable`, `PagingIterator`).

### Dependency Flow

```
app-android-journal3 → app-kmm-journal3 → lib-kmm-foundation
                                        → lib-kmm-geography → lib-kmm-foundation, lib-kmm-collection, lib-kmm-json
                                        → lib-kmm-paraphrase
                                        → lib-kmm-io
                                        → lib-kmm-json
```

## Architecture

### Event-Driven UseCase Pattern

Each feature is modeled as a `UseCase` (functional interface: `operator fun invoke()`). A `UseCase` orchestrates by:
1. Subscribing to an `EventSource` (stream of `Event`s via Reaktive `Observable`)
2. Reacting to events by delegating to domain models (never performing work itself)
3. Sinking processed events to `EventSink`s for observability

Events flow from user actions (clicks, text input, back button) and system events (lifecycle) through the use case into sinks (logging, activity completion, analytics).

## Test-Driven Development

This project follows TDD; write a failing test first, then implement just enough to make it pass, then refine. Do not write production code without a corresponding failing test driving it.

## Quality Gate

`./gradlew check` is the **sole quality gate** — it runs formatting (Detekt with auto-correct), tests, coverage (Kover, **90% branch minimum**), and bundle analysis. Run it as often as possible, ideally before every commit. A commit that breaks `check` is not acceptable.

Convenience shortcuts for faster iteration:

```bash
# Tests for a single module
./gradlew :app-kmm-journal3:test

# A single test class
./gradlew :app-kmm-journal3:jvmTest --tests "com.hadisatrio.apps.kotlin.journal3.moment.EditAMomentUseCaseTest"

# Lint only
./gradlew detekt
```

Classes matching `*Fake*` and `*Test` are excluded from coverage.

## Git Conventions

**Granular commits.** Each commit should represent one small, coherent change — a single idea that can be described in one sentence. A commit touching more than a handful of files is the exception, not the norm. When a task involves multiple steps, commit after each step rather than bundling everything into one commit.

**Message format:** `<scope>: <imperative summary>`

- **Scope** is a slash-separated module/package path: `android/journal3`, `kotlin/journal3`, `kotlin/geography`, `android/geography`, `android/foundation`, `root`, etc.
- **Subject** is imperative mood, sentence-case after the colon.
- **Body** (separated by blank line) explains the "why" when the subject alone isn't enough.

Examples from history:
- `android/journal3: Rely on current place to capture moments`
- `kotlin/journal3: Define a use-case to show moments`
- `root: Update dependency org.xerial:sqlite-jdbc to v3.47.2.0`

## External Services

Building requires API keys as environment variables for: Google Places (`KEY_GOOGLE_API`), HERE Maps (`KEY_HERE_API`), OpenAI (`KEY_OAI_API`), Sentry (`KEY_SENTRY`). Debug and release variants use separate keys (e.g., `DEBUG_KEY_HERE_API` / `RELEASE_KEY_HERE_API`).

