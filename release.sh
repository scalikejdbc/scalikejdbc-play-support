#!/bin/bash

sbt ++2.10.5 \
    play-initializer/publishSigned \
    play-dbapi-adapter/publishSigned \
    play-fixture/publishSigned \
    ++2.11.7 \
    play-initializer/publishSigned \
    play-dbapi-adapter/publishSigned \
    play-fixture/publishSigned \
    sonatypeRelease

