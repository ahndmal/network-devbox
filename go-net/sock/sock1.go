package sock

import (
	"github.com/AndriiMaliuta/my_lib"
	"net"
)

func MySockConnect() {
	dial, err := net.Dial("tcp", "localhost:8888")
	my_lib.CheckError(err)

	dial.Write([]byte("Hello from GO server!"))

}
