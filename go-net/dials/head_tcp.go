package dials

import (
	"fmt"
	"github.com/AndriiMaliuta/my_lib"
	"io/ioutil"

	"net"
)

func Head() {

	con, err := net.Dial("tcp", "webcode.me:80")
	my_lib.CheckError(err)

	req := "HEAD / HTTP/1.0\r\n\r\n"

	_, err = con.Write([]byte(req))
	my_lib.CheckError(err)

	res, err := ioutil.ReadAll(con)
	my_lib.CheckError(err)

	fmt.Println(string(res))
}
