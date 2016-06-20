
JAVAC=javac 
CP=.:javaee-api-7.0.jar:jetty-all-9.3.7.v20160115-uber.jar
sources = $(wildcard *.java)
classes = $(sources:.java=.class)

all: $(classes)

clean :
	rm -f *.class

%.class : %.java
	$(JAVAC) -cp $(CP) $<