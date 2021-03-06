#!/bin/bash

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

clear

bold=$(tput bold)
normal=$(tput sgr0)

systemName=$(uname -s)
baseDir=$(dirname $0)

echo 

# simple check if setup was already done
if [ ! -f "$baseDir/app/src/main/kotlin/com/tailoredapps/template/MyApp.kt" ]; then
    echo "${bold}Setup already completed!"
    echo
    exit 0
fi

echo "${bold}Welcome to the Android Template setup assistant.${normal}"
echo Please enter the details of your project.
echo

read -p "${bold}App name:${normal} " appName
# trim app name
appName=$(echo $appName | xargs echo -n)
appNameNoWhiteSpace=$(echo $appName | sed -e 's/ //g')
appClassName=$(echo $appNameNoWhiteSpace | awk '{print toupper(substr($0,0,1))substr($0,2)}')
appClassName=${appClassName%"app"}
appClassName=${appClassName%"App"}
appClassName=${appClassName%"Application"}

# Check for correct app name
regex="^[A-Za-z0-9 ]+$"
if ! [[ $appName =~ $regex ]]; then
    echo 
    echo Aborting, please enter a correct app name.
    echo 
    exit 1
fi

read -p "${bold}Package name:${normal} " packageName
# trim package name
packageName=$(echo $packageName | xargs echo -n)

# check for correct package name
regex="^[a-z][a-z0-9_]*(\.[a-z0-9_]+)+[0-9a-z_]$"
if ! [[ $packageName =~ $regex ]]; then
    echo
    echo Aborting, please enter a correct package name.
    echo
    exit 1
fi

packagePath=$(echo $packageName | sed 's/\./\//g')


# set app name

if [ "Darwin" == $systemName ]; then
    sed -i '' "s/resValue \"string\", \"app_name\", \"TA Template App/resValue \"string\", \"app_name\", \"$appName/g" $baseDir/app/build.gradle
else
    sed -i "s/resValue \"string\", \"app_name\", \"TA Template App/resValue \"string\", \"app_name\", \"$appName/g" $baseDir/app/build.gradle
fi

if [ "Darwin" == $systemName ]; then
    sed -i '' "s/resValue \"string\", \"leak_canary_display_activity_label\", \"TA Template App/resValue \"string\", \"leak_canary_display_activity_label\", \"$appName/g" $baseDir/app/build.gradle
else
    sed -i "s/resValue \"string\", \"leak_canary_display_activity_label\", \"TA Template App/resValue \"string\", \"leak_canary_display_activity_label\", \"$appName/g" $baseDir/app/build.gradle
fi

if [ "Darwin" == $systemName ]; then
    sed -i '' "s/setProperty(\"archivesBaseName\", \"TAAppTemplate/setProperty(\"archivesBaseName\", \"$appNameNoWhiteSpace/g" $baseDir/app/build.gradle
else
    sed -i "s/setProperty(\"archivesBaseName\", \"TAAppTemplate/setProperty(\"archivesBaseName\", \"$appNameNoWhiteSpace/g" $baseDir/app/build.gradle
fi


# find and replace package name recursively

if [ "Darwin" == $systemName ]; then
    find $baseDir -not -path './.idea*' -a -not -path './.git*' -a -type f \( -iname \*.kt -o -iname \*.java -o -iname \*.xml -o -iname \*.gradle -o -iname \*.pro \) -exec sed -i '' "s/com\.tailoredapps\.template/$packageName/g" {} +
else
    find $baseDir -not -path './.idea*' -a -not -path './.git*' -a -type f \( -iname \*.kt -o -iname \*.java -o -iname \*.xml -o -iname \*.gradle -o -iname \*.pro \) -exec sed -i "s/com\.tailoredapps\.template/$packageName/g" {} +
fi

# find and replace app name recursively

if [ "Darwin" == $systemName ]; then
    find $baseDir -not -path './.idea*' -a -not -path './.git*' -a -type f \( -iname \*.kt -o -iname \*.java -o -iname \*.xml -o -iname \*.gradle \) -exec sed -i '' "s/MyApp/${appClassName}App/g" {} +
else
    find $baseDir -not -path './.idea*' -a -not -path './.git*' -a -type f \( -iname \*.kt -o -iname \*.java -o -iname \*.xml -o -iname \*.gradle \) -exec sed -i "s/MyApp/${appClassName}App/g" {} +
fi

# move files

mkdir -p $baseDir/app/src/main/kotlin/$packagePath
mkdir -p $baseDir/app/src/test/kotlin/$packagePath
mkdir -p $baseDir/app/src/androidTest/kotlin/$packagePath
mv $baseDir/app/src/main/kotlin/com/tailoredapps/template/* $baseDir/app/src/main/kotlin/$packagePath
mv $baseDir/app/src/test/kotlin/com/tailoredapps/template/* $baseDir/app/src/test/kotlin/$packagePath
mv $baseDir/app/src/androidTest/kotlin/com/tailoredapps/template/* $baseDir/app/src/androidTest/kotlin/$packagePath
mv $baseDir/app/src/main/kotlin/$packagePath/MyApp.kt $baseDir/app/src/main/kotlin/$packagePath/${appClassName}App.kt

# remove old folders

originalPackagePathParts=(com tailoredapps template)
newPackagePathParts=(`echo $packageName | sed 's/\./ /g'`)

if [[ $packageName != com.tailoredapps.template* ]]; then
    # New package name is not equal or subpackage of old package name
    
    deletePath=""
    for index in ${!originalPackagePathParts[*]}
    do
        if [ $index -ne 0 ]; then 
            deletePath=${deletePath}/ 
        fi

        deletePath=${deletePath}${originalPackagePathParts[$index]}

        if  [ $index -eq ${#newPackagePathParts[@]} ] || [ ${originalPackagePathParts[$index]} != ${newPackagePathParts[$index]} ]; then
            break
        fi
    done

    rm -r $baseDir/app/src/main/kotlin/$deletePath
    rm -r $baseDir/app/src/test/kotlin/$deletePath
    rm -r $baseDir/app/src/androidTest/kotlin/$deletePath

fi

echo
echo "${bold}Setup complete${normal}"
echo
echo 
