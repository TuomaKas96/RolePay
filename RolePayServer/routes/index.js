var express = require('express');
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
  connection.connect(function(err) {
    if (err) throw err
    res.send('Database connection works')
    connection.end();
  })
  
})
/* GET user */
router.get('/user/:id', function (req, res) {
  connection.connect(function(err) {
  if (err) throw err
  connection.query('SELECT 1 FROM user WHERE user_id=' + req.params.id, function(err, result) {
    if (err) throw err
    res.send('Data for user id ' + req.params.id + ': ' + result[0])
    })
    connection.end();
  })
  
})
module.exports = router;
