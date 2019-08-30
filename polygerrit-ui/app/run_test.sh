#!/usr/bin/env bash

wct_bin=$(which wct)
if [[ -z "$wct_bin" ]]; then
    echo "WCT must be on the path."
    exit 1
fi

npm_bin=$(which npm)
if [[ -z "$npm_bin" ]]; then
    echo "NPM must be on the path."
    exit 1
fi

bazel_bin=$(which bazelisk >/dev/null 2>&1)
if [[ -z "$bazel_bin" ]]; then
    echo "Warning: bazelisk is not installed; falling back to bazel."
    bazel_bin=bazel
fi

# WCT tests are not hermetic, and need extra environment variables.
# TODO(hanwen): does $DISPLAY even work on OSX?
${bazel_bin} test \
      --test_env="HOME=$HOME" \
      --test_env="WCT=${wct_bin}" \
      --test_env="WCT_ARGS=${WCT_ARGS}" \
      --test_env="NPM=${npm_bin}" \
      --test_env="DISPLAY=${DISPLAY}" \
      --test_env="WCT_HEADLESS_MODE=${WCT_HEADLESS_MODE}" \
      "$@" \
      //polygerrit-ui/app:wct_test
