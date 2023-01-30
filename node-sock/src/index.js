const http = require("http");

const PORT = 4000;

const requestHandler = (req, resp) => {
  console.log(req.url);
  resp.setHeader("Link", "</assets/css/common.css>;rel=preload");
  resp.writeHead(200, { "Content-Type": "text/html" });
  resp.write("<!DOCTYPE html>\n");
  resp.write("<html>\n");
  resp.write("<head>\n");
  resp.write(
    '<link rel="stylesheet" type="text/css" href="/assets/css/common.css">\n'
  );
  resp.write("</head>\n");
  resp.write("<body>\n");
  resp.write("<h1>Test</h1>\n");
  resp.write("</body>\n");
  resp.write("</html>\n");
  resp.end();
};

const server = http.createServer(requestHandler);
server.listen(PORT);

console.log(`Server is listening on ${PORT}`);
