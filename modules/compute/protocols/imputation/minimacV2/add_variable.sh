#!/bin/sh

usage(){
    echo "################################################################################################################";
    echo "Usage: $0 -w worksheet.csv -v variable_to_add -p value_of_variable -o outputfile.csv";
    echo "Example:";
    echo "sh add_variable.sh -w concattedChunkWorksheet.csv -v imputationResultDir -p /my/results/directory -o output.csv"
    echo "################################################################################################################";
    exit 1;
}

if [ $# -lt 1 ] ; then
    usage;
fi

while getopts w:v:p:o: opt
do
	case "$opt" in

	w) echo "Worksheet: $OPTARG";worksheet="$OPTARG";;
	v) echo "Variable to add: $OPTARG";variable="$OPTARG";;
	p) echo "Variables value: $OPTARG";value="$OPTARG";;
	o) echo "Outputfile: $OPTARG";output="$OPTARG";;
	\?) usage;;
	esac

done


head -1 $worksheet | awk -v avar1=$variable '{print $0,avar1}' OFS="," > "$output"

awk -v avar2=$value 'NR>1{print $0,avar2}' OFS="," $worksheet >> "$output"
