if [[ "$#" -lt 2 ]]; then
	echo -e "Insufficient command line arguments!"
	exit
fi

training_set_file=$1
output_file=$2
k=2
m=50
r=1

if [[ "$3" == "-k" ]]; then
	k=$4
elif [[ "$3" == "-m" ]]; then
	m=$4
elif [[ "$3" == "-r" ]]; then
	r=$4
fi

if [[ "$5" == "-k" ]]; then
	k=$6
elif [[ "$5" == "-m" ]]; then
	m=$6
elif [[ "$5" == "-r" ]]; then
	r=$6
fi

if [[ "$7" == "-k" ]]; then
	k=$8
elif [[ "$7" == "-m" ]]; then
	m=$8
elif [[ "$7" == "-r" ]]; then
	r=$8
fi

echo "training_set_file=$training_set_file"
echo "output_file    =$output_file"
echo "k=$k"
echo "m=$m"
echo "r=$r"

# Compile java source file
javac kmean/*.java

# Run program
java -cp . kmean.KMean $training_set_file $output_file -k $k -m $m -r $r
