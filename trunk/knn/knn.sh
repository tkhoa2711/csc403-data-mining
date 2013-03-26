if [[ "$#" -lt 2 ]]; then
	echo -e "Insufficient command line arguments!"
	exit
fi

training_set_file=$1
test_set_file=$2
k=1
d=2

if [[ "$3" == "-k" ]]; then
	k=$4
elif [[ "$3" == "-d" ]]; then
	d=$4
fi

if [[ "$5" == "-d" ]]; then
	d=$6
elif [[ "$5" == "-k" ]]; then
	k=$6
fi

<<COMMENT
while getopts k:d: opt; do
	case $opt in
		k)
			k=$OPTARG
			;;
		d)
			d=$OPTARG
			;;
		\?)
			echo "Invalid option: -$OPTARG"
			;;
	esac
done
COMMENT

echo "training_set_file=$training_set_file"
echo "test_set_file    =$test_set_file"
echo "k=$k"
echo "d=$d"

# Compile java source file
# javac -d classes/ src/knn/*.java
javac src/knn/*.java

# Build jar file from class files
# jar -cvfm knn.jar Manifest.txt classes/knn/*.class
# jar -cvfe knn.jar knn.KNN classes/knn/*.class

# Run program
# java -jar knn.jar $training_set_file $test_set_file -k $k -d $d
# java -cp src/ knn.KNN ../dataset/classification/iris_train.txt ../dataset/classification/iris_test.txt -k $k -d $d
java -cp src/ knn.KNN $training_set_file $test_set_file -k $k -d $d
