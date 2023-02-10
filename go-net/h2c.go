package my_lib

import (
	"crypto/tls"
	"fmt"
	"golang.org/x/net/http2"
	"golang.org/x/net/http2/h2c"
	"net"
	"net/http"
)

func client2() {
	fmt.Println("s1")

	client := &http.Client{}
	client.Transport = &http2.Transport{
		AllowHTTP: true,
		DialTLS: func(netw, addr string, cfg *tls.Config) (net.Conn, error) {
			return net.Dial(netw, addr)
		}}

	resp, err := client.Get("http://localhost:8080/anc")
	fmt.Println(err)
	fmt.Println(resp)
}

func main2() {

	handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprint(w, "Hello world123")
	})
	h2s := &http2.Server{
		// ...
	}
	h1s := &http.Server{
		Addr:    ":8080",
		Handler: h2c.NewHandler(handler, h2s),
	}

	go h1s.ListenAndServe()

	MyClient()
}
