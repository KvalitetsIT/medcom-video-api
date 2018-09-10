var http = require('http');

var server = http.createServer(function(req, res) {
	
  var sessionHeader = req.headers['SESSION'];

  res.setHeader('Content-Type', 'application/json');
  res.writeHead(200);
  if (sessionHeader) {
	  var decoded = new Buffer(sessionHeader, 'base64');
	  res.end(decoded);
  } else {
	  res.end("{}");
  }
});
server.listen(9200);