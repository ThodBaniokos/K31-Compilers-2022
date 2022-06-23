all: compile

compile:
	jflex scanner.flex
	java -jar ../java-cup-11b.jar -interface -parser Parser parser.cup
	javac -cp ../java-cup-11b-runtime.jar *.java

execute-stdout:
	java -cp ../java-cup-11b-runtime.jar:. Main

execute-java:
	java -cp ../java-cup-11b-runtime.jar:. Main > ../Main.java
clean:
	rm -f *.class *~
