#!/bin/bash

sbt ++2.10.4 \
    play-plugin/publishSigned \
    play-dbplugin-adapter/publishSigned \
    play-fixture-plugin/publishSigned \
    ++2.11.2 \
    play-plugin/publishSigned \
    play-dbplugin-adapter/publishSigned \
    play-fixture-plugin/publishSigned 
