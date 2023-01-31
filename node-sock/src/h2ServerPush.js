const fs = require("fs");
const http2 = require("http2");
const PORT = 8443;

//Create a HTTP/2 server with HTTPS certificate and key
const server = http2.createSecureServer({
  cert: fs.readFileSync("server.crt"),
  key: fs.readFileSync("server.key"),
});

//Handle any incoming streams
server.on("stream", (stream, headers) => {
  //Check if the incoming stream supports push at the connection level
  if (stream.session.remoteSettings.enablePush) {
    //If it supports push, push the CSS file
    console.log("Push enabled. Pushing CSS file");
    //Open the File for reading
    const cssFile = fs.openSync("/www/htdocs/assets/css/common.css", "r");
    //Get some stats on the file for the HTTP response headers
    const cssStat = fs.fstatSync(cssFile);
    const cssRespHeaders = {
      "content-length": cssStat.size,
      "last-modified": cssStat.mtime.toUTCString(),
      "content-type": "text/css",
    };
    //Send a Push Promise stream for the file
    stream.pushStream(
      { ":path": "/assets/css/common.css" },
      (err, pushStream, headers) => {
        //Push the file in the newly created pushStream
        pushStream.respondWithFD(cssFile, cssRespHeaders);
      }
    );
  } else {
    //If push is disabled, log that
    console.log("Push disabled.");
  }
  //Respond to the original request
  stream.respond({
    "content-type": "text/html",
    ":status": 200,
  });
  stream.write("<DOCTYPE html><html><head>");
  stream.write(
    '<link rel="stylesheet" type="text/css" media="all" ref="/assets/css/common.css">'
  );
  stream.write("</head><body><h1>Test</h1></body></html>");

  //Start the server listening for requests on the given port
  server.listen(PORT);
  console.log(`Server listening on ${PORT}`);
});
