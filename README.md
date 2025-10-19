# android-code-exercise
Skeleton project for the coding exercise for Android candidates

## Skeleton Code Overview

We're starting you off with a simple app skeleton you can build on. Feel free to make any changes
you want. Just because we're doing something in the skeleton doesn't mean you should too -- we want
to see your preferences and skills for Android development.

### Libraries / Dependencies
All dependencies are managed with a Gradle version catalog in `gradle/libs.versions.toml`. If you
have a third-party library you want to use, feel free to add it. Remember to write about it in your
Readme so we know what it does and why you picked it.

### Views or Compose
We provide two different Fragment skeletons you can pick from: `UserSearchFragment` uses XML views,
and `UserSearchComposeFragment` uses Compose. There is no requirement to use one or the other: use
whatever you are comfortable with or think will showcase your skills best in the time you have.

You can pick which fragment to use by changing the `android:name` param in the Activity's layout 
(`activity_user_search.xml`).

### Dependency Injection
We know setting up dependency injection can be time consuming, so we provide a basic Dagger setup
for you to use. This uses dagger.android components, with the `@ContributesAndroidInjector` bindings
in `ui/dagger/BindingModule.kt`
