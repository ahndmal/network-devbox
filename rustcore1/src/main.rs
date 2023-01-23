use std::net::{TcpListener, TcpStream};
use std::io::Result;
use std::io::Write;
use std::io::prelude::*;


fn init_sockets() -> Result<()> {
    let listener = TcpListener::bind("127.0.0.1:6565")?;
    loop {
        match listener.accept() {
            Ok((mut sock, addr)) => {
                sock.write(&[1])?;
                sock.read(&mut [0; 128])?;
                println!("new client: {addr:?}");
                
            },
            Err(e) => println!("couldn't get client: {e:?}"),
        }
    }

    Ok(())

}


fn main() -> std::io::Result<()> {
    
    //let mut stream = TcpStream::connect("127.0.0.1:7070")?;
    //stream.write(&[1])?;
    //stream.read(&mut [0; 128])?;

    init_sockets();

    Ok(())
}
