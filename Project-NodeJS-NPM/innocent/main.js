const advancedFunctions = require("vulnerable");

function calc(left, op, right) {
    switch (op) {
        case "+":
            return left + right;
        case "-":
            return left - right;
        case "*":
            return left * right;
        case "/":
            return left / right;
        default:
            return advancedFunctions.getFunction(op)(left, right);
    }
}

module.exports = {
    calc,
};
