package main

import (
	"github.com/AndriiMaliuta/my_lib/curl"
	"log"
	"sync"
)

func main() {
	//host := "https://facebook.com/"
	//hostTcp := "facebook.com:80"
	//hostTcp := "webcode.me:80"

	//dials.Head()
	//dials.MyGet(hostTcp)

	wg := sync.WaitGroup{}

	for a := 0; a < 10000; a++ {
		wg.Add(1)
		go curl.Doss(&wg, "http://localhost:2011/")
		log.Printf(">>> Request %d", a)
	}

	wg.Wait()

	//server, err := net.Listen("tcp", "127.0.0.1:8888")
	//my_lib.CheckError(err)
	//log.Println(">>> GO server listening")
	//
	//for {
	//	accept, err := server.Accept()
	//	my_lib.CheckError(err)
	//
	//	data := make([]byte, 500)
	//	_, err = accept.Read(data)
	//	my_lib.CheckError(err)
	//
	//	log.Println(">> Message from client:")
	//	log.Println(string(data))
	//
	//	accept.Write([]byte("Hello from GO!"))
	//}
}
