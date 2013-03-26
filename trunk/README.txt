Example of running program.

-------------------------------------------------------------------------------

Linux/Unix: running using bash script

- knn

./knn.sh ../dataset/classification/iris_train.txt ../dataset/classification/iris_test.txt
./knn.sh ../dataset/classification/iris_train.txt ../dataset/classification/iris_test.txt -k 7 -d 2

- kmeans

./kmeans.sh ../dataset/clustering/iris.txt ./output.txt
./kmeans.sh ../dataset/clustering/iris.txt ./output.txt -k 3 -m 30 -r 10

-------------------------------------------------------------------------------

Windows: running using java

- knn

javac src\knn\*.java
java -cp src\ knn.KNN ..\dataset\classification\iris_train.txt ..\dataset\classification\iris_test.txt -k 7 -d 2

- kmeans

javac kmean\*.java
java -cp . kmean.KMean ..\dataset\clustering\iris.txt .\output.txt -k 3 -m 30 -r 10
