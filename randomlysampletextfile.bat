set saveclasspath=%classpath%

set classpath=.
set classpath=%classpath%;bin

java -Xmx512m edu.northwestern.at.morphadorner.tools.sampletextfile.RandomlySampleTextFile %1 %2 %3

set classpath=%saveclasspath%

