var json = `<<json-input>>`;
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
} else if (overallStatus == "failed with deferred issue") {
  // overAllStatusInnerElement = `<div class="over-all-highlight" style="background-color:#FFC300 ; color:#008000; font-weight:bold;">${overallStatus.toUpperCase()}</div>`;
  overAllStatusInnerElement = `<div class="over-all-highlight" style="background-color:#74c69d ; color:white; font-weight:bold;">RESULT CONTAINS BOTH PASSED AND KNOWN FAILURES</div>`;
} else {
  overAllStatusInnerElement = `<div class="over-all-highlight" style="background-color:#C0C0C0 ; color:white; font-weight:bold;">No Results Found</div>`;
}

overallStatusParentElement.innerHTML = overAllStatusInnerElement;

// project info section
var projectInfo = data.projectInfo;

let projectInfoFlexParentElemnt = document.querySelector("#project-info-flex");
var projectInfoGridInnerElement = " ";

projectInfoGridInnerElement = `

<div class="info-section info-section-a">
  <div class="project-info-element project-info-key">Environment</div>
  <div class="project-info-element project-info-separator">:</div>
  <div class="project-info-element project-info-value">${projectInfo.environment}</div>

  <div class="project-info-element project-info-key">Browser</div>
  <div class="project-info-element project-info-separator">:</div>
  <div class="project-info-element project-info-value">${projectInfo.browser}</div>

  <div class="project-info-element project-info-key">Application</div>
  <div class="project-info-element project-info-separator">:</div>
  <div class="project-info-element project-info-value">${projectInfo.appName}</div>

  <div class="project-info-element project-info-key last-row">OS</div>
  <div class="project-info-element project-info-separator">:</div>
  <div class="project-info-element project-info-value">${projectInfo.os}</div>
</div>

<div class="info-section info-section-b">
  <div class="project-info-element project-info-key">Start Time</div>
  <div class="project-info-element project-info-separator">:</div>
  <div class="project-info-element project-info-value">${projectInfo.startTime}</div>

  <div class="project-info-element project-info-key">End Time</div>
  <div class="project-info-element project-info-separator">:</div>
  <div class="project-info-element project-info-value">${projectInfo.endTime}</div>

  <div class="project-info-element project-info-key">Elapsed Time</div>
  <div class="project-info-element project-info-separator">:</div>
  <div class="project-info-element project-info-value">${projectInfo.totalDuration}</div>

  <div class="project-info-element project-info-key"></div>
  <div class="project-info-element project-info-separator"></div>
  <div class="project-info-element project-info-value"></div>

</div>

<div class="info-section info-section-b">
  <div class="project-info-element project-info-key">Release Name</div>
  <div class="project-info-element project-info-separator">:</div>
  <div class="project-info-element project-info-value">${projectInfo.releaseName}</div>

  <div class="project-info-element project-info-key">Release Date</div>
  <div class="project-info-element project-info-separator">:</div>
  <div class="project-info-element project-info-value">${projectInfo.releaseDate}</div>

  <div class="project-info-element project-info-key">Sprint Name</div>
  <div class="project-info-element project-info-separator">:</div>
  <div class="project-info-element project-info-value">${projectInfo.sprint}</div>

  <div class="project-info-element project-info-key"></div>
  <div class="project-info-element project-info-separator"></div>
  <div class="project-info-element project-info-value"></div>

</div>

<div class="info-section info-section-c">
  <div class="project-info-element project-info-key">Project Manager</div>
  <div class="project-info-element project-info-separator">:</div>
  <div class="project-info-element project-info-value">${projectInfo.projectManager}</div>

  <div class="project-info-element project-info-key">DQ Manager</div>
  <div class="project-info-element project-info-separator">:</div>
  <div class="project-info-element project-info-value">${projectInfo.dqManager}</div>

  <div class="project-info-element project-info-key">DQ Lead</div>
  <div class="project-info-element project-info-separator">:</div>
  <div class="project-info-element project-info-value">${projectInfo.dqLead}</div>

  <div class="project-info-element project-info-key"></div>
  <div class="project-info-element project-info-separator"></div>
  <div class="project-info-element project-info-value"></div>
</div>



`;
projectInfoFlexParentElemnt.innerHTML = projectInfoGridInnerElement;

// project description section
let projectDescInfoParentElemnt = document.querySelector("#project-desc-info");
var projectDescInfoInnerElement = " ";

projectInfoGridInnerElement = `
<div class="description-section info-section-d">
  <div class="project-info-element project-info-key project-desc-header">Project Description / Release Notes :</div>
  <div class="project-info-element project-info-value project-desc-value">${projectInfo.descriptionOrReleaseNotes}</div>
</div>
`;

projectDescInfoParentElemnt.innerHTML = projectInfoGridInnerElement;


// Load pie charts for feature, test case and test steps on loading the page
// refer Chart.js library : https://www.chartjs.org/docs/latest/samples/other-charts/doughnut.html

// feature pie chart
// CenterText Plugin
const featureCenterText = {
  afterDatasetsDraw(chart) {
    const { ctx } = chart;
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
  "Failed - New Issue",
  "Failed - Deffered Issue",
  "Skipped"
];

let featurePieDataSet = [
  data.featurePieChartDataMap.passed,
  data.featurePieChartDataMap.failed,
  data.featurePieChartDataMap.failedWithDeferredIssue,
  data.featurePieChartDataMap.skipped
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
          '#FFC300',
          '#C0C0C0'
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
// CenterText Plugin
const testCaseCenterText = {
  afterDatasetsDraw(chart) {
    const { ctx } = chart;
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

let testCaseLabels = [
  "Passed",
  "Failed - New Issue",
  "Failed - Deferred Issue",
  "Skipped"
];
let testCasePieDataSet = [
  data.testCasePieChartDataMap.passed,
  data.testCasePieChartDataMap.failed,
  data.testCasePieChartDataMap.failedWithDeferredIssue,
  data.testCasePieChartDataMap.skipped
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
          '#FFC300',
          '#C0C0C0'
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
// CenterText Plugin
const testStepCenterText = {
  afterDatasetsDraw(chart) {
    const { ctx } = chart;
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

let testStepLabels = [
  "Passed",
  "Failed - New Issue",
  "Failed - Deferred Issue",
  "Skipped",
  "Pending",
  "Undefined",
  "Ambiguous",
  "Unused"
];
let testStepPieDataSet = [
  data.testStepPieChartDataMap.passed,
  data.testStepPieChartDataMap.failed,
  data.testStepPieChartDataMap.failedWithDeferredIssue,
  data.testStepPieChartDataMap.skipped,
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
          '#FFC300',
          '#C0C0C0',
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

// const stepDoughnutChart = new Chart(
//   document.querySelector("#testStepePieChart"),
//   stepChartConfig
// )

// defect pie chart
const reducer = (accumulator, curr) => accumulator + curr;
var totalDefects = data.defectPieChartDataMap.deferredDefects
  + data.defectPieChartDataMap.newDefects;
totalDefects = totalDefects > 0 ? totalDefects : 'No Defects';

// CenterText Plugin
const defectCenterText = {
  afterDatasetsDraw(chart) {
    const { ctx } = chart;
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

let defectCategoryLabel = ["Deferred Defect", "New Defect"];
let defectPieDataSet = [
  data.defectPieChartDataMap.deferredDefects,
  data.defectPieChartDataMap.newDefects
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
          '#ff758f',
          '#d90429'
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
      name: "Fail",
      columns: [
        {
          id: "failed",
          name: "New"
        },
        {
          id: "failedWithDeferredIssue",
          name: "Deferred"
        }
      ]
    },
    {
      id: "skipped",
      name: "Skip"
    },
    {
      id: "passPercent",
      name: "Pass %"
    },
    {
      name: "Fail %",
      columns: [
        {
          id: "failPercent",
          name: "New"
        },
        {
          id: "failedWithDeferredIssuePercent",
          name: "Deferred"
        }
      ]
    },
    {
      id: "skippedPercent",
      name: "Skip %"
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
        } else if (cell == "failed with deferred issue") {
          return {
            'class': 'gridjs-td known-failures-status'
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
      name: "Fail",
      columns: [
        {
          id: "failed",
          name: "New"
        },
        {
          id: "failedWithDeferredIssue",
          name: "Deferred"
        }
      ]
    },
    {
      id: "skipped",
      name: "Skip"
    },
    {
      id: "passPercent",
      name: "Pass %"
    },
    {
      name: "Fail %",
      columns: [
        {
          id: "failPercent",
          name: "New"
        },
        {
          id: "failedWithDeferredIssuePercent",
          name: "Deferred"
        }
      ]
    },
    {
      id: "skippedPercent",
      name: "Skip %"
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
        } else if (cell == "failed with deferred issue") {
          return {
            'class': 'gridjs-td known-failures-status'
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
  // language: {
  //   'search': {
  //     'placeholder': '🔍 Search...'
  //   },
  //   'pagination': {
  //     'previous': '⬅️',
  //     'next': '➡️',
  //     'showing': '😃 Displaying',
  //     'results': () => 'Records'
  //   }
  // },
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

const replaceAccentsChars = (str, charWith = '-', regexType = 'NO_SPECIAL') => {

  if (!str) return

  const REGEX_TYPE = {
    'ALL': / /g,
    'NO_SPECIAL': /[^A-Z0-9]/ig,
    'SINGLE_FOR_MULTI': /[^A-Z0-9]+/ig,
  }

  return str.replace(REGEX_TYPE[regexType], charWith).toLowerCase()
}

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

function getFeatureElements(features) {
  var featureListElement = "";

  for (let feature of features) {
    var statusCell = getFeatureNavigationLineStatusColor(feature.status);
    var featureId = replaceAccentsChars(feature.id, '', 'SINGLE_FOR_MULTI');

    // prepare left side features link as li
    featureListElement += `
    <li class="side-nav-item">
      <a
        id="${featureId}-a"
        href="#${featureId}"
        class="featureLinks"
        onclick='loadScenarioContent("#${featureId}")'>
        ${atob(feature.name)}
      </a>
      <span>${feature.duration}</span>
      ${statusCell}
    </li>
    `;

  }
  return featureListElement;
}

function getContentToDisplayOnRightWhenClickingFeatureNavLink(features) {
  var displayOnClickContent = "";
  for (let feature of features) {
    // get all scenario elements to be displayed on right side page
    var scenarioElements = getScenarioElements(feature.scenarios);
    var featureId = replaceAccentsChars(feature.id, '', 'SINGLE_FOR_MULTI');

    // then build the content panel section with above scenario elements
    // for every feature
    displayOnClickContent += `
      <div id="${featureId}" class="displayOnClick">
        ${scenarioElements}
      </div>
      `;
  }
  return displayOnClickContent;
}

function getScenarioElements(scenarios) {
  var scenarioElements = "";

  for (let scenario of scenarios) {
    var steps = scenario.steps;
    var stepElements = getStepElements(steps);
    var scLineColor = getScenarioLineColorBasedOnStatus(scenario.status);
    var scenarioId = replaceAccentsChars(scenario.id, '', 'SINGLE_FOR_MULTI') + "-" + createUUID();

    var beforeScenarioError = "";
    if (scenario.beforeError.length != 0) {
      var beforeScenarioErrorDecoded = atob(scenario.beforeError);
      beforeScenarioError = `
        <div class="scenario-hook-section">
          <div class="scenario-hook-error-key">${scenario.scenarioSeq} BeforeScenario-Hook-Error : </div>
          <div class="scenario-hook-error-value">${beforeScenarioErrorDecoded}</div>
        </div>
      `;
    }

    var afterScenarioError = "";
    if (scenario.afterError.length != 0) {
      var afterScenarioErrorDecoded = atob(scenario.afterError);
      afterScenarioError = `
        <div class="scenario-hook-section">
          <div class="scenario-hook-error-key">${scenario.scenarioSeq} AfterScenario-Hook-Error : </div>
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
            <div ${scLineColor}>${atob(scenario.name)}</div>
            <span class="fa-solid fa-angle-down scenario-expand-icon" style="color: #9eb3c2;"></span>
          </div>
          <div id="#${scenarioId}-collapsible-content" class="step-items">
            ${stepElements}
          </div>
        </div>
        ${afterScenarioError}
    </div>
      `;

  }
  return scenarioElements;
}

function getStepElements(steps) {
  var stepElements = "";

  for (let step of steps) {
    var stepLineColor = getStepLineColorBasedOnStatus(step.status);
    var beforeStepError = "";
    if (step.beforeError.length != 0) {
      var beforeStepErrorDecoded = atob(step.beforeError);
      beforeStepError = `
        <div class="step-name">
          <div class="step-hook-error-section">
            <div>${step.stepSeq} BeforeStep-Hook-Error : </div>
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
          <div>${step.stepSeq} AfterStep-Hook-Error : </div>
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
    var stepName = atob(step.name);
    var stepNameForId = replaceAccentsChars(stepName, '', 'SINGLE_FOR_MULTI')  + "-" + createUUID();

    stepElements += `
      <div id="#${stepNameForId}-${step.line}" class="step-item">
        ${beforeStepError}
        <div
          id="#step-${stepNameForId}-${step.line}-collapsible"
          class="step-name" ${stepLineColor}
          onclick="collapseExpandStep('#step-${stepNameForId}-${step.line}-collapsible-content')">
            <div>${stepName}</div>
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
  }
  return stepElements;
}

function getStepEmbeddingElements(embeddings, stepStatus) {
  var stepEmbeddingElement = "";
  let index = 0;
  if (embeddings != null && embeddings.length > 0) {
    for (let embedding of embeddings) {
      index = index + 1;
      var embeddingName = atob(embedding.name);
      var attachmentSource = getStepScreenshotElement(embedding.mime_type, embedding.data, embeddingName, index);
      if (isStepStatusIngnorable(stepStatus)) {
        stepEmbeddingElement += `
          <div>
          <div class="embedding-name">${embeddingName}</div>
          <div class="embedding-src">${attachmentSource}</div>
          <div class="embedding-name">${stepStatus} step</div>
          </div>
        `;
      } else {
        stepEmbeddingElement += `
          <div>
            <div class="embedding-name">${embeddingName}</div>
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
  // var firstFeatureId = features[0].id;
  var firstFeatureId = replaceAccentsChars(features[0].id, '', 'SINGLE_FOR_MULTI');

  document.querySelector("li a[href='#" + firstFeatureId + "']").parentNode.classList.add("side-nav-item-active");
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

function collapseExpandDefect(id) {

  if (document.getElementById(id).style.display == "flex") {
    document.getElementById(id).style.display = "none";
  } else {
    document.getElementById(id).style.display = "flex";
  }
}

function createUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

function getFeatureNavigationLineStatusColor(featureStatus) {
  var statusCell = "";
  if (featureStatus == "passed") {
    statusCell = `<i class="fa-solid fa-circle-check status-icon" style="color: #0e920c;"></i>`;
  } else if (featureStatus == "failed") {
    statusCell = `<i class="fa-solid fa-circle-xmark status-icon" style="color: #f20202;"></i>`;
  } else if (featureStatus == "skipped") {
    statusCell = `<i class="fa-solid fa-circle-arrow-right status-icon" style="color: #c3c6cb;"></i>`;
  } else if (featureStatus == "failed with deferred issue") {
    statusCell = `<i class="fa-solid fa-circle-exclamation status-icon" style="color: #f0cf75;"></i>`;
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
  } else if (stepStatus == "failed with deferred issue") {
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
  } else if (scenarioStatus == "failed with deferred issue") {
    scLineColor = "style='color: #FFC300'";
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

// Defect Details Section
var defectDetails = data.defectDetails;

// it consist of whole elements
var defectDetailsSectionParentElement = document.querySelector("#defect-details-section");

var defectDetailsSectionContainerElement = `

  <div
    class="defect-category deferred-failures"
    id="tracked-known-failures"
    onclick="collapseExpandDefect('deferred-failures-list-collapsible-content')">
      <div>Deferred Defects</div>
      <span class="fa-solid fa-angle-down scenario-expand-icon" style="color: black;"></span>
  </div>
  <div class="deferred-failures-list" id="deferred-failures-list-collapsible-content">
    ${getTrackedKnownFailureSection()}
  </div>





  <div
    class="defect-category new-failures"
    onclick="collapseExpandDefect('new-failures-list-collapsible-content')">
      <div>New Defects</div>
      <span class="fa-solid fa-angle-down scenario-expand-icon" style="color: black;"></span>
  </div>
  <div class="new-failures-list" id="new-failures-list-collapsible-content">
    ${getNewFailureSection()}
  </div>



  `;

defectDetailsSectionParentElement.innerHTML = defectDetailsSectionContainerElement;

function getTrackedKnownFailureSection() {
  var knownDefectIds = data.defectDetails.trackedKnownDefectIds;
  var knownFailures = data.defectDetails.knownFailures;

  var knownDefectIdItem = "";
  var i = 1
  for (let defectId of knownDefectIds) {
    var knownDefectIdRow = "";
    for (let knownFail of knownFailures) {
      if (knownFail.trackingId == defectId) {
        knownDefectIdRow += `

          <div class="deferred-defect-item-row">
            <div>${atob(knownFail.featureName)}</div>
            <div>${atob(knownFail.scenarioName)}</div>
            <div>${atob(knownFail.stepName)}</div>
            <div>${knownFail.label}</div>
            <div>${atob(knownFail.failureMessage)}</div>
          </div>

          `;
      }
    }
    knownDefectIdItem += `
      <div>
        <div class="deferred-defect-id-header">${i} . ${defectId}</div>
        ${knownDefectIdRow}
      </div>
    `;
    i++;

  }

  var knownDefectsBox = "";
  if(knownFailures.length == 0) {
    knownDefectsBox = `
    <div class="no-defects-found">No Known Failures Identified</div>
    `;
  } else {
    knownDefectsBox = `

    <div class="deferred-defect-item-header">
      <div>Feature</div>
      <div>Scenario</div>
      <div>Step</div>
      <div>Assert Label</div>
      <div>Error Details</div>
    </div>
    ${knownDefectIdItem}
    `;
  }

  return knownDefectsBox;
}

function getNewFailureSection() {
  var newFailures = data.defectDetails.newFailures;


  var newDefectIdRow = "";
  var i = 1;
  for (let newFail of newFailures) {
    newDefectIdRow += `

          <div class="new-defect-item-row">
            <div class="defect-feature-name-column"><div>${i}. </div>${atob(newFail.featureName)}</div>
            <div>${atob(newFail.scenarioName)}</div>
            <div>${atob(newFail.stepName)}</div>
            <div>${newFail.label}</div>
            <div>${atob(newFail.failureMessage)}</div>
          </div>

          `;
          i++;
  }

  var newDefectsBox = "";
  if(newFailures.length == 0) {
    newDefectsBox = `
      <div class="no-defects-found">No New Failures Identified</div>
    `;
  } else {
    newDefectsBox = `

    <div class="new-defect-item-header">
      <div>Feature</div>
      <div>Scenario</div>
      <div>Step</div>
      <div>Assert Label</div>
      <div>Error Details</div>
    </div>
    ${newDefectIdRow}
    `;
  }

  return newDefectsBox;
}