#!/bin/bash

sbt  ++2.12.11 \
    play-initializer/publishSigned \
    play-dbapi-adapter/publishSigned \
    play-fixture/publishSigned

sbt  ++2.13.1 \
    play-initializer/publishSigned \
    play-dbapi-adapter/publishSigned \
    play-fixture/publishSigned
