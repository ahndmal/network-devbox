use std::net::{TcpListener, TcpStream};
use std::io::Result;
use std::io::Write;
use std::io::prelude::*;


fn init_sock_server() -> Result<()> {
    let listener = TcpListener::bind("127.0.0.1:8888")?;
    match listener.accept() {
            Ok((mut sock, addr)) => {
                let msg = "Hello from RUST!";
                sock.write(msg.as_bytes())?;
                sock.read(&mut [0; 128])?;
                println!("new client: {addr:?}");
                
            },
            Err(e) => println!("couldn't get client: {e:?}"),
    }

    Ok(())

}


fn main() -> std::io::Result<()> {
    //init_sockets();

    let mut stream = TcpStream::connect("127.0.0.1:8888")?;
    let msg = "Hello from RUST!";

    stream.write(msg.as_bytes())?;



    let mut data = [0; 128];
    stream.read( &mut data)?;
    println!("{}", String::from_utf8(data.to_vec()).unwrap());

    Ok(())
}
