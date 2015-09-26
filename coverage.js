var fs = require('fs');

var filename, matcher;

if(process.argv[2] === 'cobertura'){
    filename = 'target/site/cobertura/frame-summary.html';
    matcher = '<span class="text">(.*?)</span>';
} else {
    filename = 'target/site/jacoco/index.html';
    matcher = '<td class="bar">(.*?)</td>'
}

var html = fs.readFileSync(filename).toString();

var regex = new RegExp(matcher + '.*?' + matcher);
var matches = html.match(regex);

var format = process.argv[2] === 'cobertura'
    ? formatCobertura
    : formatJacoco;

var lineCov = toDecimal(format(matches[1]), 1);
var branchCov = toDecimal(format(matches[2]), 1);

console.log('Line Coverage: ' + lineCov + '%\t\t' + 'Branch Coverage: ' + branchCov + '%');

function formatCobertura(match){
    return eval(match + ' * 100');
}

function formatJacoco(match){
    return eval('100 - ((' + match.replace('of', '/').replace(/,/g,'') + ') * 100)');
}

function toDecimal(num, pre) {
    return (+(Math.round(+(num + 'e' + pre)) + 'e' + -pre)).toFixed(pre);
}