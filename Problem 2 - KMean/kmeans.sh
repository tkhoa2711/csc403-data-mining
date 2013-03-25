if [[ "$#" -lt 2 ]]; then
	echo -e "Insufficient command line arguments!"
	exit
fi

training_set_file=$1
test_set_file=$2
k=2
m=50
r=1

if [[ "$3" == "-k" ]]; then
	k=$4
elif [[ "$3" == "-m" ]]; then
	m=$4
elif [[ "$3" == "-r" ]]; then
	r=$4

if [[ "$5" == "-k" ]]; then
	k=$6
elif [[ "$5" == "-m" ]]; then
	m=$6
elif [[ "$5" == "-r" ]]; then
	r=$6

if [[ "$7" == "-k" ]]; then
	k=$8
elif [[ "$7" == "-m" ]]; then
	m=$8
elif [[ "$7" == "-r" ]]; then
	r=$8

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
echo "m=$m"
echo "r=$r"

# Compile java source file
# javac -d classes/ src/knn/*.java
javac kmean/*.java

# Build jar file from class files
# jar -cvfm knn.jar Manifest.txt classes/knn/*.class
# jar -cvfe knn.jar knn.KNN classes/knn/*.class

# Run program
# java -jar knn.jar $training_set_file $test_set_file -k $k -d $d
# java -cp src/ knn.KNN ../dataset/classification/iris_train.txt ../dataset/classification/iris_test.txt -k $k -d $d
java -cp kmean/ kmean.KMean $training_set_file $test_set_file -k $k -m $m -r $r
