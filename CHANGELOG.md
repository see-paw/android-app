## [1.1.0](https://github.com/see-paw/android-app/compare/v1.0.0...v1.1.0) (2025-11-24)


### Features

* add register user screen and funcionality ([3f68610](https://github.com/see-paw/android-app/commit/3f68610f4d64026676a843ce5c87c574b843d496))


### Bug Fixes

* pipeline now fetches latest backend version ([7ad76e6](https://github.com/see-paw/android-app/commit/7ad76e60c5571b0d4f0ce48dcc662c864a7bd78b))
* Use environment variable for backend Docker image ([c5e3eff](https://github.com/see-paw/android-app/commit/c5e3effb81c5c414d19b74c4ed38d3ee86143c0b))


### Code Refactoring

* **di:** implement Hilt dependency injection ([2da0309](https://github.com/see-paw/android-app/commit/2da030991a2a72ce325635271cc51abeb5942415))
* updated user info with api/Users/me endpoint ([ff6096f](https://github.com/see-paw/android-app/commit/ff6096fac7cfae91239ccd935b4f6e7b6e4e56b9))


### Tests

* add system tests for login screen ([2fad710](https://github.com/see-paw/android-app/commit/2fad710fd84dd4b13a3c2f36842fa1ed72894a82))
* changed timeout in tests ([e58eb39](https://github.com/see-paw/android-app/commit/e58eb39ad8b5c2737f3491405622662118312458))


### Chores

* add delay in authRepository to debug pipeline ([da3b08f](https://github.com/see-paw/android-app/commit/da3b08f200eee0ce68dee6ca735543dbcd2e5a9a))
* add logcat to failing tests in pipeline ([3ebaa6c](https://github.com/see-paw/android-app/commit/3ebaa6c8b5b8447224bed10886b68913cd066939))
* add more time for tests to wait for result in pipeline ([8a02063](https://github.com/see-paw/android-app/commit/8a0206347813d055766dca2de60c95891c3140c1))
* attempt to fix failing tests in pipeline by removing --max-workers=2 ([0a18fe5](https://github.com/see-paw/android-app/commit/0a18fe5ae7c4eab4156ed7b2ebdd1de7b0cd01fb))
* commented code to debug pipeline test failure ([9ca2743](https://github.com/see-paw/android-app/commit/9ca27432c5a0ac26e8e07d6c1c3e5c4cfc52cab1))
* trigger pipeline ([b059759](https://github.com/see-paw/android-app/commit/b059759f3118bd084566cd8395489a2c5a9b9af8))
* trigger pipeline ([dd8da53](https://github.com/see-paw/android-app/commit/dd8da53df3795bbe22e924c953ecc43eff4ffe13))
* trigger pipeline ([3276269](https://github.com/see-paw/android-app/commit/32762696961ec25b74f4a5bd5b12a765288ef2f7))
* update pipeline for emulator use more resources ([c88b60a](https://github.com/see-paw/android-app/commit/c88b60a23a8dac3907c5324d1fb884607ab38e42))
* updated pipeline to use latest main backend container ([5768687](https://github.com/see-paw/android-app/commit/5768687f3fa2df6751690cd167d70b61b3cc2dd2))

## 1.0.0 (2025-11-19)


### Features

* **backendApi:** add service and session manager to enable login via the backend api ([807765f](https://github.com/see-paw/android-app/commit/807765fac84305c6057427a797981e3da39f125b))


### CI/CD

* Restrict branches for Android system tests ([98314fa](https://github.com/see-paw/android-app/commit/98314fa839f829b766db0f49b686cf04bdb683a1))


### Chores

* add base project structure and system tests pipeline ([4f553a1](https://github.com/see-paw/android-app/commit/4f553a12637d9b53db8b8b29b1dd09cf5046fa9b))
* Add permissions to system tests workflow ([0c1a41a](https://github.com/see-paw/android-app/commit/0c1a41af04e80c97aae971f257b648bc36ac0b02))
* add semantic release settings ([162457f](https://github.com/see-paw/android-app/commit/162457fe7ea20c61ca591fe398a68692cc646e48))
* Comment PR with Android system test results ([ea15b01](https://github.com/see-paw/android-app/commit/ea15b0118555296eee92dd01dd9c01b3996e4cfe))
* Enhance test report generation with debug info ([15c179c](https://github.com/see-paw/android-app/commit/15c179c694cc7e737f8361c12c1f157ea30a998d))
* finished base project ([311cc65](https://github.com/see-paw/android-app/commit/311cc65de5a83c09408041c0cae9ce49f0f9db8d))
* fix generate report ([871e91c](https://github.com/see-paw/android-app/commit/871e91c7b6f16350290194ee08bab3c7379fe00e))
* pr test ([838cbda](https://github.com/see-paw/android-app/commit/838cbdac3829b6515e3e2e8c9bbab5f4b6a44b3a))
* Refactor test report generation in workflow ([e02a801](https://github.com/see-paw/android-app/commit/e02a801d39635cfaa030990ded3a6e0901f7ffcb))
* Rename releaserc.json to .releaserc.json ([bcfb7c6](https://github.com/see-paw/android-app/commit/bcfb7c6df1b2f5135dcb4a3b8b10d0e15964d89f))
* trigger pipeline ([7faa363](https://github.com/see-paw/android-app/commit/7faa363457d2cd28ac8c8710ae69a6c0c1b9e49d))
* trigger pipeline ([b42b45a](https://github.com/see-paw/android-app/commit/b42b45aca6a59d7d227e4256c0ba2d5a3f3339f6))
* trigger pipeline ([651bd14](https://github.com/see-paw/android-app/commit/651bd143ca6deb6c94d250bc20300037d60d4768))
* trigger pipeline ([9ff0804](https://github.com/see-paw/android-app/commit/9ff080470d21acfa49783c8c7b510e58c713a5ca))
* trigger pipeline ([664144a](https://github.com/see-paw/android-app/commit/664144af4bd0cd3313df4522c49c748866de6fab))
* update base project from dev ([0fcbe52](https://github.com/see-paw/android-app/commit/0fcbe5272fbb26609fdc651cb108cd84880346f5))
* Update branches for Android system tests workflow ([c4347d0](https://github.com/see-paw/android-app/commit/c4347d04fbbac6b971d1dae7778a2f087e248cf8))
* update dependencies ([a5dafcc](https://github.com/see-paw/android-app/commit/a5dafcc1a72a2aa53d347fbdd2edd734dd72ed1a))
* Update JAVA_VERSION from 11 to 17 ([c423d5e](https://github.com/see-paw/android-app/commit/c423d5e420a7cd19e68348b03d90d3327c7ef148))
* update pipeline ([3418e35](https://github.com/see-paw/android-app/commit/3418e35084f3ba6d0a8fe3f4f7db6e481d744e96))
* update pipeline ([27546fa](https://github.com/see-paw/android-app/commit/27546fae820d8d64805531938f8792581716e26d))
