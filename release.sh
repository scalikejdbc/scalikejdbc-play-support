#!/bin/bash

sbt ++2.11.11 \
    play-initializer/publishSigned \
    play-dbapi-adapter/publishSigned \
    play-fixture/publishSigned \
    ++2.12.3 \
    play-initializer/publishSigned \
    play-dbapi-adapter/publishSigned \
    play-fixture/publishSigned \
    sonatypeRelease # FIXME: doesn't work.. java.lang.IllegalStateException: No staging repository is found. Run publishSigned first
