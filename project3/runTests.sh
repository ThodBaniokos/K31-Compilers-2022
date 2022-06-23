#! /bin/bash
myOutputPath="./llvm-output/"
courseOutputPath="./llvm-examples/"

# run make to create executable
(cd src/; make; java Main ../minijava-input-files/*.java; cd ../)

# for each file in the input directory
for file in "$myOutputPath"/*; do

    # get the file name
    NAME="$(basename $file .ll)"

    echo "Compiling $NAME"

    # compile the file to create an llvm executable
    $1 -Wno-everything -g3 -o ./"$NAME".out "$file"

    # if there is a given .ll file example, compile it and compare the output
    # using the diff command, if the output is different then the different
    # parts of the output are printed to the console
    if [ -f "$courseOutputPath"/"$NAME".ll ]; then

        # compile the file to create an llvm executable
        $1 -Wno-everything -g3 -o ./"$NAME"EX.out "$courseOutputPath"/"$NAME".ll

        # run the executable produced by me and save the output to a file
        ./"$NAME".out > "$NAME".txt

        # run the executable given by the course staff and save the output to a file
        ./"$NAME"EX.out > "$NAME"EX.txt
        echo "Comparing $NAME"

        # compare the two files
        diff "$NAME".txt "$NAME"EX.txt

        # delete the .out and .txt files
        rm ./"$NAME".out ./"$NAME"EX.out
        rm "$NAME".txt "$NAME"EX.txt
    else

        # if there is no given .ll file example, run the executable produced by me and print the output
        echo "No example for $NAME, executing my result."
        ./"$NAME".out
        rm ./"$NAME".out
    fi
done

# delete the .ll files and .class files, with make clean
(cd src/; make clean; cd ../; rm -rf ./llvm-output/*.ll)
