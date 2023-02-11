package main

import (
	"github.com/AndriiMaliuta/my_lib"
	"log"
	"net"
)

func main() {
	//host := "https://facebook.com/"
	//hostTcp := "facebook.com:80"
	//hostTcp := "webcode.me:80"

	//dials.Head()
	//dials.MyGet(hostTcp)

	server, err := net.Listen("tcp", "127.0.0.1:8888")
	my_lib.CheckError(err)
	log.Println(">>> GO server listening")

	for {
		accept, err := server.Accept()
		my_lib.CheckError(err)

		data := make([]byte, 500)
		_, err = accept.Read(data)
		my_lib.CheckError(err)

		log.Println(">> Message from client:")
		log.Println(string(data))

		accept.Write([]byte("Hello from GO!"))
	}
}
