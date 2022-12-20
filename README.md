# Journal3

![CI Status Badge](https://github.com/MrHadiSatrio/Journal3/actions/workflows/ci.yaml/badge.svg) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=MrHadiSatrio_Journal3&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=MrHadiSatrio_Journal3) [![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=MrHadiSatrio_Journal3&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=MrHadiSatrio_Journal3)

There's barely anything special about the features that Journal3 is offering,
it's literally yet another journaling application.

What _is_ special about it though (or at least what *I* think is special about
it) is that at the same time it's also a thought experiment; an avenue to
explore the what-ifs.

**What if** there's a way to code an Android application without any
architecture? Relying instead on the good ol' software engineering patterns
and object-oriented principles?

**What if** there's a way to bring the power and flexibility of declarative
programming to the domain?

**What if** we treat objects with respect instead of merely thinking of them
as dumb containers of data?

## Structure

I do not intend to apply any kind of architecture to Journal3 nor am I trying
to enforce any patterns within it. However, there is a common structure that
you would come to realize when diving deeper into the codebase.

- `UseCase` as representation of business requirements/features
- `Event` as the dominant way of inter-objects communication
- `Presenter` as the renderer of objects (not to be confused with MVP)

One thing that I hope you'd also notice is that they are not necessarily
architecture components, rather merely a byproduct of encapsulation.

## Highlights

- [Full encapsulation of business requirements](app-kmm-journal3/src/commonMain/kotlin/com/hadisatrio/apps/kotlin/journal3/story/EditAStoryUseCase.kt)
  (including event handling) within `UseCase` classes makes [writing tests a breeze](app-kmm-journal3/src/commonTest/kotlin/com/hadisatrio/apps/kotlin/journal3/story/EditAStoryUseCaseTest.kt).
- Heavy reliance on interfaces at program boundaries allows for [composable](app-kmm-journal3/src/commonMain/kotlin/com/hadisatrio/apps/kotlin/journal3/story/cache/CachingStoriesPresenter.kt)
  [behaviors](app-kmm-journal3/src/commonTest/kotlin/com/hadisatrio/apps/kotlin/journal3/story/SelfPopulatingStories.kt).
- Loosely-typed message passing allows for `Event`s not only to [drive interactions](lib-kmm-foundation/src/androidMain/kotlin/com/hadisatrio/libs/android/foundation/widget/EditTextInputEventSource.kt)
  but also to become [a means of observability](lib-kmm-foundation/src/androidMain/kotlin/com/hadisatrio/libs/android/foundation/os/SystemLog.kt).
- Activities' sole purpose is to declare a `UseCase` and invoke it; keeping them
  [light and concise](app-android-journal3/src/main/kotlin/com/hadisatrio/apps/android/journal3/story/EditAStoryActivity.kt).

## Contributing

Interested in joining this journey with me? Feel free to open up issues to
start a discussion on anything related to Journal3. I also accept any kind
of pull-requests that would help push this journey forward.

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
