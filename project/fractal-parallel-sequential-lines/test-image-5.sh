#!/bin/bash

function testImage() {
    local output="output-image-5-five-runs.txt"
	local maxThreads=24
    local imageSize="3200x2400"
	    
	truncate -s 0 "${output}"
    
	date >> "${output}"
    echo "maxThreads : ${maxThreads}" >> "${output}"
    echo "imageSize : ${imageSize}" >> "${output}"

    for (( thread=1; thread<="${maxThreads}"; thread++ ))
    do
        echo "thread : ${thread}" >> "${output}"

        for run in $(seq 5)
        do 
            local options="-q -t ${thread} -s ${imageSize}"
            bash ./runMe.sh "${options}" >> "${output}"
            wait $!
        done
        echo "" >> "${output}"
    done
}

testImage
