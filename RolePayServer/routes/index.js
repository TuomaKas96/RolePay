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

function handleDisconnect(connection) {
  connection.on('error', function(err) {
  //   if (!err.fatal) {
  //     return;
  //   }

    if (err.code !== 'PROTOCOL_CONNECTION_LOST') {
      throw err;
    }

    console.log('Re-connecting lost connection: ' + err.stack);

    connection = mysql.createConnection(connection.config);
    handleDisconnect(connection);
    connection.connect();
  });
}

handleDisconnect(connection);
connection.connect();

/* GET database test */
router.get('/', function (req, res) {
    res.send('Database connection works')
})
// ------------- ENVIRONMENT ----------------
/* GET environment name */
router.get('/environment/name/', function (req, res) {

    connection.query('SELECT name FROM environment WHERE environment_id=' + req.query.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
})
/* GET environment users (without admin) */
router.get('/environment/users/', function (req, res) {
    connection.query('SELECT * FROM user WHERE environment_id=' + req.query.id + ' AND NOT admin=1', function (err, result) {
      if (err) throw err
      res.send(result)
    })
})

/* ADD environment, returns id */
router.post('/environment/add/', function (req, res) {
    connection.query('INSERT INTO environment (name) VALUES (' + crypto.randomBytes(5).toString('hex') + 
    '); SELECT environment_id FROM environment ORDER BY environment_id DESC LIMIT 1', function (err, result) {
      if (err) throw err
      res.send(result)
    })
})

/* UPDATE environment */
router.post('/environment/update/', function (req, res) {
    connection.query('UPDATE environment SET name="' + req.body.name + '" WHERE environment_id=' + req.body.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
})
/* REMOVE environment */
router.get('/environment/remove/', function (req, res) {
    connection.query('DELETE FROM environment WHERE environment_id=' + req.query.id +
    '; DELETE FROM user WHERE environment_id='+ req.query.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
})
// ------------- USER ------------------
/* GET user */
router.get('/user/', function (req, res) {
    connection.query('SELECT 1 FROM user WHERE private_token=' + req.query.token, function (err, result) {
      if (err) throw err
      res.send(result)
    })
})

/* ADD user */
router.post('/user/add/', function (req, res) {
    var balance, public, private, admin
    private = crypto.randomBytes(5).toString('hex');
    // Admin does not need balance or public id
    if (req.body.admin == undefined || req.body.admin == 0){
      balance = ""
      public = ""
      admin = 0
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
    + public + '", "' + private + '", ' + admin + ', ' + balance + ', ' + req.body.environment + ')', function (err, result) {
      if (err) throw err
      res.send(result)
    })
})
/* UPDATE user */
router.post('/user/update/', function (req, res) {
    connection.query('UPDATE user SET public_token="' + req.body.public + '" WHERE user_id=' + req.body.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
    if (req.body.balance)
    connection.query('UPDATE balance SET value=' + req.body.balance + ' WHERE user_id=' + req.body.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
})
/* REMOVE user */
router.get('/user/remove/', function (req, res) {
    connection.query('DELETE FROM user WHERE user_id=' + req.query.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
})
// ----------------- TRANSACTIONS ------------
/* GET transaction by id */
router.get('/transaction/', function (req, res) {
    connection.query('SELECT 1 FROM transaction WHERE transaction_id=' + req.query.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
})
/* GET user transactions */
router.get('/user/transaction/', function (req, res) {
    connection.query('SELECT * FROM transaction WHERE sender=' + req.query.id + " OR receiver=" + req.query.id, function (err, result) {
      if (err) throw err
      res.send(result)
    })
})
/* ADD a new transaction */
router.post('/transaction/add/', function (req, res) {
    connection.query('INSERT INTO transaction (timestamp, amount, sender, receiver) VALUES (NOW(), ' 
    + req.body.amount, + ', "' + req.body.sender, + '", "' + req.body.receiver + '")', function (err, result) {
      if (err) throw err
      res.send(result)
    })
})

module.exports = router;
