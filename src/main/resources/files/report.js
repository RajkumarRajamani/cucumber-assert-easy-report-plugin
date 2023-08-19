var json = `

{
  "overAllExecutionStatus": "passed",
  "projectInfo": {
    "environment": "Development Region",
    "appName": "Google",
    "browser": "Microsoft Edge",
    "description": "We regularly use Google to search for CSS properties that we don't know or don't remember.When using Google, search what you're trying to accomplish.",
    "url": "https://www.google.com",
    "os" : "Mac OS X"
  },
  "featurePieChartDataMap": {
    "totalFeatures": 4,
    "passedWithKnownFailures": 1,
    "passed": 1,
    "failed": 2,
    "knownFailures": 0,
    "skipped": 0
  },
  "testCasePieChartDataMap": {
    "passedWithKnownFailures": 4,
    "passed": 6,
    "failed": 2,
    "knownFailures": 0,
    "totalCases": 12,
    "skipped": 0
  },
  "testStepPieChartDataMap": {
    "pending": 0,
    "totalSteps": 36,
    "unused": 0,
    "passed": 20,
    "failed": 2,
    "ambiguous": 0,
    "knownFailures": 4,
    "skipped": 10,
    "undefined": 0
  },
  "overallTestCaseStats": {
    "failPercent": "17 %",
    "failed": "2",
    "overAllStatus": "failed",
    "passedWithKnownFailPercent": "33 %",
    "overallDuration": "0h 0m 2s",
    "skipped": "0",
    "knownFailPercent": "0 %",
    "passedWithKnownFailures": "4",
    "passed": "6",
    "passPercent": "50 %",
    "knownFailures": "0",
    "skippedPercent": "0 %",
    "totalCases": "12"
  },
  "featuresStats": [
    {
      "failPercent": "0 %",
      "featureName": "Home Page All Pass",
      "failed": "0",
      "passedWithKnownFailPercent": "0 %",
      "skipped": "0",
      "duration": "0h 0m 0s",
      "knownFailPercent": "0 %",
      "passedWithKnownFailures": "0",
      "passed": "3",
      "passPercent": "100 %",
      "knownFailures": "0",
      "skippedPercent": "0 %",
      "totalCases": "3",
      "status": "passed"
    },
    {
      "failPercent": "0 %",
      "featureName": "Home Page pass with known fails",
      "failed": "0",
      "passedWithKnownFailPercent": "100 %",
      "skipped": "0",
      "duration": "0h 0m 1s",
      "knownFailPercent": "0 %",
      "passedWithKnownFailures": "3",
      "passed": "0",
      "passPercent": "0 %",
      "knownFailures": "0",
      "skippedPercent": "0 %",
      "totalCases": "3",
      "status": "passed with known failures"
    },
    {
      "failPercent": "33 %",
      "featureName": "Home Page pass with fails",
      "failed": "1",
      "passedWithKnownFailPercent": "0 %",
      "skipped": "0",
      "duration": "0h 0m 0s",
      "knownFailPercent": "0 %",
      "passedWithKnownFailures": "0",
      "passed": "2",
      "passPercent": "67 %",
      "knownFailures": "0",
      "skippedPercent": "0 %",
      "totalCases": "3",
      "status": "failed"
    },
    {
      "failPercent": "33 %",
      "featureName": "Home Page pass with fails 2",
      "failed": "1",
      "passedWithKnownFailPercent": "33 %",
      "skipped": "0",
      "duration": "0h 0m 0s",
      "knownFailPercent": "0 %",
      "passedWithKnownFailures": "1",
      "passed": "1",
      "passPercent": "33 %",
      "knownFailures": "0",
      "skippedPercent": "0 %",
      "totalCases": "3",
      "status": "failed"
    }
  ]
}

`;

// Read Json Data
var data = JSON.parse(json);

// populate overall status on top of the report
let overallStatusParentElement = document.querySelector(".over-all-highlight-section");
var overAllStatusInnerElement = " ";
var overallStatus = data.overAllExecutionStatus;
if (overallStatus == "passed") {
  overAllStatusInnerElement = `<div class="over-all-highlight" style="background-color:#008000;">${overallStatus}</div>`;
} else if (overallStatus == "failed") {
  overAllStatusInnerElement = `<div class="over-all-highlight" style="background-color:#e01e37;">${overallStatus}</div>`;
} else if (overallStatus == "skipped") {
  overAllStatusInnerElement = `<div class="over-all-highlight" style="background-color:#C0C0C0 ; color:white; font-weight:bold;">${overallStatus}</div>`;
} else if (overallStatus == "known failures") {
  overAllStatusInnerElement = `<div class="over-all-highlight" style="background-color:#FFC300 ; color:#008000; font-weight:bold;">${overallStatus}</div>`;
} else if (overallStatus == "passed with known failures") {
  overAllStatusInnerElement = `<div class="over-all-highlight" style="background-color:#85e74d ; color:white; font-weight:bold;">${overallStatus}</div>`;
} else {
  overAllStatusInnerElement = `<div class="over-all-highlight" style="background-color:#C0C0C0 ; color:white; font-weight:bold;">No Results Found</div>`;
}

overallStatusParentElement.innerHTML = overAllStatusInnerElement;

// set project informations
// "projectInfo": {
//   "environment": "Development Region",
//   "appName": "Google",
//   "browser": "Microsoft Edge",
//   "description": "test description",
//   "url": "https://www.google.com"
// },

var projectInfo = data.projectInfo;
let projectInfoContentDetailsParentElement = document.querySelector("#project-info-content-details");
var projectInfoContentDetailsInnerElement = " ";

projectInfoContentDetailsInnerElement = `
<div class="project-info-element project-info-key">Environment</div>
<div class="project-info-element project-info-separator">:</div>
<div class="project-info-element project-info-value">${projectInfo.environment}</div>

<div class="project-info-element project-info-key">Browser</div>
<div class="project-info-element project-info-separator">:</div>
<div class="project-info-element project-info-value">${projectInfo.browser}</div>

<div class="project-info-element project-info-key">Application</div>
<div class="project-info-element project-info-separator">:</div>
<div class="project-info-element project-info-value">${projectInfo.appName}</div>

<div class="project-info-element project-info-key">URL</div>
<div class="project-info-element project-info-separator">:</div>
<div class="project-info-element project-info-value">${projectInfo.url}</div>

<div class="project-info-element project-info-key">OS</div>
<div class="project-info-element project-info-separator">:</div>
<div class="project-info-element project-info-value">${projectInfo.os}</div>

`;

projectInfoContentDetailsParentElement.innerHTML = projectInfoContentDetailsInnerElement;

let projectInfoContentDescriptionParentElement = document.querySelector("#project-info-content-description");
var projectInfoContentDescriptionInnerElement = " ";

projectInfoContentDescriptionInnerElement = `
<div class="project-info-element project-info-key">Description</div>
<div class="project-info-element project-info-separator">:</div>
<div class="project-info-element project-info-value">${projectInfo.description}</div>

`;

projectInfoContentDescriptionParentElement.innerHTML = projectInfoContentDescriptionInnerElement;

// Load pie charts for feature, test case and test steps on loading the page
// refer Chart.js library : https://www.chartjs.org/docs/latest/samples/other-charts/doughnut.html
window.onload = function () {

  // feature pie chart
  let featureLabels = ["Passed", "Failed", "Skipped", "Known Failures", "Passed With Known Failures"];
  let featurePieDataSet = [
    data.featurePieChartDataMap.passed,
    data.featurePieChartDataMap.failed,
    data.featurePieChartDataMap.skipped,
    data.featurePieChartDataMap.knownFailures,
    data.featurePieChartDataMap.passedWithKnownFailures
  ];

  const featurePieData = {
    labels: featureLabels,
    datasets: [{
      data: featurePieDataSet,
      backgroundColor: [
        '#25a244',
        '#d80032',
        '#c0d6df',
        '#ffc43d',
        '#80b918'
      ]
    }]
  };

  const featurePieConfig = {
    type: 'doughnut',
    data: featurePieData,
    options: {
      maintainAspectRatio: false,
      responsive: true,
      legend: {
        position: 'right'
      },
      title: {
        display: true,
        text: 'Feature Status'
      },
      plugins: {

      }
    }
  };

  const featurePieChart = new Chart(
    document.getElementById('featurePieChart'),
    featurePieConfig
  );

  // testcase pie chart
  let testCaseLabels = ["Passed", "Failed", "Skipped", "Known Failures", "Passed With Known Failures"];
  let testCasePieDataSet = [
    data.testCasePieChartDataMap.passed,
    data.testCasePieChartDataMap.failed,
    data.testCasePieChartDataMap.skipped,
    data.testCasePieChartDataMap.knownFailures,
    data.testCasePieChartDataMap.passedWithKnownFailures
  ];

  const testCasePieData = {
    labels: testCaseLabels,
    datasets: [{
      data: testCasePieDataSet,
      backgroundColor: [
        '#25a244',
        '#d80032',
        '#c0d6df',
        '#ffc43d',
        '#80b918'
      ]
    }]
  };

  const testCasePieConfig = {
    type: 'doughnut',
    data: testCasePieData,
    options: {
      maintainAspectRatio: false,
      responsive: true,
      legend: {
        display: true,
        position: 'right'
      },
      title: {
        display: true,
        text: 'TestCase Status'
      },
      plugins: {

      }
    }
  };

  const testCasePieChart = new Chart(
    document.getElementById('testCasePieChart'),
    testCasePieConfig
  );

  // testcase pie chart
  let testStepLabels = ["Passed", "Failed", "Skipped", "Known Failures", "Pending", "Undefined", "Ambiguous", "Unused"];
  let testStepPieDataSet = [
    data.testStepPieChartDataMap.passed,
    data.testStepPieChartDataMap.failed,
    data.testStepPieChartDataMap.skipped,
    data.testStepPieChartDataMap.knownFailures,
    data.testStepPieChartDataMap.pending,
    data.testStepPieChartDataMap.undefined,
    data.testStepPieChartDataMap.ambiguous,
    data.testStepPieChartDataMap.unused,
  ];

  const testStepPieData = {
    labels: testStepLabels,
    datasets: [{
      data: testStepPieDataSet,
      backgroundColor: [
        '#25a244',
        '#d80032',
        '#c0d6df',
        '#ffc43d',
        '#f72585',
        '#124559',
        '#aed9e0',
        '#8a817c'
      ]
    }]
  };

  const testStepPieConfig = {
    type: 'doughnut',
    data: testStepPieData,
    options: {
      maintainAspectRatio: false,
      responsive: true,
      legend: {
        display: true,
        position: 'right'
      },
      title: {
        display: true,
        text: 'TestStep Status'
      }
    }
  };

  const testStepPieChart = new Chart(
    document.getElementById('testStepePieChart'),
    testStepPieConfig
  );

}

// load feature stats table - starts
let featureStatsTable = document.querySelector("#feature-stats-data-output");
let out = "";
var featuresList = data.featuresStats;

for (let feature of featuresList) {
  var statusCell = "";
  if (feature.status == "passed") {
    statusCell = `<td style="color:#008000; font-weight:bold;">${feature.status}</td>`;
  } else if (feature.status == "failed") {
    statusCell = `<td style="color:#e01e37; font-weight:bold;">${feature.status}</td>`;
  } else if (feature.status == "skipped") {
    statusCell = `<td style="color:#C0C0C0; font-weight:bold;">${feature.status}</td>`;
  } else if (feature.status == "known failures") {
    statusCell = `<td style="color:#FFC300; font-weight:bold;">${feature.status}</td>`;
  } else if (feature.status == "passed with known failures") {
    statusCell = `<td style="color:#85e74d; font-weight:bold;">${feature.status}</td>`;
  } else {
    statusCell = `<td>${feature.status}</td>`;
  }

  out += `

  <tr>
    <td style="text-align: left">${feature.featureName}</td>
    <td>${feature.totalCases}</td>
    <td>${feature.passed}</td>
    <td>${feature.failed}</td>
    <td>${feature.skipped}</td>
    <td>${feature.knownFailures}</td>
    <td>${feature.passedWithKnownFailures}</td>
    <td>${feature.passPercent}</td>
    <td>${feature.failPercent}</td>
    <td>${feature.skippedPercent}</td>
    <td>${feature.knownFailPercent}</td>
    <td>${feature.passedWithKnownFailPercent}</td>
    
    ${statusCell}
    
    
    <td>${feature.duration}</td>
  </tr>

  `;
  featureStatsTable.innerHTML = out;
}
// load feature stats table - ends

// load overall testcase stats table - starts
let overallTestCaseStatsTable = document.querySelector("#overall-stats-data-output");
let overAllTestCaseTableOut = "";
var overallTestCaseData = data.overallTestCaseStats;

var statusCell = "";
var overallStatus = overallTestCaseData.overAllStatus;
if (overallStatus == "passed") {
  statusCell = `<td style="color:#008000; font-weight:bold;">${overallStatus}</td>`;
} else if (overallStatus == "failed") {
  statusCell = `<td style="color:#e01e37; font-weight:bold;">${overallStatus}</td>`;
} else if (overallStatus == "skipped") {
  statusCell = `<td style="color:#C0C0C0; font-weight:bold;">${overallStatus}</td>`;
} else if (overallStatus == "known failures") {
  statusCell = `<td style="color:#FFC300; font-weight:bold;">${overallStatus}</td>`;
} else if (overallStatus == "passed with known failures") {
  statusCell = `<td style="color:#85e74d; font-weight:bold;">${overallStatus}</td>`;
} else {
  statusCell = `<td>${feature.status}</td>`;
}

overAllTestCaseTableOut = `

  <tr>
    <td>${overallTestCaseData.totalCases}</td>
    <td>${overallTestCaseData.passed}</td>
    <td>${overallTestCaseData.failed}</td>
    <td>${overallTestCaseData.skipped}</td>
    <td>${overallTestCaseData.knownFailures}</td>
    <td>${overallTestCaseData.passedWithKnownFailures}</td>
    <td>${overallTestCaseData.passPercent}</td>
    <td>${overallTestCaseData.failPercent}</td>
    <td>${overallTestCaseData.skippedPercent}</td>
    <td>${overallTestCaseData.knownFailPercent}</td>
    <td>${overallTestCaseData.passedWithKnownFailPercent}</td>
    
    ${statusCell}
    
    
    <td>${overallTestCaseData.overallDuration}</td>
  </tr>

  `;
overallTestCaseStatsTable.innerHTML = overAllTestCaseTableOut;
// load overall testcase stats table - ends