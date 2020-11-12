var express = require('express');
const crypto = require('crypto');
var router = express.Router();
// Start server with "$env:DEBUG='myapp:*'; npm start" if running with VS Code

var mysql = require('mysql')

var connection = mysql.createConnection({
  host: 'localhost',
  user: 'app',
  password: 'appPassword',
  database: 'rolepay'
})

/* GET database test */
router.get('/', function (req, res) {
  connection.connect(function (err) {
    if (err) throw err
    res.send('Database connection works')
    connection.end();
  })
})
// ------------- ENVIRONMENT ----------------
/* GET environment name */
router.get('/environment/name/:id', function (req, res) {
  connection.connect(function (err) {
    if (err) throw err
    connection.query('SELECT name FROM environment WHERE environment_id=' + req.params.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
    connection.end();
  })
})
/* GET environment users (without admin) */
router.get('/environment/users/:id', function (req, res) {
  connection.connect(function (err) {
    if (err) throw err
    connection.query('SELECT * FROM user WHERE environment_id=' + req.params.id+ ' AND NOT admin=1', function (err, result) {
      if (err) throw err
      res.send(result)
    })
    connection.end();
  })
})

/* ADD environment, returns id */
router.post('/environment/add', function (req, res) {
  connection.connect(function (err) {
    if (err) throw err
    connection.query('INSERT INTO environment (name) VALUES (' + crypto.randomBytes(5).toString('hex') + 
    '); SELECT environment_id FROM environment ORDER BY environment_id DESC LIMIT 1', function (err, result) {
      if (err) throw err
      res.send(result)
    })
    connection.end();
  })
})

/* UPDATE environment */
router.post('/environment/update/:id/:name', function (req, res) {
  connection.connect(function (err) {
    if (err) throw err
    connection.query('UPDATE environment SET name=' + res.params.name + ' WHERE environment_id=' + res.params.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
    connection.end();
  })
})
/* REMOVE environment */
router.get('/environment/remove/:id', function (req, res) {
  connection.connect(function (err) {
    if (err) throw err
    connection.query('DELETE FROM environment WHERE environment_id=' + req.params.id +
    '; DELETE FROM user WHERE environment_id='+ req.params.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
    connection.end();
  })
})
// ------------- USER ------------------
/* GET user */
router.get('/user/:id', function (req, res) {
  connection.connect(function (err) {
    if (err) throw err
    connection.query('SELECT 1 FROM user WHERE user_id=' + req.params.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
    connection.end();
  })
})

/* ADD user */
router.post('/user/add/:environment/:admin', function (req, res) {
  connection.connect(function (err) {
    if (err) throw err
    var balance, public, private
    private = crypto.randomBytes(5).toString('hex');
    // Admin does not need balance or public id
    if (admin == undefined || admin == 0){
      balance = ""
      public = ""
    }
    else {
      // Add balance for new user
      connection.query('INSERT INTO balance (value) VALUES (0); SELECT balance_id FROM balance ORDER BY balance_id DESC LIMIT 1', function (err, result) {
      if (err) throw err
      balance = result[0]
      public = crypto.randomBytes(5).toString('hex');
    })
    }
    connection.query('INSERT INTO user (public_token, private_token, admin, balance_id, environment_id) VALUES ("'
    + public + '", "' + private + ', ' + req.params.admin + ', ' + balance + ', ' + req.params.environment + ')', function (err, result) {
      if (err) throw err
      res.send(result)
    })
    connection.end();
  })
})
/* UPDATE user */
router.post('/user/update/:id/:public/:balance', function (req, res) {
  connection.connect(function (err) {
    if (err) throw err
    connection.query('UPDATE user SET public_token=' + req.params.public + ' WHERE user_id=' + req.params.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
    if (req.params.balance)
    connection.query('UPDATE balance SET value=' + req.params.balance + ' WHERE user_id=' + req.params.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
    connection.end();
  })
})
/* REMOVE user */
router.get('/user/remove/:id', function (req, res) {
  connection.connect(function (err) {
    if (err) throw err
    connection.query('DELETE FROM user WHERE user_id=' + req.params.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
    connection.end();
  })
})
// ----------------- TRANSACTIONS ------------
/* GET transaction by id */
router.get('/transaction/:id', function (req, res) {
  connection.connect(function (err) {
    if (err) throw err
    connection.query('SELECT 1 FROM transaction WHERE transaction_id=' + req.params.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
    connection.end();
  })
})
/* GET user transactions */
router.get('/user/transaction/:id', function (req, res) {
  connection.connect(function (err) {
    if (err) throw err
    connection.query('SELECT * FROM transaction WHERE sender=' + req.params.id + " OR receiver=" + req.params.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
    connection.end();
  })
})
/* ADD a new transaction */
router.post('/transaction/add/:amount/:sender/:receiver', function (req, res) {
  connection.connect(function (err) {
    if (err) throw err
    connection.query('INSERT INTO transaction (timestamp, amount, sender, receiver) VALUES (NOW(), ' 
    + req.params.amount, + ', ' + req.params.sender, + ', ' + req.params.receiver, + ')', function (err, result) {
      if (err) throw err
      res.send(result)
    })
    connection.end();
  })
})

module.exports = router;
