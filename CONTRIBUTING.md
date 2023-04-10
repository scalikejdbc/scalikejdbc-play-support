## ScalikeJDBC Contributers' Guide

### Issues

- Questions should be posted to [ScalikeJDBC Users Group](https://groups.google.com/forum/#!forum/scalikejdbc-users-group)
- Please describe about your issue in detail (verison, situation, examples)

### Pull Requests

- Send pull requests toward "develop" or "feature/xxx" branches
- Compatibility always must be kept as far as possible
- scalafmt must be applied to all Scala source code
- Prefer creating scala source code for each class/object/trait (of course, except for sealed trait)

#### Testing your pull request

Testing with default settings is required when push changes:

```sh
sbt test
```

