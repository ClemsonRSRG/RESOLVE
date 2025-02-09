#!/bin/bash
#
# vcgen.sh
# ---------------------------------
# Copyright (c) 2024
# RESOLVE Software Research Group
# School of Computing
# Clemson University
# All rights reserved.
# ---------------------------------
# This file is subject to the terms and conditions defined in
# file 'LICENSE.txt', which is part of this source code package.
#




REPO_PATH=$PWD

echo "$REPO_PATH"


EXECUTABLE="$REPO_PATH/target/RESOLVE-Summer24a-jar-with-dependencies.jar"

echo "$EXECUTABLE"

git submodule update --init --recursive

cd RESOLVE-Workspace
cd RESOLVE
cd Main

RETURN_PATH=$PWD

echo "$RETURN_PATH"

find "$RETURN_PATH" -type f -name "*.rb" | while read -r file; do
    # Get the directory of the file
    dir=$(dirname "$file")
    base_file=$(basename "$file" .rb)

    echo "Directory: $dir"
    echo "File: $file"
    # Output the directory and the file
    cd "$dir"
    java -jar "$EXECUTABLE" -VCs "$file"

    file_found=$(find . -type f -name "$base_file.asrt")

    if [ -n "$file_found" ]; then  # Check if the file was found
        mv "$file_found" "$RETURN_PATH"  # Move the found file to the target directory
    fi
ls "$RETURN_PATH"
done