package my_lib

import (
	"fmt"
	"io/ioutil"
	"net"
)

func sendMail() {

	from := "john.doe@example.com"
	to := "root@core9"
	name := "John Doe"
	subject := "Hello"
	body := "Hello there"

	host := "core9:25"

	con, err := net.Dial("tcp", host)
	CheckError(err)

	req := "HELO core9\r\n" +
		"MAIL FROM: " + from + "\r\n" +
		"RCPT TO: " + to + "\r\n" +
		"DATA\r\n" +
		"From: " + name + "\r\n" +
		"Subject: " + subject + "\r\n" +
		body + "\r\n.\r\n" + "QUIT\r\n"

	_, err = con.Write([]byte(req))
	CheckError(err)

	res, err := ioutil.ReadAll(con)
	CheckError(err)

	fmt.Println(string(res))
}
