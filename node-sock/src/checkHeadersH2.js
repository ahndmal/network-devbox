const http = require("http");

const port = 3000;

const requestHandler = (request, response) => {
  const { headers } = request;

  console.log("HTTP Version: " + headers["protocol"]);
  console.log("HTTP2 Support: " + headers["http2"]);
  console.log("HTTP2 Push Support: " + headers["h2push"]);

  response.setHeader("Link", "</assets/css/common.css>;rel=preload");
  response.writeHead(200, { "Content-Type": "text/html" });

  response.write("<!DOCTYPE html>\n");
  response.write("<html>\n");
  response.write("<head>\n");
  response.write(
    '<link rel="stylesheet" type="text/css" href="/assets/css/common.css">\n'
  );
  response.write("</head>\n");
  response.write("<body>\n");
  response.write("<h1>Test</h1>\n");
  response.write("</body>\n");
  response.write("</html>\n");
  response.end();
};

const server = http.createServer(requestHandler);

server.listen(port);
console.log("Server is listening on " + port);
