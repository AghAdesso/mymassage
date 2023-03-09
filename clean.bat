CALL echo "Building maven artifacts..." 
CALL cd mymassage-util/
CALL mvn -e clean
CALL cd ../mymassage-ejb/
CALL mvn -e clean
CALL cd ../mymassage-war/
CALL mvn -e clean
CALL cd ../mymassage-ear/
CALL  mvn -e clean