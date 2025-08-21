# Design Patterns in Kotlin

This repository contains examples of design patterns implemented in Kotlin scripts. Currently, only the `creational-patterns` folder is available, but in the future, folders for `structural-patterns` and `behavioral-patterns` will be added.

## How to Run the Scripts


To execute any script, use the following command, replacing `yourScript.kts` with the `.kts` file of your choice:

```
kotlinc -script yourScript.kts
```

Make sure you are in the correct directory, for example:

```
cd creational-patterns/abstract-factory
kotlinc -script abstractFactoryClient.kts
```

## Installing Kotlin

If you do not have Kotlin installed globally, you need to install it. Please follow the official documentation:

- [Kotlin Command Line Installation Guide](https://kotlinlang.org/docs/command-line.html#homebrew)

For macOS users, you can install Kotlin using Homebrew:

```
brew install kotlin
```

For other platforms and more details, refer to the official documentation above.

---

Stay tuned for more design pattern examples in future updates!
