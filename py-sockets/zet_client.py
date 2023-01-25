import socket

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:

    host = "localhost"
    port = 8001

    s.connect((host, port))
    s.sendall(b'hello there')
    print(str(s.recv(4096), 'utf-8'))
