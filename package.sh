echo "Building lightweight JAR"
scalac src/*.scala src/*/*.scala src/*/*/*.scala -d tanuki.jar
echo "Building Fat JAR"
steam-run scala-cli --power package src --assembly --preamble=false --jvm 8 -o tanuki-java.jar
