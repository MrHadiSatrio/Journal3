# Journal3

![CI Status Badge](https://github.com/MrHadiSatrio/Journal3/actions/workflows/ci.yaml/badge.svg) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=MrHadiSatrio_Journal3&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=MrHadiSatrio_Journal3) [![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=MrHadiSatrio_Journal3&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=MrHadiSatrio_Journal3)

![Reflections](https://github.com/MrHadiSatrio/Journal3/assets/22863610/e27d2d72-bd46-4fdb-8227-89c2988ffe74)
![Story](https://github.com/MrHadiSatrio/Journal3/assets/22863610/5e5c8541-ecb0-4cbe-821c-383793266052)
![Moment Editing](https://github.com/MrHadiSatrio/Journal3/assets/22863610/762f434b-16fa-47f7-8877-1f681f075006)

Journal3 isn't packed with groundbreaking features; it's just another journaling app. But what sets it apart, at least in my view, is its role as a thought experiment—a space to ponder the "what-ifs."

**What if** we could build an Android app without leaning on a specific framework, relying on tried-and-true software engineering patterns and object-oriented principles instead?

**What if** we could bring the power and adaptability of declarative programming into the domain?

**What if** we started treating objects not just as data containers, but as entities capable of intelligent actions?

## Structure

![Graph illustrating common components in Journal3 and their relationships.](https://github.com/MrHadiSatrio/Journal3/assets/22863610/f255d96b-11bb-48fe-b4c9-a3fe2d15e77e)

Each feature (e.g., [editing a moment](https://github.com/MrHadiSatrio/Journal3/blob/develop/app-kmm-journal3/src/commonMain/kotlin/com/hadisatrio/apps/kotlin/journal3/moment/EditAMomentUseCase.kt), [selecting a place](https://github.com/MrHadiSatrio/Journal3/blob/develop/app-kmm-journal3/src/commonMain/kotlin/com/hadisatrio/apps/kotlin/journal3/geography/SelectAPlaceUseCase.kt)) has a corresponding [`UseCase`](https://github.com/MrHadiSatrio/Journal3/blob/develop/lib-kmm-foundation/src/commonMain/kotlin/com/hadisatrio/libs/kotlin/foundation/UseCase.kt) class modeling the interactions between the user and the system. These `UseCase` classes serve as the deepest core and do not return values to the caller.

A `UseCase` typically depends on an [`EventSource`](https://github.com/MrHadiSatrio/Journal3/blob/develop/lib-kmm-foundation/src/commonMain/kotlin/com/hadisatrio/libs/kotlin/foundation/event/EventSource.kt) that exposes a stream of [`Event`](https://github.com/MrHadiSatrio/Journal3/blob/develop/lib-kmm-foundation/src/commonMain/kotlin/com/hadisatrio/libs/kotlin/foundation/event/Event.kt)s. For example, `UseCases` related to editing may attempt to [update the underlying model upon observing a text input event](https://github.com/MrHadiSatrio/Journal3/blob/6c026767261db01532294b3b1a4ccf239da35aec/app-kmm-journal3/src/commonMain/kotlin/com/hadisatrio/apps/kotlin/journal3/story/EditAStoryUseCase.kt#L87). These `Event`s can originate from user actions like [clicks](https://github.com/MrHadiSatrio/Journal3/blob/develop/lib-kmm-foundation/src/androidMain/kotlin/com/hadisatrio/libs/android/foundation/widget/ViewClickEventSource.kt), [text inputs](https://github.com/MrHadiSatrio/Journal3/blob/develop/lib-kmm-foundation/src/androidMain/kotlin/com/hadisatrio/libs/android/foundation/widget/EditTextInputEventSource.kt), and [Back button taps](https://github.com/MrHadiSatrio/Journal3/blob/develop/lib-kmm-foundation/src/androidMain/kotlin/com/hadisatrio/libs/android/foundation/widget/BackButtonCancellationEventSource.kt), as well as from the system (e.g., [Activity lifecycle changes](https://github.com/MrHadiSatrio/Journal3/blob/develop/lib-kmm-foundation/src/androidMain/kotlin/com/hadisatrio/libs/android/foundation/lifecycle/LifecycleTriggeredEventSource.kt)).

After processing, `Event`s are then sunk into [`EventSink`](https://github.com/MrHadiSatrio/Journal3/blob/develop/lib-kmm-foundation/src/commonMain/kotlin/com/hadisatrio/libs/kotlin/foundation/event/EventSink.kt)s. These sinks can be either [global](https://github.com/MrHadiSatrio/Journal3/blob/6c026767261db01532294b3b1a4ccf239da35aec/app-android-journal3/src/main/kotlin/com/hadisatrio/apps/android/journal3/RealJournal3Application.kt#L192) or [local](https://github.com/MrHadiSatrio/Journal3/blob/6c026767261db01532294b3b1a4ccf239da35aec/app-android-journal3/src/main/kotlin/com/hadisatrio/apps/android/journal3/story/EditAStoryActivity.kt#L113). Global sinks provide complete observability to the entire domain, enabling functionalities like [application-wide logging](https://github.com/MrHadiSatrio/Journal3/blob/develop/lib-kmm-foundation/src/androidMain/kotlin/com/hadisatrio/libs/android/foundation/os/SystemLog.kt). Local sinks, on the other hand, may serve scoped functionalities such as [finishing the Activity upon receiving terminal events](https://github.com/MrHadiSatrio/Journal3/blob/develop/lib-kmm-foundation/src/androidMain/kotlin/com/hadisatrio/libs/android/foundation/activity/ActivityCompletionEventSink.kt).

The primary role of a `UseCase` is to act as an orchestrator. For example, if a feature involves persisting a user's change, the `UseCase` will not know how to perform the action itself. Instead, it will [delegate the responsibility to a model](https://github.com/MrHadiSatrio/Journal3/blob/6c026767261db01532294b3b1a4ccf239da35aec/app-kmm-journal3/src/commonMain/kotlin/com/hadisatrio/apps/kotlin/journal3/moment/EditAMomentUseCase.kt#L111). It is then the model's responsibility to [actually complete the task at hand](https://github.com/MrHadiSatrio/Journal3/blob/6c026767261db01532294b3b1a4ccf239da35aec/app-kmm-journal3/src/commonMain/kotlin/com/hadisatrio/apps/kotlin/journal3/moment/filesystem/FilesystemMoment.kt#L82).

This same principle applies to presentation. If a feature involves [displaying a list of items sourced remotely](https://github.com/MrHadiSatrio/Journal3/blob/develop/app-kmm-journal3/src/commonMain/kotlin/com/hadisatrio/apps/kotlin/journal3/geography/SelectAPlaceUseCase.kt) (e.g., from a web API) to the user, it depends on appropriate models to [load the data](https://github.com/MrHadiSatrio/Journal3/blob/develop/lib-kmm-geography/src/commonMain/kotlin/com/hadisatrio/libs/kotlin/geography/here/HereNearbyPlaces.kt) and [render it on the screen](https://github.com/MrHadiSatrio/Journal3/blob/develop/lib-kmm-foundation/src/androidMain/kotlin/com/hadisatrio/libs/android/foundation/widget/RecyclerViewPresenter.kt).

## Highlights

- [Full encapsulation of business requirements](app-kmm-journal3/src/commonMain/kotlin/com/hadisatrio/apps/kotlin/journal3/story/EditAStoryUseCase.kt) within `UseCases` makes [writing tests for them a breeze](app-kmm-journal3/src/commonTest/kotlin/com/hadisatrio/apps/kotlin/journal3/story/EditAStoryUseCaseTest.kt).
- The heavy reliance on interfaces allows for [composable behaviors](https://github.com/MrHadiSatrio/Journal3/blob/6c026767261db01532294b3b1a4ccf239da35aec/app-android-journal3/src/main/kotlin/com/hadisatrio/apps/android/journal3/RealJournal3Application.kt#L116), promoting flexibility.
- Loosely-typed message passing enables `Events` not only to [drive interactions](lib-kmm-foundation/src/androidMain/kotlin/com/hadisatrio/libs/android/foundation/widget/EditTextInputEventSource.kt) but also to [serve as a means of observability](lib-kmm-foundation/src/androidMain/kotlin/com/hadisatrio/libs/android/foundation/os/SystemLog.kt).
- The sole purpose of Activities is to declare a `UseCase` and invoke it, keeping them [light and concise](app-android-journal3/src/main/kotlin/com/hadisatrio/apps/android/journal3/story/EditAStoryActivity.kt).

## Contributing

Are you interested in joining this journey with me? Feel free to open issues to initiate discussions about anything related to Journal3. I also welcome any kind of pull requests that can help advance this project.

### Things to Keep in Mind

- Journal3 currently relies on several external services such as HERE Maps, OpenAI, and Sentry. If you plan to build the project on your machine, you may need to provide your own keys for these services.
- Please note that formatting and branch coverage are strictly enforced in the CI pipeline. Ensure that you've run `./gradlew check` before submitting a pull request.

## License

```
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```
