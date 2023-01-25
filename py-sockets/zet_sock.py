import socket


def get_ip():
    ip = socket.gethostbyname("example.com")
    print(ip)


if __name__ == '__main__':
    with socket.socket() as sock:
        #host = "time.nist.gov"
        #port = 13
        host = 'localhost'
        port = 8001
        
        addr = (host, port)

        sock.bind(addr)
        print(f'socket binded to {port}')

        sock.listen()

        conn, addr = sock.accept()

        with conn:
            while True:
                
                conn.sendall(b"First message")

                data = conn.recv(1024)
                
                if not data:
                    print('Error when receiving data from socket')
                    break

                conn.sendall(data)

        #message = b'Hello from python!'
        #sock.connect(addr)
       
        #sock.sendall(message)

        #read_data = sock.recv(4096)

        #data, addr = sock.recvfrom(1024)
        #print(str(read_data, 'utf-8'))

        #print(data.decode())
