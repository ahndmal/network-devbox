package main

import (
	"github.com/AndriiMaliuta/my_lib/dials"
)

func main() {
	//host := "https://facebook.com/"
	hostTcp := "facebook.com:80"
	//hostTcp := "webcode.me:80"

	dials.Head()
	dials.MyGet(hostTcp)

}
