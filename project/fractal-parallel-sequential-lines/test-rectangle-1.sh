#!/bin/bash

function testRectangle() {
    local output="rectangle-1-five-runs.txt"
	local maxThreads=24
    local rectangle="-2.0:0.0:-2.0:0.0"
    local imageSize="1920x1440"
        
    echo "----------------------------------------" >> "${output}"
	date >> "${output}"
    echo "maxThreads : ${maxThreads}" >> "${output}"
    echo "rectangle 1 : ${rectangle}" >> "${output}"
    echo "image size : ${imageSize}" >> "${output}"

    echo "thread : 1" >> "${output}"
    for run in $(seq 5)
    do 
        local options="-q -t 1 -r ${rectangle} -s ${imageSize}"
        bash ./runMe.sh "${options}" >> "${output}"
        wait $!
    done
    echo "" >> "${output}"

    for (( thread=2; thread<="${maxThreads}"; thread+=2 ))
    do
        echo "thread : ${thread}" >> "${output}"

        for run in $(seq 5)
        do 
            local options="-q -t ${thread} -r ${rectangle} -s ${imageSize}"
            bash ./runMe.sh "${options}" >> "${output}"
            wait $!
        done
        echo "" >> "${output}"
    done
}

testRectangle