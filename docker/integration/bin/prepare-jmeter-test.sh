#!/bin/sh

. ./env.sh

if [ -d $baseDir/jmeter ]; then
  rm -Rf $baseDir/jmeter
fi
mkdir $baseDir/jmeter
cd $baseDir/jmeter
cp $baseDir/../../jpa-webapp-spring/src/test/jmeter/*.jmx .
sed -i "s/\(<stringProp name=\"HTTPSampler.domain\">\).\+\(<\/stringProp>\)/\1$lb1_ip\2/" *.jmx
sed -i "s/\(<stringProp name=\"HTTPSampler.port\">\).\+\(<\/stringProp>\)/\180\2/" *.jmx

