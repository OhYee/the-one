target/input/MessageWithCopiesCreateEvent.class target/input/MessageWithCopiesEventGenerator.class target/routing/SprayAndWaitRouterWithDiffCopies.class target/report/MyReport.class	:	src/input/MessageWithCopiesCreateEvent.java src/input/MessageWithCopiesEventGenerator.java src/routing/SprayAndWaitRouterWithDiffCopies.java src/report/MyReport.java	
	javac -sourcepath src -d target -extdirs lib/ src/input/MessageWithCopiesCreateEvent.java src/input/MessageWithCopiesEventGenerator.java src/routing/SprayAndWaitRouterWithDiffCopies.java src/report/MyReport.java

all: 
	compile.sh