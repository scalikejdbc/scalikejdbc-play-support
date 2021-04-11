#!/bin/bash

sbt  ++2.12.13 \
    play-initializer/publishSigned \
    play-dbapi-adapter/publishSigned \
    play-fixture/publishSigned

sbt  ++2.13.5 \
    play-initializer/publishSigned \
    play-dbapi-adapter/publishSigned \
    play-fixture/publishSigned
