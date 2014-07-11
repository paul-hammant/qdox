def buildLog = new File( basedir, "build.log" )

// zip contains 6 java files + empty LambdaExamples01/nbproject/private/config.properties 
assert buildLog.text.contains( "Success: 7" )
// zip contains 9 non-java files
assert buildLog.text.contains( "Failure: 9" )
assert buildLog.text.contains( "Error  : 0" )
assert buildLog.text.contains( "Total  : 16" )
