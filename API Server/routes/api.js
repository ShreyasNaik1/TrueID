var express = require('express')
var router = express.Router()
var extend = require('xtend')
var mongoose = require('mongoose')
var request = require('superagent')

/* config vars */
var dburl = 'mongodb://localhost/trueid'
var apikey = '<firebase>'//

/* Simple Schema */
var modelSchema = mongoose.Schema({
  mobile_id: String,
  registration_token: String
})
var streamSchema = mongoose.Schema({
  mobile_id: String,
  domain_name: String,
  username: String,
  password: String,
  timestamp: Date
})
var smsSchema = mongoose.Schema({
  mobile_id: String,
  secret_key: Number,
  timestamp: Date
})
var model = mongoose.model('Model', modelSchema)
var stream = mongoose.model('Stream', streamSchema)
var sms = mongoose.model('Sms', smsSchema)

/* Retrieves the data based on user id and domain */
router.get('/', function (req, res, next) {
  var _data = extend(req.query, req.body)
  if (!_data.domain_name) {
    res.send({'error': 'NoDomainName'})
    return false
  }
  var query = {mobile_id: _data.mobile_id}
  mongoose.connect(dburl)
  mongoose.connection
    .on('error', console.error.bind(console, 'connection error:'))
    .once('open', function () {
      model.findOne(query, function (err, result) {
        if (err) {
          mongoose.disconnect()
          res.send({'error': err})
          return false
        }
	console.log(result);
        // We then do a push notification here
        if (result) {
          request
            .post('https://fcm.googleapis.com/fcm/send')
            .set('Authorization', 'key=' + apikey)
            .set('Content-Type', 'application/json')
            .send({
              'to': result.registration_token,
              'notification': {
                'body': _data.domain_name,
                'title': 'Approve Login'
              },
              'data': {
                'message': _data.domain_name
              }
            })
            .end(function (err, response) {
		console.log(response);
              // var m_id = JSON.parse(response.text).results[0].message_id
              if (err) {
                mongoose.disconnect()
                res.send({'error': err})
                return false
              }
              mongoose.disconnect()
              res.send({'success': 'success'})
              return false
            })
        }else {
          mongoose.disconnect()
          res.send({'error': 'WrongParamsError'})
          return false
        }
      })
    })
})

/* Registers the phone id and app token */
router.post('/', function (req, res, next) {
  var _data = extend(req.query, req.body)
  // malformed json body
  // _data=JSON.parse(Object.keys(_data)[0])
  var query = {mobile_id: _data.mobile_id}
  var data = {
    mobile_id: _data.mobile_id,
    registration_token: _data.token,
    is_authenticated: false
  }
  var options = { upsert: true, new: true }
  mongoose.connect(dburl)
  mongoose.connection
    .on('error', console.error.bind(console, 'connection error:'))
    .once('open', function () {
      model.findOneAndUpdate(query, data, options, function (err, _result) {
        if (err) {
          mongoose.disconnect()
          res.send({'error': err})
          return false
        }
        mongoose.disconnect()
        res.send({'success': _result})
        return false
      })
    })
})

/* Deletes the phone id*/
router.delete('/', function (req, res, next) {
  var _data = extend(req.query, req.body)
  var query = {mobile_id: _data.mobile_id}
  mongoose.connect(dburl)
  mongoose.connection
    .on('error', console.error.bind(console, 'connection error:'))
    .once('open', function () {
      model.remove(query, function (err, chapter) {
        if (err) {
          mongoose.disconnect()
          res.send({'error': err})
          return false
        }
        mongoose.disconnect()
        res.send({'success': chapter})
        return false
      })
    })
})

router.get('/message', function (req, res, next) {
  var _data = extend(req.query, req.body)
  var query = {
    mobile_id: _data.mobile_id,
    domain_name: _data.domain_name
  }
  var result = {}
  mongoose.connect(dburl)
  mongoose.connection
    .on('error', console.error.bind(console, 'connection error:'))
    .once('open', function () {
      stream.findOne(query, function (err, result) {
        if (err) {
          mongoose.disconnect()
          res.send({'error': err})
          return false
        }
        if (result) {
          result = {username: result.username, password: result.password}
          stream.remove(query, function (err, s) {
            if (err) {
              mongoose.disconnect()
              res.send({'error': err})
              return false
            }
            mongoose.disconnect()
            res.send(result)
            return false
          })
        }else {
          mongoose.disconnect()
          res.send({'error': 'NoMessageFound'})
          return false
        }
      })
    })
})

/* Registers a message */
router.post('/message', function (req, res, next) {
  var _data = extend(req.query, req.body)
  var data = {
    mobile_id: _data.mobile_id,
    domain_name: _data.domain_name,
    username: _data.username,
    password: _data.password,
    timestamp: new Date().toISOString()
  }
  mongoose.connect(dburl)
  mongoose.connection
    .on('error', console.error.bind(console, 'connection error:'))
    .once('open', function () {
      var _s = new stream(data)
      _s.save(function (err, s) {
        if (err) {
          mongoose.disconnect()
          res.send({'error': err})
          return false
        }
        mongoose.disconnect()
        res.send({'success': s})
        return false
      })
    })
})

/* Sends an sms */
/* This should be separated from GET*/
router.post('/sms', function(req,res,next){
  var _data = extend(req.query, req.body)
  if(_data && !_data.secret_key){
    var secret_key=Math.floor(Math.random() * 90000) + 10000
    var data ={
      email:  "<smsgateway>",
      password: "<smsgateway>",
      device: "<smsgateway>",
      number: _data.mobile_id,
      message: "Your TrueID authentication number is "+secret_key
    }
    request
      .post('https://smsgateway.me/api/v3/messages/send')
      .set('Content-Type', 'application/json')
      .send(data)
      .end(function (err, response) {
        // var m_id = JSON.parse(response.text).results[0].message_id
        if (err) {
          res.send({'error': err})
          return false
        }
        console.log("sms sent")
        mongoose.connect(dburl)
        mongoose.connection
          .on('error', console.error.bind(console, 'connection error:'))
          .once('open', function () {
            var query={mobile_id:_data.mobile_id}
            var vdata={
              mobile_id: data.number,
              secret_key: secret_key,
              timestamp: new Date().toISOString()
            }
            console.log(vdata)
            var options = { upsert: true, new: true }
            sms.findOneAndUpdate(query, vdata, options, function (err, _result) {
              if (err) {
                mongoose.disconnect()
                res.send({'error': err})
                return false
              }
              mongoose.disconnect()
              res.send({'success': _result})
              return false
            })
          })
      })
    }else if(_data && _data.secret_key){
      var data={};
      mongoose.connect(dburl)
      mongoose.connection
        .on('error', console.error.bind(console, 'connection error:'))
        .once('open', function () {
          sms.findOne({mobile_id:_data.mobile_id}, function(err, _result){
            if (err) {
              mongoose.disconnect()
              res.send({'error': err})
              return false
            }
            if (!_result) {
              mongoose.disconnect()
              res.send({'error': "Nothing found"})
              return false
            }    
            console.log(_result.secret_key,_data.secret_key)
            if (_result.secret_key!=_data.secret_key) {
              mongoose.disconnect()
              res.send({'success': {'matched':false}})
              return false
            }  
            sms.remove({mobile_id:_data.mobile_id},function(err,_result){
              if (err) {
                mongoose.disconnect()
                res.send({'error': err})
                return false
              }
              mongoose.disconnect()
              res.send({'success': {'matched':true}})
              return false
            })    
          })
        })
    }
})



module.exports = router
