var express = require('express');
const crypto = require('crypto');
var router = express.Router();
// Start server with "$env:DEBUG='myapp:*'; npm start" if running with VS Code

var mysql = require('mysql');
const { map } = require('../app');

var connection = mysql.createConnection({
  host: 'localhost',
  user: 'app',
  password: 'appPassword',
  database: 'rolepay'
})

function handleDisconnect(connection) {
  connection.on('error', function (err) {
    //   if (!err.fatal) {
    //     return;
    //   }

    if (err.code !== 'PROTOCOL_CONNECTION_LOST') {
      next(err);
    }

    console.log('Re-connecting lost connection: ' + err.stack);

    connection = mysql.createConnection(connection.config);
    handleDisconnect(connection);
    connection.connect();
  });
}

handleDisconnect(connection);
connection.connect();

function next(err) {
  console.log(err)
}

/* GET database test */
router.get('/', function (req, res) {
  res.send('Database connection works')
})
// ------------- ENVIRONMENT ----------------
/* GET environment name */
router.get('/environment/name/', function (req, res) {

  connection.query('SELECT name FROM environment WHERE environment_id=' + req.query.id, function (err, result) {
    if (err) next(err)
    res.send(result[0].name)
  })
})
/* GET environment users (without admin) */
router.get('/environment/users/', function (req, res) {
  connection.query('SELECT * FROM user WHERE environment_id=' + req.query.id + ' AND NOT admin=1', function (err, result1) {
    if (err) next(err)
    // Fetch balances
    result1.forEach((user, index) => {
      if (user.balance_id)
    connection.query('SELECT value FROM balance WHERE balance_id=' + user.balance_id, function (err, result2) {
      if (err) next(err)
      user.balance = result2[0].value
      if (index == result1.length-1)
      res.send(result1)
    })
    else if (index == result1.length-1)
    res.send(result1)
  })
  })
})

/* ADD environment, returns id */
router.get('/environment/add/', function (req, res) {
  var environmentId, userId
  const privateToken = crypto.randomBytes(2).toString('hex')
  connection.query('INSERT INTO environment (name) VALUES ("' + crypto.randomBytes(5).toString('hex') +
    '")', function (err, result) {
      if (err) next(err)
      connection.query('SELECT environment_id FROM environment ORDER BY environment_id DESC LIMIT 1', function (err, result) {
        if (err) next(err)
        environmentId = result[0].environment_id
        connection.query('INSERT INTO user (private_token, admin, environment_id) VALUES ("'
          + privateToken + '", 1, ' + environmentId + ')', function (err, result) {
            if (err) next(err)
            connection.query('SELECT user_id FROM user ORDER BY user_id DESC LIMIT 1', function (err, result) {
              if (err) next(err)
              userId = result[0].user_id
              res.send([{ "privateToken": privateToken, "environmentId": environmentId, "userId": userId }])
            })
          })
      })
    })
})

/* UPDATE environment */
router.post('/environment/update/', function (req, res) {
  connection.query('UPDATE environment SET name="' + req.body.name + '" WHERE environment_id=' + req.body.id, function (err, result) {
    if (err) next(err)
    res.send(result)
  })
})
/* REMOVE environment */
router.get('/environment/remove/', function (req, res) {
  connection.query('DELETE FROM environment WHERE environment_id=' + req.query.id +
    '; DELETE FROM user WHERE environment_id=' + req.query.id, function (err, result) {
      if (err) next(err)
      res.send(result)
    })
})
// ------------- USER ------------------
/* GET user */
router.get('/user/', function (req, res) {
  connection.query('SELECT * FROM user WHERE private_token="' + req.query.token + '" OR public_token="' + req.query.publictoken + '"', function (err, result) {
    if (err) next(err)
    console.log(req.query.token)
    res.send(result)
  })
})

/* ADD user */
router.post('/user/add/', function (req, res) {
  var balance, public, private, admin
  private = crypto.randomBytes(2).toString('hex');
  // Admin does not need balance or public id
  if (req.body.admin == 1) {
    balance = ""
    public = ""
    admin = 1
  }
  else {
    admin = 0
    // Add balance for new user
    connection.query('INSERT INTO balance (value) VALUES (0)', function (err, result) {
      if (err) next(err)
      connection.query('SELECT balance_id FROM balance ORDER BY balance_id DESC LIMIT 1', function (err, result) {
        if (err) next(err)
        balance = result[0].balance_id
        public = crypto.randomBytes(2).toString('hex');
        connection.query('INSERT INTO user (public_token, private_token, admin, balance_id, environment_id) VALUES ("'
          + public + '", "' + private + '", ' + admin + ', ' + balance + ', ' + req.body.environment + ')', function (err, result) {
            if (err) next(err)
            res.send(result)
          })
      })
    })

  }

})
/* UPDATE user */
router.post('/user/update/', function (req, res) {
  connection.query('UPDATE user SET public_token="' + req.body.public + '" WHERE user_id=' + req.body.id, function (err, result) {
    if (err) next(err)
    if (req.body.balance)
      connection.query('UPDATE balance SET value=' + req.body.balance + ' WHERE balance_id=' + req.body.balanceId, function (err, result) {
        if (err) next(err)
        res.send(result)
      })
  })
})
/* REMOVE user */
router.get('/user/remove/', function (req, res) {
  connection.query('DELETE FROM transaction WHERE receiver=' + req.query.id + ' OR sender=' + req.query.id, function (err, result) {
    if (err) next(err)
    connection.query('DELETE FROM balance WHERE balance_id=' + req.query.balanceId, function (err, result) {
      if (err) next(err)
      connection.query('DELETE FROM user WHERE user_id=' + req.query.id, function (err, result) {
        if (err) next(err)
        res.send(result)
      })
    })
  })
})
// ----------------- TRANSACTIONS ------------
/* GET transaction by id */
router.get('/transaction/', function (req, res) {
  connection.query('SELECT * FROM transaction WHERE transaction_id=' + req.query.id, function (err, result) {
    if (err) next(err)
    res.send(result)
  })
})

/* GET user transactions */
router.get('/user/transaction/', function (req, res) {
  connection.query('SELECT * FROM transaction WHERE sender=' + req.query.id + " OR receiver=" + req.query.id, function (err, result) {
    if (err) next(err)
    eventArray = result
    tokenList = new Map()
    // Fetch public tokens for other users
    eventArray.forEach((event, index) => {
      var searchable
      (event.sender == req.query.id) ? searchable = event.receiver : searchable = event.sender
      if (!tokenList.has(searchable)) {
        connection.query('SELECT public_token FROM user WHERE user_id=' + searchable, function (err, result) {
          if (err) next(err)
          tokenList.set(searchable, result[0].public_token)
          // Rewrite id into public token
          event.publicToken = tokenList.get(searchable)
          if (index == eventArray.length-1)
            res.send(eventArray)
        })
      }
      else {
        // Rewrite id into public token
        event.publicToken = tokenList.get(searchable)
        if (index == eventArray.length-1)
            res.send(eventArray)
      }
    }); 
    
  })
})
/* ADD a new transaction */
router.post('/transaction/add', function (req, res) {
  //TODO: Add error checking
  // Add balance for new user
  var senderBalance, receiverBalance, receiverId
  connection.query('SELECT balance_id FROM USER WHERE user_id=' + req.body.sender, function (err, result) {
    if (err) next(err)
    senderBalance = result[0].balance_id

    connection.query('SELECT balance_id, user_id FROM USER WHERE public_token="' + req.body.receiver + '"', function (err, result) {
      if (err) next(err)
      receiverBalance = result[0].balance_id
      receiverId = result[0].user_id

      connection.query('SELECT 1 FROM BALANCE WHERE balance_id=' + senderBalance, function (err, result) {
        if (err) next(err)
        if (result < req.body.amount) {
          res.status(500)
          res.send("Error: Not enough balance for payment")
        }
        else {
          connection.query('UPDATE BALANCE SET value=value-' + req.body.amount + ' WHERE balance_id=' + senderBalance, function (err, result) {
            if (err) next(err)
            connection.query('UPDATE BALANCE SET value=value-' + req.body.amount + ' WHERE balance_id=' + receiverBalance, function (err, result) {
              if (err) next(err)
              connection.query('INSERT INTO transaction (timestamp, amount, sender, receiver) VALUES (NOW(), ' + req.body.amount + ', ' + req.body.sender + ', ' + receiverId + ')', function (err, result) {
                if (err) next(err)
                res.send("Query Success!")
              })
            })
          })
        }
      })
    })
  })
})

// ---------------- BALANCE ---------------------
/* GET balance */
router.get('/balance/', function (req, res) {
  connection.query('SELECT value FROM balance WHERE balance_id=' + req.query.id, function (err, result) {
    if (err) next(err)
    console.log(result[0].value)
    res.status(200)
    res.send(result[0].value.toString())
  })
})
module.exports = router;
