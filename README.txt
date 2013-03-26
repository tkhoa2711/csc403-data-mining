Example usage:

- knn:

./knn.sh ../dataset/classification/iris_train.txt ../dataset/classification/iris_test.txt
./knn.sh ../dataset/classification/iris_train.txt ../dataset/classification/iris_test.txt -k 7 -d 2

- kmeans:

./kmeans.sh ../dataset/clustering/iris.txt ./output.txt
./kmeans.sh ../dataset/clustering/iris.txt ./output.txt -k 3 -m 30 -r 10