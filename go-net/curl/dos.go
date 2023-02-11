package curl

import (
	"bufio"
	"github.com/AndriiMaliuta/my_lib"
	"log"
	"net/http"
	"sync"
	"time"
)

func GetClient() *http.Client {
	client := http.Client{
		Timeout: 100 * time.Second,
	}
	return &client
}

func Doss(wg *sync.WaitGroup, url string) {
	get, err := GetClient().Get(url)
	my_lib.CheckError(err)

	data := make([]byte, 1000)
	bufio.NewReader(get.Body).Read(data)

	log.Println(string(data))
	wg.Done()

}
