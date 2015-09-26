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

if(process.argv[2] === 'cobertura'){
    matches[1] = matches[1] + ' * 100';
    matches[2] = matches[2] + ' * 100';
} else {
    matches[1] = '100 - ((' + matches[1].replace('of', '/').replace(/,/g,'') + ') * 100)';
    matches[2] = '100 - ((' + matches[2].replace('of', '/').replace(/,/g,'') + ') * 100)';
}

var lineCov = toDecimal(eval(matches[1]), 1);
var branchCov = toDecimal(eval(matches[2]), 1);

console.log('Line Coverage: ' + lineCov + '%\t\t' + 'Branch Coverage: ' + branchCov + '%');

function toDecimal(num, pre) {
    return (+(Math.round(+(num + 'e' + pre)) + 'e' + -pre)).toFixed(pre);
}