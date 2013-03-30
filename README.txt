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

-------------------------------------------------------------------------------

Troubleshooting running command line with Java in Windows

For those who don't have Java installed, please download and install the JDK from www.java.com.

To be able to run command 'java' and 'javac' in cmd. You have to make sure both executables are in the PATH system environment. Follow the below instruction to go to the settings:

Computers -> Properties -> Advanced System Settings -> Advanced -> Environment Variables.

Edit the PATH system variable by adding the path to the 'bin' folder (where all the executables located) within the location of your Java installation. For example:

"C:\Program Files\Java\jdk1.6.0_24\bin\"

Save and close everything. Restart the command line interface. You should be able to run java/javac now.

