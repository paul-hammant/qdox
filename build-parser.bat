@echo off
bin\yacc -Jnorun -Jnoconstruct -Jclass=Parser -Jsemantic=Value -Jpackage=net.sf.qdox.parser.impl src\grammar\parser.y
copy /Y Parser*.java src\java\net\sf\qdox\parser\impl
del Parser.java ParserVal.java
