package my_lib

import (
	"bufio"
	"crypto/tls"
	"fmt"
	"golang.org/x/net/http2"
	"log"
	"net"
	"net/http"
)

func Http2Core(host string) {
	//log.Printf("HEADERS\n  + END_STREAM\n  + END_HEADERS\n"+
	//	"  :method = GET\n"+
	//	"  :scheme = https\n"+
	//	"  :authority = %s\n"+
	//	"  :path = /\r\n"+
	//	"  host = %s\n"+
	//	"  accept = text,html", host, host)

	response, err := MyClient().Get(host)
	CheckError(err)
	log.Println(response)
}

func MyClient() *http.Client {
	client := &http.Client{}
	client.Transport = &http2.Transport{
		AllowHTTP: true,
		DialTLS: func(netw, addr string, cfg *tls.Config) (net.Conn, error) {
			return net.Dial(netw, addr)
		}}

	return client
}

func MySockListen() {
	server, err := net.Listen("tcp", "example.com:80")
	CheckError(err)

	for {
		conn, err := server.Accept()
		CheckError(err)

		reply := make([]byte, 1024)

		_, err = conn.Read(reply)

		conn.Write([]byte("Hello from GO!"))
		CheckError(err)

		fmt.Println(string(reply))

		DialClient()

	}
}

func DialClient() {
	//conn, err := net.Dial("tcp", "localhost:7071")
	conn, err := net.Dial("tcp", "example.com:80")
	if err != nil {
		log.Println(err)
	}

	//conn.Write()
	fmt.Fprintf(conn, "GET / HTTP/1.0\r\n\r\n")

	status, _ := bufio.NewReader(conn).ReadString('\n')

	fmt.Println(status)

}

//func dialTcpHttp2(host string) {
//	//host := "example.com:80"
//	conn, err := net.Dial("tcp", host)
//	CheckError(err)
//
//	//preface := "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n"
//	firstReq := "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n"
//
//	req := "send HEADERS frame <length=43, flags=0x25, stream_id=13>\n" +
//		"; END_STREAM | END_HEADERS | PRIORITY\n" +
//		"(padlen=0, dep_stream_id=11, weight=16, exclusive=0)\n" +
//		"; Open new stream\n" +
//		":method: GET\n" +
//		":path: /\n" +
//		":scheme: https\n" +
//		":authority: www.facebook.com\n" +
//		"accept: */*\n" +
//		"accept-encoding: gzip, deflate\n" +
//		"user-agent: IDEA"
//
//	req2 := "send HEADERS frame <length=43, flags=0x25, stream_id=13>\n" +
//		"; END_STREAM | END_HEADERS | PRIORITY\n" +
//		"(padlen=0, dep_stream_id=11, weight=16, exclusive=0)\n" +
//		"; Open new stream" +
//		"\n:method: GET\n:path: /\n:scheme: https\n:authority: www.facebook.com\naccept: */*\naccept-encoding: gzip, deflate\nuser-agent: nghttp2/1.28.0"
//
//	http2.Frame()
//
//	//req := fmt.Sprintf(
//	//	"HEADERS\n"+
//	//		"  END_STREAM\n"+
//	//		"  END_HEADERS\n"+
//	//		"  :method = GET\n"+
//	//		"  :scheme = https\n"+
//	//		"  :authority = %s\n"+
//	//		"  :path = /\r\n"+
//	//		"  host = %s\n"+
//	//		"  accept = text,html", host, host)
//
//	_, err2 := fmt.Fprintf(conn, initReq)
//	CheckError(err2)
//
//	data := make([]byte, 1000)
//
//	_, err = conn.Read(data)
//	CheckError(err)
//
//	_, err = conn.Read(data)
//	CheckError(err)
//
//	//status, err := bufio.NewReader(conn).ReadString('\n')
//
//	fmt.Println(string(data))
//}

func DialTcpHttp() {
	host := "example.com:80"
	conn, err := net.Dial("tcp", host)
	CheckError(err)

	_, err2 := fmt.Fprintf(conn, "GET / HTTP/1.1\r\nHost: %s\r\n\r\n", host)
	CheckError(err2)

	data := make([]byte, 1000)

	_, err = conn.Read(data)
	CheckError(err)

	//status, err := bufio.NewReader(conn).ReadString('\n')

	fmt.Println(string(data))
}

func CheckError(err error) {

	if err != nil {
		log.Fatal(err)
	}
}
