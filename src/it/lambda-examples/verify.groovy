def buildLog = new File( basedir, "build.log" )

// zip contains 6 java files
assert buildLog.text.contains( "Success: 6" )
// zip contains 10 non-java files 
assert buildLog.text.contains( "Failure: 10" )
assert buildLog.text.contains( "Error  : 0" )
assert buildLog.text.contains( "Total  : 16" )
