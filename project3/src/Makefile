all: compile

compile:
	java -jar ../jtb132di.jar -te minijava.jj
	java -jar ../javacc5.jar minijava-jtb.jj
	javac ./symboltable/*.java ./symboltablebuilder/*.java ./exceptions/*.java ./semanticanalysis/*.java ./offsets/*.java ./irgeneration/*.java ./Main.java

clean:
	rm -f ./symboltable/*.class ./symboltablebuilder/*.class ./exceptions/*.class ./semanticanalysis/*.class ./offsets/*.class ./llfilebuilder/*.class ./irgeneration/*.class *.class *~
