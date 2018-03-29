#!/bin/sh

# Copyright 2018 Tailored Media GmbH
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License. */

bold=$(tput bold)
normal=$(tput sgr0)

baseDir=$(dirname $0)

echo

# simple check if setup was already done
if [ ! -f "$baseDir/app/src/main/java/com/tailoredapps/template/MyApp.kt" ]; then
    echo "${bold}Setup already completed!"
    echo
    exit 0
fi

echo "${bold}Welcome to the Android Template setup assistant.${normal}"
echo Please enter the details of your project.
echo

read -p "App name:${normal} " appName
# trim app name
appName=$(echo $appName | xargs echo -n)
appNameNoWhiteSpace=$(echo -n "${appName//[[:space:]]/}")

# Check for correct app name
if ! [[ $appName =~ ^[A-Za-z0-9\ ]+$ ]]; then
    echo
    echo Aborting, please enter a correct app name.
    echo
    exit 1
fi

read -p "${bold}Package name:${normal} " packageName
# trim package name
packageName=$(echo $packageName | xargs echo -n)

# check for correct package name
if ! [[ $packageName =~ ^[a-z][a-z0-9_]*(\.[a-z0-9_]+)+[0-9a-z_]$ ]]; then
    echo
    echo Aborting, please enter a correct package name.
    echo
    exit 1
fi

packagePath=$(echo $packageName | sed 's/\./\//g')


# set app name
sed -i '' "s/resValue \"string\", \"app_name\", \"TA Template App/resValue \"string\", \"app_name\", \"$appName/g" $baseDir/app/build.gradle
sed -i '' "s/resValue \"string\", \"leak_canary_display_activity_label\", \"TA Template App/resValue \"string\", \"leak_canary_display_activity_label\", \"$appName/g" $baseDir/app/build.gradle
sed -i '' "s/setProperty(\"archivesBaseName\", \"TAAppTemplate/setProperty(\"archivesBaseName\", \"$appNameNoWhiteSpace/g" $baseDir/app/build.gradle

# find and replace package name recursively
find $baseDir -not -path './.idea*' -a -not -path './.git*' -a -type f \( -iname \*.kt -o -iname \*.java -o -iname \*.xml -o -iname \*.gradle \) -exec sed -i '' "s/com\.tailoredapps\.template/$packageName/g" {} +

# move files
mkdir -p $baseDir/app/src/main/java/$packagePath
mkdir -p $baseDir/app/src/test/java/$packagePath
mkdir -p $baseDir/app/src/androidTest/java/$packagePath
mv $baseDir/app/src/main/java/com/tailoredapps/template/* $baseDir/app/src/main/java/$packagePath
mv $baseDir/app/src/test/java/com/tailoredapps/template/* $baseDir/app/src/test/java/$packagePath
mv $baseDir/app/src/androidTest/java/com/tailoredapps/template/* $baseDir/app/src/androidTest/java/$packagePath

echo
echo "${bold}Setup complete${normal}"
echo