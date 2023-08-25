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

// project info section
var projectInfo = data.projectInfo;

// project into content column a
let projectInfoContentDetailsParentElement = document.querySelector("#project-info-content-details-a");
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

<div class="project-info-element project-info-key">OS</div>
<div class="project-info-element project-info-separator">:</div>
<div class="project-info-element project-info-value">${projectInfo.os}</div>

`;

projectInfoContentDetailsParentElement.innerHTML = projectInfoContentDetailsInnerElement;

// project info content column b

let projectInfoContentDetailsBParentElement = document.querySelector("#project-info-content-details-b");
var projectInfoContentDetailsBInnerElement = " ";

projectInfoContentDetailsBInnerElement = `
<div class="project-info-element project-info-key">Start Time</div>
<div class="project-info-element project-info-separator">:</div>
<div class="project-info-element project-info-value">${projectInfo.startTime}</div>

<div class="project-info-element project-info-key">End Time</div>
<div class="project-info-element project-info-separator">:</div>
<div class="project-info-element project-info-value">${projectInfo.endTime}</div>

<div class="project-info-element project-info-key">Elapsed Time</div>
<div class="project-info-element project-info-separator">:</div>
<div class="project-info-element project-info-value">${projectInfo.totalDuration}</div>

`;

projectInfoContentDetailsBParentElement.innerHTML = projectInfoContentDetailsBInnerElement;

// project info content column c

let projectInfoContentDescriptionParentElement = document.querySelector("#project-info-content-description");
var projectInfoContentDescriptionInnerElement = " ";

projectInfoContentDescriptionInnerElement = `
<div class="project-info-element project-info-key">Description</div>
<div class="project-info-element project-info-separator">:</div>
<div class="project-info-element project-info-value project-info-content-description-value">${projectInfo.description}</div>

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
        '#73e2a7'
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
        '#73e2a7'
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
    statusCell = `<td style="color:#73e2a7; font-weight:bold;">${feature.status}</td>`;
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
  statusCell = `<td style="color:#73e2a7; font-weight:bold;">${overallStatus}</td>`;
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


// Display features, its scenarios, its steps and its screenshots

var features = data.featureMapList;

// On selecting any feature link from left side navigation,
// clearing backgroup of other features
// add background only for selected feature
function loadScenarioContent(selector) {
  $("#loadOnClick").html($(selector).html());

  $("ul > li > a").on("click", function () {
    $("ul li a").css("color", "black");
    $("ul li a").css("background-color", "transparent");
    $("ul li a").css("border", "none");

    $("ul li").css("color", "black");
    $("ul li").css("background-color", "transparent");

    $(this).css("color", "black");
    $(this).css("background-color", "#dee2e6");
    $(this.parentNode).css("background-color", "#dee2e6");
  });
};

// when document becomes ready, it selects and highlights
// the first feature navigation option
$(document).ready(function () {
  var firstFeatureId = features[0].id
  document.querySelector("li a[href='#" + features[0].id + "']").parentNode.classList.add("side-nav-item-active");
  loadScenarioContent("#" + firstFeatureId);

});

function getFeatureNavigationLineStatusColor(featureStatus) {
  var statusCell = "";
  if (featureStatus == "passed") {
    statusCell = `<i class="fa-solid fa-circle-check status-icon" style="color: #0e920c;"></i>`;
  } else if (featureStatus == "failed") {
    statusCell = `<i class="fa-solid fa-circle-xmark status-icon" style="color: #f20202;"></i>`;
  } else if (featureStatus == "skipped") {
    statusCell = `<i class="fa-solid fa-circle-arrow-right status-icon" style="color: #c3c6cb;"></i>`;
  } else if (featureStatus == "known failures") {
    statusCell = `<i class="fa-solid fa-circle-exclamation status-icon" style="color: #f0cf75;"></i>`;
  } else if (featureStatus == "passed with known failures") {
    statusCell = `<i class="fa-solid fa-circle-exclamation status-icon" style="color: #73e2a7;"></i>`;
  } else {
    statusCell = "";
  }
  return statusCell;
}

function getStepLineColorBasedOnStatus(stepStatus) {
  var stepLineColor = "";
  if (stepStatus == "passed") {
    stepLineColor = "style='color: #008000'";
  } else if (stepStatus == "failed") {
    stepLineColor = "style='color: #e01e37'";
  } else if (stepStatus == "skipped") {
    stepLineColor = "style='color: #C0C0C0'";
  } else if (stepStatus == "known failures") {
    stepLineColor = "style='color: #FFC300'";
  } else if (stepStatus == "pending") {
    stepLineColor = "style='color: #f72585'";
  } else if (stepStatus == "undefined") {
    stepLineColor = "style='color: #124559'";
  } else if (stepStatus == "ambiguous") {
    stepLineColor = "style='color: #aed9e0'";
  } else if (stepStatus == "unused") {
    stepLineColor = "style='color: #8a817c'";
  }
  return stepLineColor;
}

function getScenarioLineColorBasedOnStatus(scenarioStatus) {
  var scLineColor = "";
  if (scenarioStatus == "passed") {
    scLineColor = "style='color: #008000'";
  } else if (scenarioStatus == "failed") {
    scLineColor = "style='color: #e01e37'";
  } else if (scenarioStatus == "skipped") {
    scLineColor = "style='color: #C0C0C0'";
  } else if (scenarioStatus == "known failures") {
    scLineColor = "style='color: #FFC300'";
  } else if (scenarioStatus == "passed with known failures") {
    scLineColor = "style='color: #73e2a7'";
  }
  return scLineColor;
}

// it consist of whole elements
var completeResultSectionParentElement = document.querySelector("#complete-result-section");
var featureListElement = "";
var content = "";

for (let feature of features) {
  var statusCell = getFeatureNavigationLineStatusColor(feature.status);

  // prepare left side features link as li
  featureListElement += `
  <li class="side-nav-item"><a id="${feature.id}-a" href="#${feature.id}" class="featureLinks" onclick='loadScenarioContent("#${feature.id}")'>${feature.name}</a><span>${feature.duration}</span>${statusCell}</li>
  `;



  var scenarios = feature.scenarios;
  var scenarioElements = "";
  for (let scenario of scenarios) {
    // steps
    var steps = scenario.steps;
    var stepElements = "";

    for (let step of steps) {
      var stepLineColor = getStepLineColorBasedOnStatus(step.status);
      var stepError = "";

      if (step.error != null) {
        stepError = `
          <div class="step-error">${step.error}</div>
        `;
      }

      var stepEmbeddings = "";
      var currentStepStatus = step.status;

      if (step.embeddings != null && step.embeddings.length > 0) {
        var i = 0;
        for (let embedding of step.embeddings) {

          var attachmentSource = "";
          if (embedding.mime_type == "text/plain") {
            attachmentSource = `<div>${embedding.data}</div>`;
          } else {
            var uint8array = new TextEncoder("utf-8").encode(embedding.data);
            var text = new TextDecoder().decode(uint8array);
            attachmentSource = `<img src="data:${embedding.mime_type};base64, ${text}">`;
          }

          var ignorableStatuses = ['skipped', 'pending', 'undefined', 'ambiguous', 'unused'];
          if (ignorableStatuses.includes(step.status)) {
            stepEmbeddings += `
              <div>
              <div class="embedding-name">${embedding.name}</div>
              <div class="embedding-src">${attachmentSource}</div>
              <div class="embedding-name">${currentStepStatus} step</div>
              </div>
            `;
          } else {
            stepEmbeddings += `
              <div>
                <div class="embedding-name">${embedding.name}</div>
                <div class="embedding-src">${attachmentSource}</div>
              </div>
            `;
          }

          i++;
        }
      } else {
        stepEmbeddings += `
              <div>
                <div class="embedding-name">${currentStepStatus}</div>
              </div>
            `;
      }

      stepElements += `

        <div id="#${step.name}-${step.line}" class="step-item">
          <div id="#step-${step.name}-${step.line}-collapsible" class="step-name" ${stepLineColor} onclick="collapseExpandStep('#step-${step.name}-${step.line}-collapsible-content')">${step.name}</div>
          <div id="#step-${step.name}-${step.line}-collapsible-content" class="step-info-section">
            ${stepError}
            ${stepEmbeddings}
          </div>
        </div>

      `;
    }

    var scLineColor = getScenarioLineColorBasedOnStatus(scenario.status);
    scenarioElements += `
        <div id="#${scenario.id}" class="scenario-item">
          <div id="#${scenario.id}-collapsible" class="scenario-name collapsible" onclick="collapseExpandScenario('#${scenario.id}-collapsible-content')">
            <div ${scLineColor}>${scenario.name}</div>
            <span class="fa-solid fa-angle-down scenario-expand-icon" style="color: #9eb3c2;"></span>
          </div>
          <div id="#${scenario.id}-collapsible-content" class="step-items">
            ${stepElements}
          </div>
        </div>
      `;
  }

  // prepare right side scenarios container for each above li
  content += `
    <div id="${feature.id}" class="displayOnClick">
      ${scenarioElements}
    </div>
    `;

}
///<nav class="displayInLine" style="width: 30%; float: left;">
var containerElement = `

  <section class="complete-result-container">
    <nav>
      <ul>
        ${featureListElement}
      </ul>
    </nav>

    <div class="feature-specific-sc-container" id="loadOnClick" style="width:70%; float: right;">

    </div>
  </section>

${content}

  `;

completeResultSectionParentElement.innerHTML = containerElement;

function collapseExpandScenario(id) {

  if (document.getElementById(id).style.display == "flex") {
    document.getElementById(id).style.display = "none";
  } else {
    document.getElementById(id).style.display = "flex";
  }
}

function collapseExpandStep(id) {

  if (document.getElementById(id).style.display == "block") {
    document.getElementById(id).style.display = "none";
  } else {
    document.getElementById(id).style.display = "block";
  }
}

$(document).ready(function () { $("img").click(function () { this.requestFullscreen() }) });

