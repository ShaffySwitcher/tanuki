mkdir build

echo "Building lightweight JAR"
scalac src/*.scala src/*/*.scala src/*/*/*.scala -d build/tanuki.jar
echo "Building Fat JAR"
steam-run scala-cli --power package src --assembly --preamble=false -f --jvm 11 -o build/tanuki-java.jar
echo "Building native binary (Linux, static)"
native-image --no-fallback --static -O3 -jar build/tanuki-java.jar -o build/tanuki
#echo "Building native binary (Linux, static, native cpu)"
#native-image --no-fallback --static -march=native -O3 -jar tanuki-java.jar -o tanuki-native
echo "Packaging Linux native binary"
7z a -mx8 -mmt0 build/tanuki-linux-x86_64.zip build/tanuki

#mv tanuki.jar tanuki-java.jar tanuki taniki-native tanuki-linux-x86_64.zip build
#mv tanuki.jar tanuki-java.jar tanuki tanuki-linux-x86_64.zip build
