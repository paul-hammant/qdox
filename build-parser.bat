@echo off
bin\yacc -Jnorun -Jnoconstruct -Jclass=Parser -Jpackage=net.sf.qdox.parser src\grammar\parser.y
copy /Y Parser*.java src\java\net\sf\qdox\parser
del Parser.java ParserVal.java
