package main

import (
	"fmt"
	"time"
)

func f(from string) {
	for i := 0; i < 3; i++ {
		fmt.Println(from, ":", i)
	}
}

func Main2() {

	f("direct")

	go f("goroutine")

	go func(msg string) {
		fmt.Println(msg)
	}("main2 1")

	go func(msg string) {
		fmt.Println(msg)
	}("main2 2")

	time.Sleep(time.Second)
	fmt.Println("done")
}
