import socket


if __name__ == '__main__':
    print(">> initiating...")
    addr = ("localhost", 7074)
    if socket.has_dualstack_ipv6():
        s = socket.create_server(addr, family=socket.AF_INET6, dualstack_ipv6=True)
    else:
        server = socket.create_server(addr)
        print(server)
        socket.getaddrinfo("example.org", 80, proto=socket.IPPROTO_TCP)
    
