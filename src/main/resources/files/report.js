var json = `
 <<json-input>>
`;

// Read Json Data
var data = JSON.parse(json);

// populate overall status on top of the report
let overallStatusParentElement = document.querySelector(".over-all-highlight-section");
var overAllStatusInnerElement = " ";
var overallStatus = data.overAllExecutionStatus;
if (overallStatus == "passed") {
  overAllStatusInnerElement = `<div class="over-all-highlight" style="background-color:#008000;">${overallStatus.toUpperCase()}</div>`;
} else if (overallStatus == "failed") {
  overAllStatusInnerElement = `<div class="over-all-highlight" style="background-color:#e01e37;">${overallStatus.toUpperCase()}</div>`;
} else if (overallStatus == "skipped") {
  overAllStatusInnerElement = `<div class="over-all-highlight" style="background-color:#C0C0C0 ; color:white; font-weight:bold;">${overallStatus.toUpperCase()}</div>`;
} else if (overallStatus == "known failures") {
  overAllStatusInnerElement = `<div class="over-all-highlight" style="background-color:#FFC300 ; color:#008000; font-weight:bold;">${overallStatus.toUpperCase()}</div>`;
} else if (overallStatus == "passed with known failures") {
  overAllStatusInnerElement = `<div class="over-all-highlight" style="background-color:#85e74d ; color:white; font-weight:bold;">${overallStatus.toUpperCase()}</div>`;
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

// feature pie chart
const featureCenterText = {
  afterDatasetsDraw(chart) {
    const {ctx} = chart;
    const text = data.featurePieChartDataMap.totalFeatures;
    ctx.save();
    const x = chart.getDatasetMeta(0).data[0].x;
    const y = chart.getDatasetMeta(0).data[0].y;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.font = 'bold 15px sans-serif';
    ctx.fillText(text, x, y);
  }
}

let featureLabels = [
  "Passed",
  "Failed",
  "Skipped",
  "Known Failures",
  "Passed With Known Failures"
];

let featurePieDataSet = [
  data.featurePieChartDataMap.passed,
  data.featurePieChartDataMap.failed,
  data.featurePieChartDataMap.skipped,
  data.featurePieChartDataMap.knownFailures,
  data.featurePieChartDataMap.passedWithKnownFailures
];

const featureChartConfig = {
  type: "doughnut",
  data: {
    labels: featureLabels,
    datasets: [
      {
        label: 'Count',
        data: featurePieDataSet,
        backgroundColor: [
          '#008000',
          '#e01e37',
          '#C0C0C0',
          '#FFC300',
          '#73e2a7'
        ]
      }
    ]
  },
  options: {
    radius: 65, // outer radius
    animation: {
      animateScale: true // effect while loading page
    },
    cutout: 15, // inner radius
    maintainAspectRatio: false, // adopt as per container element
    responsive: true,  // adopt as per container element
    plugins: {
      // chart title
      title: {
        display: true,
        text: 'Feature Status',
        position: 'top',
        font: {
          weight: 'bold'
        },
        color: 'grey',
        padding: {
          top: 0,
          bottom: 50
        }
      },
      // chart legend
      legend: {
        display: true,
        position: 'right',
        align: 'start',
        rtl: false,
        title: {
          color: '#03256c',
          display: true,
          text: 'Status',
          font: {
            weight: 'bold'
          }
        },
        // chart legend text & color box prop
        labels: {
          boxWidth: 10,
          textAlign: 'left'
        }
      },
      tooltip: {
        enabled: true
      },
      // this will work only if we included plugin " plugins: [ChartDataLabels] "
      // It helps to show data over the doughnut colored area
      // need to import plugin "chartjs-plugin-datalabels" library
      // check video "https://www.youtube.com/watch?v=hyyIX_8Xe8w" for more info
      datalabels: {
        color: 'white',
        font: {
          weight: 'bold'
        },
        formatter: (value, context) => {
          // const datapoints = context.chart.data.datasets[0].data;
          // function totalSum(total, dataPoint) {
          //   return total + dataPoint;
          // }

          // const totalValue = datapoints.reduce(totalSum, 0);
          // const percentageValue = (value / totalValue * 100).toFixed(0);
          // return `${percentageValue}%`;
          return value == 0 ? null : value;
        }
      },
      // this will work only if we included plugin <script src="https://unpkg.com/chart.js-plugin-labels-dv/dist/chartjs-plugin-labels.min.js">
      // No need to register the plugin via " plugins: []". It is just enough to add above library "chartjs-plugin-labels-dv" in html
      // Check URL : https://www.npmjs.com/package/chart.js-plugin-labels-dv
      // Also check video "https://www.youtube.com/watch?v=xpN394MAhPA"
      // It helps to show % values outside the chart with easy configs
      labels: {
        render: 'percentage',
        precision: 0,
        showZero: false,
        fontStyle: 'bold',
        position: 'outside',
        textShadow: true,
        arc: true
        // fontColor: data.datasets[0].backgroundColor
      }
    },
    layout: {
      padding: {
        bottom: 60
      }
    }
  },
  plugins: [ChartDataLabels, featureCenterText]

};



const featureDoughnutChart = new Chart(
  document.querySelector("#featurePieChart"),
  featureChartConfig
)

// testcase pie chart
const testCaseCenterText = {
  afterDatasetsDraw(chart) {
    const {ctx} = chart;
    const text = data.testCasePieChartDataMap.totalCases;
    ctx.save();
    const x = chart.getDatasetMeta(0).data[0].x;
    const y = chart.getDatasetMeta(0).data[0].y;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.font = 'bold 15px sans-serif';
    ctx.fillText(text, x, y);
  }
}

let testCaseLabels = ["Passed", "Failed", "Skipped", "Known Failures"];
let testCasePieDataSet = [
  data.testCasePieChartDataMap.passed,
  data.testCasePieChartDataMap.failed,
  data.testCasePieChartDataMap.skipped,
  data.testCasePieChartDataMap.knownFailures
];

const testCaseChartConfig = {
  type: "doughnut",
  data: {
    labels: testCaseLabels,
    datasets: [
      {
        label: 'Count',
        data: testCasePieDataSet,
        backgroundColor: [
          '#008000',
          '#e01e37',
          '#C0C0C0',
          '#FFC300'
        ]
      }
    ]
  },
  options: {
    radius: 65, // outer radius
    animation: {
      animateScale: true // effect while loading page
    },
    cutout: 15, // inner radius
    maintainAspectRatio: false, // adopt as per container element
    responsive: true,  // adopt as per container element
    plugins: {
      // chart title
      title: {
        display: true,
        text: 'TestCase Status',
        position: 'top',
        font: {
          weight: 'bold'
        },
        color: 'grey',
        padding: {
          top: 0,
          bottom: 50
        }
      },
      // chart legend
      legend: {
        display: true,
        position: 'right',
        align: 'start',
        rtl: false,
        title: {
          color: '#03256c',
          display: true,
          text: 'Status',
          font: {
            weight: 'bold'
          }
        },
        // chart legend text & color box prop
        labels: {
          boxWidth: 10,
          textAlign: 'left'
        }
      },
      tooltip: {
        enabled: true
      },
      // this will work only if we included plugin " plugins: [ChartDataLabels] "
      // It helps to show data over the doughnut colored area
      // need to import plugin "chartjs-plugin-datalabels" library
      // check video "https://www.youtube.com/watch?v=hyyIX_8Xe8w" for more info
      datalabels: {
        color: 'white',
        font: {
          weight: 'bold'
        },
        formatter: (value, context) => {
          // const datapoints = context.chart.data.datasets[0].data;
          // function totalSum(total, dataPoint) {
          //   return total + dataPoint;
          // }

          // const totalValue = datapoints.reduce(totalSum, 0);
          // const percentageValue = (value / totalValue * 100).toFixed(0);
          // return `${percentageValue}%`;
          return value == 0 ? null : value;
        }
      },
      // this will work only if we included plugin <script src="https://unpkg.com/chart.js-plugin-labels-dv/dist/chartjs-plugin-labels.min.js">
      // No need to register the plugin via " plugins: []". It is just enough to add above library "chartjs-plugin-labels-dv" in html
      // Check URL : https://www.npmjs.com/package/chart.js-plugin-labels-dv
      // Also check video "https://www.youtube.com/watch?v=xpN394MAhPA"
      // It helps to show % values outside the chart with easy configs
      labels: {
        render: 'percentage',
        precision: 0,
        showZero: false,
        fontStyle: 'bold',
        position: 'outside',
        textShadow: true,
        arc: true
        // fontColor: data.datasets[0].backgroundColor
      }
    },
    layout: {
      padding: {
        bottom: 60
      }
    }
  },
  plugins: [ChartDataLabels, testCaseCenterText]

};

const doughnutChart = new Chart(
  document.querySelector("#testCasePieChart"),
  testCaseChartConfig
)

// testStep pie chart
const testStepCenterText = {
  afterDatasetsDraw(chart) {
    const {ctx} = chart;
    const text = data.testStepPieChartDataMap.totalSteps;
    ctx.save();
    const x = chart.getDatasetMeta(0).data[0].x;
    const y = chart.getDatasetMeta(0).data[0].y;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.font = 'bold 15px sans-serif';
    ctx.fillText(text, x, y);
  }
}

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

const stepChartConfig = {
  type: "doughnut",
  data: {
    labels: testStepLabels,
    datasets: [
      {
        label: 'Count',
        data: testStepPieDataSet,
        backgroundColor: [
          '#008000',
          '#e01e37',
          '#C0C0C0',
          '#FFC300',
          '#f72585',
          '#124559',
          '#aed9e0',
          '#8a817c'
        ]
      }
    ]
  },
  options: {
    radius: 65, // outer radius
    animation: {
      animateScale: true // effect while loading page
    },
    cutout: 15, // inner radius
    maintainAspectRatio: false, // adopt as per container element
    responsive: true,  // adopt as per container element
    plugins: {
      // chart title
      title: {
        display: true,
        text: 'Step Status',
        position: 'top',
        font: {
          weight: 'bold'
        },
        color: 'grey',
        padding: {
          top: 0,
          bottom: 50
        }
      },
      // chart legend
      legend: {
        display: true,
        position: 'right',
        align: 'start',
        rtl: false,
        title: {
          color: '#03256c',
          display: true,
          text: 'Status',
          font: {
            weight: 'bold'
          }
        },
        // chart legend text & color box prop
        labels: {
          boxWidth: 10,
          textAlign: 'left'
        }
      },
      tooltip: {
        enabled: true
      },
      // this will work only if we included plugin " plugins: [ChartDataLabels] "
      // It helps to show data over the doughnut colored area
      // need to import plugin "chartjs-plugin-datalabels" library
      // check video "https://www.youtube.com/watch?v=hyyIX_8Xe8w" for more info
      datalabels: {
        color: 'white',
        font: {
          weight: 'bold'
        },
        formatter: (value, context) => {
          // const datapoints = context.chart.data.datasets[0].data;
          // function totalSum(total, dataPoint) {
          //   return total + dataPoint;
          // }

          // const totalValue = datapoints.reduce(totalSum, 0);
          // const percentageValue = (value / totalValue * 100).toFixed(0);
          // return `${percentageValue}%`;
          return value == 0 ? null : value;
        }
      },
      // this will work only if we included plugin <script src="https://unpkg.com/chart.js-plugin-labels-dv/dist/chartjs-plugin-labels.min.js">
      // No need to register the plugin via " plugins: []". It is just enough to add above library "chartjs-plugin-labels-dv" in html
      // Check URL : https://www.npmjs.com/package/chart.js-plugin-labels-dv
      // Also check video "https://www.youtube.com/watch?v=xpN394MAhPA"
      // It helps to show % values outside the chart with easy configs
      labels: {
        render: 'percentage',
        precision: 0,
        showZero: false,
        fontStyle: 'bold',
        position: 'outside',
        textShadow: true,
        arc: true
        // fontColor: data.datasets[0].backgroundColor
      }
    },
    layout: {
      padding: {
        bottom: 60
      }
    },
    elements: {
      arc: {
        // borderColor:
      }
    }
  },
  plugins: [ChartDataLabels, testStepCenterText]

};

const stepDoughnutChart = new Chart(
  document.querySelector("#testStepePieChart"),
  stepChartConfig
)

// defect pie chart
const reducer = (accumulator, curr) => accumulator + curr;
var totalDefects = data.defectPieChartDataMap.trackedKnownDefects
+ data.defectPieChartDataMap.unTrackedKnownDefects
+ data.defectPieChartDataMap.newDefects
+ data.defectPieChartDataMap.otherDefects;
totalDefects = totalDefects > 0 ? totalDefects : 'No Defects';

const defectCenterText = {
  afterDatasetsDraw(chart) {
    const {ctx} = chart;
    const text = totalDefects; //chart.data.datasets[0].data.reduce(reducer);
    ctx.save();
    const x = chart.getDatasetMeta(0).data[0].x;
    const y = chart.getDatasetMeta(0).data[0].y;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.font = 'bold 15px sans-serif';
    ctx.fillText(text, x, y);
  }
}

let defectCategoryLabel = ["Tracked-KnownFailure", "UnTracked-KnownFailure", "NewFailure", "Other Failures"];
let defectPieDataSet = [
  data.defectPieChartDataMap.trackedKnownDefects,
  data.defectPieChartDataMap.unTrackedKnownDefects,
  data.defectPieChartDataMap.newDefects,
  data.defectPieChartDataMap.otherDefects
];

const defectChartConfig = {
  type: "doughnut",
  data: {
    labels: defectCategoryLabel,
    datasets: [
      {
        label: 'Count',
        data: defectPieDataSet,
        backgroundColor: [
          '#60d394',
          '#aaf683',
          '#ff5a5f',
          '#c81d25'
        ]
      }
    ]
  },
  options: {
    radius: 65, // outer radius
    animation: {
      animateScale: true // effect while loading page
    },
    cutout: 15, // inner radius
    maintainAspectRatio: false, // adopt as per container element
    responsive: true,  // adopt as per container element
    plugins: {
      // chart title
      title: {
        display: true,
        text: 'Defect Status',
        position: 'top',
        font: {
          weight: 'bold'
        },
        color: 'grey',
        padding: {
          top: 0,
          bottom: 50
        }
      },
      // chart legend
      legend: {
        display: true,
        position: 'right',
        align: 'start',
        rtl: false,
        title: {
          color: '#03256c',
          display: true,
          text: 'Status',
          font: {
            weight: 'bold'
          }
        },
        // chart legend text & color box prop
        labels: {
          boxWidth: 10,
          textAlign: 'left'
        }
      },
      tooltip: {
        enabled: true
      },
      // this will work only if we included plugin " plugins: [ChartDataLabels] "
      // It helps to show data over the doughnut colored area
      // need to import plugin "chartjs-plugin-datalabels" library
      // check video "https://www.youtube.com/watch?v=hyyIX_8Xe8w" for more info
      datalabels: {
        color: 'white',
        font: {
          weight: 'bold'
        },
        formatter: (value, context) => {
          // const datapoints = context.chart.data.datasets[0].data;
          // function totalSum(total, dataPoint) {
          //   return total + dataPoint;
          // }

          // const totalValue = datapoints.reduce(totalSum, 0);
          // const percentageValue = (value / totalValue * 100).toFixed(0);
          // return `${percentageValue}%`;
          return value == 0 ? null : value;
        }
      },
      // this will work only if we included plugin <script src="https://unpkg.com/chart.js-plugin-labels-dv/dist/chartjs-plugin-labels.min.js">
      // No need to register the plugin via " plugins: []". It is just enough to add above library "chartjs-plugin-labels-dv" in html
      // Check URL : https://www.npmjs.com/package/chart.js-plugin-labels-dv
      // Also check video "https://www.youtube.com/watch?v=xpN394MAhPA"
      // It helps to show % values outside the chart with easy configs
      labels: {
        render: 'percentage',
        precision: 0,
        showZero: false,
        fontStyle: 'bold',
        position: 'outside',
        textShadow: true,
        arc: true
        // fontColor: data.datasets[0].backgroundColor
      }
    },
    layout: {
      padding: {
        bottom: 60
      }
    },
    elements: {
      arc: {
        // borderColor:
      }
    }
  },
  plugins: [ChartDataLabels, defectCenterText]

};

const defectDoughnutChart = new Chart(
  document.querySelector("#defectPieChart"),
  defectChartConfig
)


// load overall testcase stats table - starts
var overallTestCaseData = data.overallTestCaseStats;

var overAllStatsTable = new gridjs.Grid({
  columns: [
    {
      id: "totalCases",
      name: "Total"
    },
    {
      id: "passed",
      name: "Pass"
    },
    {
      id: "failed",
      name: "Fail"
    },
    {
      id: "skipped",
      name: "Skip"
    },
    {
      id: "knownFailures",
      name: "Known Fails"
    },
    {
      id: "passPercent",
      name: "Pass %"
    },
    {
      id: "failPercent",
      name: "Fail %"
    },
    {
      id: "skippedPercent",
      name: "Skip %"
    },
    {
      id: "knownFailPercent",
      name: "Known Fail %"
    },
    {
      id: "overAllStatus",
      name: "Status",
      formatter: (cell) => {
        return cell.toUpperCase();
      },
      attributes: (cell) => {
        if (cell == "passed") {
          return {
            'class': 'gridjs-td passed-status'
          };
        } else if (cell == "failed") {
          return {
            'class': 'gridjs-td failed-status'
          };
        } else if (cell == "skipped") {
          return {
            'class': 'gridjs-td skipped-status'
          };
        } else if (cell == "known failures") {
          return {
            'class': 'gridjs-td known-failures-status'
          };
        } else if (cell == "passed with known failures") {
          return {
            'class': 'gridjs-td passed-with-known-failures-status'
          };
        }
      }

    },
    {
      id: "overallDuration",
      name: "Duration"
    }
  ],
  autowidth: true,
  style: {
    table: {
      'box-shadow': '2px 2px 15px 2px #C0C0C0'
    },
    th: {
      'background-color': '#03045e',
      color: 'white',
      'min-width': 'fit-content !important',
      'text-align': 'center',
      'padding-left': '10px',
      'padding-right': '10px'
    },
    td: {
      'text-align': 'center',
      'padding-left': '10px',
      'padding-right': '10px'
    }
  },
  data: [overallTestCaseData]

});
overAllStatsTable.render(document.getElementById("overall-stats-data-output-container"));
// load overall testcase stats table - ends

// load feature stats table - starts
let out = "";
var featuresList = data.featuresStats;

var table = new gridjs.Grid({
  columns: [
    {
      id: "featureName",
      name: "Feature Name",
      sort: true
    },
    {
      id: "totalCases",
      name: "Total"
    },
    {
      id: "passed",
      name: "Pass"
    },
    {
      id: "failed",
      name: "Fail"
    },
    {
      id: "skipped",
      name: "Skip"
    },
    {
      id: "knownFailures",
      name: "Known Fails"
    },
    {
      id: "passPercent",
      name: "Pass %"
    },
    {
      id: "failPercent",
      name: "Fail %"
    },
    {
      id: "skippedPercent",
      name: "Skip %"
    },
    {
      id: "knownFailPercent",
      name: "Known Fail %"
    },
    {
      id: "status",
      name: "Status",
      sort: true,
      formatter: (cell) => {
        return cell.toUpperCase();
      },
      attributes: (cell) => {
        if (cell == "passed") {
          return {
            'class': 'gridjs-td passed-status'
          };
        } else if (cell == "failed") {
          return {
            'class': 'gridjs-td failed-status'
          };
        } else if (cell == "skipped") {
          return {
            'class': 'gridjs-td skipped-status'
          };
        } else if (cell == "known failures") {
          return {
            'class': 'gridjs-td known-failures-status'
          };
        } else if (cell == "passed with known failures") {
          return {
            'class': 'gridjs-td passed-with-known-failures-status'
          };
        }
      }

    },
    {
      id: "duration",
      name: "Duration"
    }
  ],
  pagination: {
    limit: 10,
    summary: true
  },
  search: true,
  autowidth: true,
  resizable: true,
  style: {
    table: {
      'box-shadow': '2px 2px 15px 2px #C0C0C0'
    },
    th: {
      'background-color': '#03045e',
      color: 'white',
      'min-width': 'fit-content !important',
      'text-align': 'center',
      'padding-left': '10px',
      'padding-right': '10px'
    },
    td: {
      'text-align': 'center',
      'padding-left': '10px',
      'padding-right': '10px'
    }
  },
  data: featuresList

});
table.render(document.getElementById("feature-stats-data-output-container"));
// load feature stats table - ends

// Display features, its scenarios, its steps and its screenshots
var features = data.featureMapList;

// it consist of whole elements
var completeResultSectionParentElement = document.querySelector("#complete-result-section");
var featureListElement = getFeatureElements(features);
var content = getContentToDisplayOnRightWhenClickingFeatureNavLink(features);
var testResultSectionContainerElement = `

  <section class="complete-result-container">
    <div class="feature-column">Features</div>
    <div class="scenario-and-step-results-column">Scenarios & Steps Results</div>
    <nav>
      <ul>
        ${featureListElement}
      </ul>
    </nav>

    <div class="feature-specific-sc-container" id="loadOnClick" style=" float: right;">

    </div>
  </section>

  ${content}

  `;

completeResultSectionParentElement.innerHTML = testResultSectionContainerElement;

$(document).ready(function () { $("img").click(function () { this.requestFullscreen() }) });

function getContentToDisplayOnRightWhenClickingFeatureNavLink(features) {
  var displayOnClickContent = "";
  for (let feature of features) {
    // get all scenario elements to be displayed on right side page
    var scenarioElements = getScenarioElements(feature.scenarios);

    // then build the content panel section with above scenario elements
    // for every feature
    displayOnClickContent += `
      <div id="${feature.id}" class="displayOnClick">
        ${scenarioElements}
      </div>
      `;
  }
  return displayOnClickContent;
}

function getFeatureElements(features) {
  var featureListElement = "";
  var i = 1;
  for (let feature of features) {
    var statusCell = getFeatureNavigationLineStatusColor(feature.status);
    var featureId = replaceEscapesAndHtmlWithHyphen(feature.id);

    // prepare left side features link as li
    featureListElement += `
    <li class="side-nav-item">
      <a
        id="${featureId}-a"
        href="#${featureId}"
        class="featureLinks"
        onclick='loadScenarioContent("#${featureId}")'>
        Feature ${i} : ${feature.name}
      </a>
      <span>${feature.duration}</span>
      ${statusCell}
    </li>
    `;
    i++;
  }
  return featureListElement;
}

function getScenarioElements(scenarios) {
  var scenarioElements = "";
  var i = 1;
  for (let scenario of scenarios) {
    var steps = scenario.steps;
    var stepElements = getStepElements(steps);
    var scLineColor = getScenarioLineColorBasedOnStatus(scenario.status);
    var scenarioId = replaceEscapesAndHtmlWithHyphen(scenario.id);

    var beforeScenarioError = "";
    if (scenario.beforeError.length != 0) {
      var beforeScenarioErrorDecoded = atob(scenario.beforeError);
      beforeScenarioError = `
        <div class="scenario-hook-section">
          <div class="scenario-hook-error-key">Scenario ${i} BeforeScenario-Hook-Error : </div>
          <div class="scenario-hook-error-value">${beforeScenarioErrorDecoded}</div>
        </div>
      `;
    }

    var afterScenarioError = "";
    if (scenario.afterError.length != 0) {
      var afterScenarioErrorDecoded = atob(scenario.afterError);
      afterScenarioError = `
        <div class="scenario-hook-section">
          <div class="scenario-hook-error-key">Scenario ${i} AfterScenario-Hook-Error : </div>
          <div class="scenario-hook-error-value">${afterScenarioErrorDecoded}</div>
        </div>
      `;
    }

    // Below is a box of scenario line and its steps details.
    // By default, steps are hidden [display : none]
    // On clicking scenario line, step is displayed
    scenarioElements += `
    <div class="scenario-item-box">
        ${beforeScenarioError}
        <div id="#${scenarioId}" class="scenario-item">
          <div id="#${scenarioId}-collapsible" class="scenario-name collapsible" onclick="collapseExpandScenario('#${scenarioId}-collapsible-content')">
            <div ${scLineColor}>Scenario ${i} : ${scenario.name}</div>
            <span class="fa-solid fa-angle-down scenario-expand-icon" style="color: #9eb3c2;"></span>
          </div>
          <div id="#${scenarioId}-collapsible-content" class="step-items">
            ${stepElements}
          </div>
        </div>
        ${afterScenarioError}
    </div>
      `;
    i++;
  }
  return scenarioElements;
}

function getStepElements(steps) {
  var stepElements = "";
  var i = 1;
  for (let step of steps) {
    var stepLineColor = getStepLineColorBasedOnStatus(step.status);
    var beforeStepError = "";
    if (step.beforeError.length != 0) {
      var beforeStepErrorDecoded = atob(step.beforeError);
      beforeStepError = `
        <div class="step-name">
          <div class="step-hook-error-section">
            <div>Step ${i} BeforeStep-Hook-Error : </div>
            <div class="step-hook-error-value">${beforeStepErrorDecoded}</div>
          </div>
        </div>
      `;
    }

    var afterStepError = "";
    if (step.afterError.length != 0) {
      var afterStepErrorDecoded = atob(step.afterError);
      afterStepError = `
      <div class="step-name">
        <div class="step-hook-error-section">
          <div>Step ${i} AfterStep-Hook-Error : </div>
          <div class="step-hook-error-value">${afterStepErrorDecoded}</div>
        </div>
      </div>
      `;
    }

    var stepError = "";

    if (step.error != null && step.error.length != 0) {
      var stepErrorDecoded = atob(step.error); // decodes the encoded error text
      stepError = `
        <div class="step-error">${stepErrorDecoded}</div>
      `;
    }

    var stepEmbeddings = getStepEmbeddingElements(step.embeddings, step.status);
    var stepName = step.name;
    var stepNameForId = replaceEscapesAndHtmlWithHyphen(stepName);

    stepElements += `
      <div id="#${stepNameForId}-${step.line}" class="step-item">
        ${beforeStepError}
        <div
          id="#step-${stepNameForId}-${step.line}-collapsible"
          class="step-name" ${stepLineColor}
          onclick="collapseExpandStep('#step-${stepNameForId}-${step.line}-collapsible-content')">
            <div>Step ${i} : ${stepName}</div>
            <div><i class="fa-sharp fa-solid fa-circle"></i></div>
        </div>

        <div
          id="#step-${stepNameForId}-${step.line}-collapsible-content"
          class="step-info-section">
            ${stepError}
            ${stepEmbeddings}
        </div>
        ${afterStepError}
      </div>
    `;
    i++;
  }
  return stepElements;
}

function replaceEscapesAndHtmlWithHyphen(value) {
  return value.replaceAll("<br>", "-")
    .replaceAll("&emsp", "-")
    .replaceAll("'", "-")
    .replaceAll(";", "-")
    .replaceAll(" ", "-");
}

function getStepEmbeddingElements(embeddings, stepStatus) {
  var stepEmbeddingElement = "";
  let index = 0;
  if (embeddings != null && embeddings.length > 0) {
    for (let embedding of embeddings) {
      index = index+1;
      var attachmentSource = getStepScreenshotElement(embedding.mime_type, embedding.data, embedding.name, index);
      if (isStepStatusIngnorable(stepStatus)) {
        stepEmbeddingElement += `
          <div>
          <div class="embedding-name">${embedding.name}</div>
          <div class="embedding-src">${attachmentSource}</div>
          <div class="embedding-name">${stepStatus} step</div>
          </div>
        `;
      } else {
        stepEmbeddingElement += `
          <div>
            <div class="embedding-name">${embedding.name}</div>
            <div class="embedding-src">${attachmentSource}</div>
          </div>
        `;
      }
    }
  } else {
    stepEmbeddingElement += `
          <div>
            <div class="embedding-name">${stepStatus}</div>
          </div>
        `;
  }

  return stepEmbeddingElement;
}

function getStepScreenshotElement(mimeType, encodedString, imageCaption, index) {
  if (mimeType == "text/plain") {
    return `<div>${encodedString}</div>`;
  } else {
    var uint8array = new TextEncoder("utf-8").encode(encodedString);
    var text = new TextDecoder().decode(uint8array);
    return `
    <a href="data:${mimeType};base64, ${text}" data-lightbox="image-${index}" data-title="${imageCaption}">
      <img src="data:${mimeType};base64, ${text}" alt="unable to find image source">
    </a>
    `;
  }
}
// {/* <img src="data:${mimeType};base64, ${text}" alt="unable to find image source"></img> */}
function isStepStatusIngnorable(stepStatus) {
  const ignorableStatuses = ['skipped', 'pending', 'undefined', 'ambiguous', 'unused'];
  return ignorableStatuses.includes(stepStatus);
}

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

    $(this).css("color", "white");
    $(this).css("background-color", "#03045e");
    $(this.parentNode).css("background-color", "#03045e");
  });
};

// when document becomes ready, it selects and highlights
// the first feature navigation option
$(document).ready(function () {
  var firstFeatureId = features[0].id
  document.querySelector("li a[href='#" + features[0].id + "']").parentNode.classList.add("side-nav-item-active");
  loadScenarioContent("#" + firstFeatureId);

});

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

// jquery to click and open any image in fullscreen view
// pressing escape will close image
// $(document).ready(function () { $("img").click(function () { this.requestFullscreen() }) });
$('img').on("click", function () {
  var win = window.open();
  var url = $(this).attr("src");
  // var html = $("body").html("<textarea>" + url + "</textarea>");
  var html = $("body").html("<img src='" + url + "' target='_blank'");
  $(win.document.body).html(html);
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

// this is used to configure options for lightbox js library
// that helps to open images smoothly on a pop-up
// refer "https://lokeshdhakar.com/projects/lightbox2/" for more details
// To make it work, added js <script> tag and css <link> tags in the html file
lightbox.option({
  'resizeDuration': 600,
  'wrapAround': true
})
