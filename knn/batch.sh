k=5
d=2
training_set_file=../dataset/classification/iris_train.txt
test_set_file=../dataset/classification/iris_test.txt

# Compile java source file
# javac -d classes/ src/knn/*.java
javac src/knn/*.java

# Build jar file from class files
# jar -cvfm knn.jar Manifest.txt classes/knn/*.class
# jar -cvfe knn.jar knn.KNN classes/knn/*.class

# Run program
# java -jar knn.jar $training_set_file $test_set_file -k $k -d $d
# java -cp src/ knn.KNN $training_set_file $test_set_file -k $k -d $d
java -cp src/ knn.KNN ../dataset/classification/iris_train.txt ../dataset/classification/iris_test.txt -k 5 -d 2