#!/bin/sh
dir=$(dirname "$0")
exec java -cp "$dir/build:$CLASSPATH" ShoppingCart
