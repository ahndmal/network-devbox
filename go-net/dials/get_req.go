package dials

import (
	"fmt"
	"io"
	"log"
	"net"
)

func MyGet(host string) {
	//con, err := net.Dial("tcp", "webcode.me:80")
	con, err := net.Dial("tcp", host)
	checkError(err)

	req := fmt.Sprintf("GET / HTTP/1.0\n"+
		"Host: %s\n"+
		"User-Agent: Go client\r\n\r\n", host[:len(host)-3])

	_, err = con.Write([]byte(req))
	checkError(err)

	res, err := io.ReadAll(con)
	checkError(err)

	fmt.Println(string(res))
}

func checkError(err error) {
	if err != nil {
		log.Fatal(err)
	}
}
