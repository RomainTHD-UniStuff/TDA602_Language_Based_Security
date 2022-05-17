const express = require("express");
const bodyParser = require("body-parser");
const path = require("path");
const innocent = require("innocent");

const app = express();
const port = 3001;

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));
app.use(express.static(path.join(__dirname, "public")));

app.use((req, res, next) => {
    res.setHeader("Access-Control-Allow-Origin", "*");
    res.setHeader(
        "Access-Control-Allow-Methods",
        "GET,POST,OPTIONS,PUT,PATCH,DELETE",
    );
    next();
});

app.post("/", (req, res) => {
    const {left, op, right} = req.body;
    const result = innocent.calc(left, op, right);
    res.send({result});
});

app.listen(port, () => {
    console.info(`Server listening on port ${port}`);
});
