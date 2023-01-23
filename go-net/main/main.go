package main

import (
	"fmt"
	"github.com/gorilla/websocket"
	_ "github.com/jackc/pgx"
	_ "github.com/lib/pq"
	"log"
	"net/http"
	"time"
)

func main() {
	start := time.Now()
	fmt.Println(fmt.Sprintf("START %s", start.String()))
	fmt.Println("========")

	var upgrader = websocket.Upgrader{
		ReadBufferSize:  1024,
		WriteBufferSize: 1024,
	}

	http.HandleFunc("/echo", func(writer http.ResponseWriter, req *http.Request) {
		conn, err := upgrader.Upgrade(writer, req, nil) // error ignored for sake of simplicity
		if err != nil {
			log.Println(err)
		}

		for {
			msgType, msg, err := conn.ReadMessage()
			if err != nil {
				log.Println(err)
			}
			fmt.Printf("%s sent: %s\n", conn.RemoteAddr(), string(msg))
			if err = conn.WriteMessage(msgType, msg); err != nil {
				log.Println(err)

			}

		}
	})

	http.ListenAndServe(":7070", nil)

	// === end
	fmt.Println(fmt.Sprintf("Script took %f seconds", time.Now().Sub(start).Seconds()))
	fmt.Println("========")
}
