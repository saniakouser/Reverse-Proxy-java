// Tiny Node.js backend used only to test the Java proxy locally.
const http = require("http");

const port = Number(process.env.PORT || 8081);
const name = process.env.NAME || "backend-A";

http.createServer((req, res) => {
  const chunks = [];
  req.on("data", chunk => chunks.push(chunk));
  req.on("end", () => {
    res.writeHead(200, { "Content-Type": "application/json" });
    res.end(JSON.stringify({
      backend: name,
      method: req.method,
      path: req.url,
      body: Buffer.concat(chunks).toString()
    }));
  });
}).listen(port, () => console.log(`${name} listening on ${port}`));
