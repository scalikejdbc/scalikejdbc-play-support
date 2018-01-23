#!/bin/bash

sbt ++2.11.12 \
    play-initializer/publishSigned \
    play-dbapi-adapter/publishSigned \
    play-fixture/publishSigned

sbt  ++2.12.4 \
    play-initializer/publishSigned \
    play-dbapi-adapter/publishSigned \
    play-fixture/publishSigned

sbt sonatypeRelease # FIXME: doesn't work.. java.lang.IllegalStateException: No staging repository is found. Run publishSigned first
