var express = require('express')
var path = require('path')
var logger = require('morgan')
var cookieParser = require('cookie-parser')
var bodyParser = require('body-parser')
var compress = require('compression')

var PATH_TO_CERT = '/root/im-ju.xyz.crt';
var PATH_TO_KEY = '/root/im-ju.xyz.d.key';
var https = require('https');
    var fs = require('fs');

    var options = {
      // ca: [fs.readFileSync(PATH_TO_BUNDLE_CERT_1), fs.readFileSync(PATH_TO_BUNDLE_CERT_2)],
      cert: fs.readFileSync(PATH_TO_CERT),
      key: fs.readFileSync(PATH_TO_KEY)
    };

var app = express()

// express app setup
app
  .set('views', path.join(__dirname, 'views'))
  .set('view engine', 'jade')
  .use(compress())
  .use(logger('dev'))
  .use(bodyParser.json())
  .use(bodyParser.urlencoded({ extended: false }))
  .use(cookieParser())
  .use(express.static(path.join(__dirname, 'public')))
  .use(function (req, res, next) {
    res.setHeader('Access-Control-Allow-Origin', '*')
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE')
    next()
  })

// routes to use for the application
app
  .use('/', require('./routes/index'))
  .use('/api', require('./routes/api'))

// catch 404 and forward to error handler
app.use(function (req, res, next) {
  var err = new Error('Not Found')
  err.status = 404
  next(err)
})

// production error handler
// no stacktraces leaked to user
app.use(function (err, req, res, next) {
  res.status(err.status || 500)
  res.render('error', {
    message: err.message,
    error: {}
  })
})

var server = https.createServer(options, app);

    server.listen(443, function(){
        console.log("server running at https://IP_ADDRESS:443/")
    });


module.exports = app
