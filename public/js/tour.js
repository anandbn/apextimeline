// Define the tour!
var tour_no_auth = {
  id: "apex-timeline",
  steps: [
    {
      title: "Login to Salesforce",
      content: "Click here to see the last 100 debug logs",
      target: "login",
      placement: "bottom"
    },
    {
      title: "Minimun Run time",
      content: "Select the minimum run time to display. Any operation below this time will not be displayed.",
      target:  "minRunTimeDiv",
      placement: "bottom"
    },
    {
        title: "Upload your log file",
        content: "Upload a debug log file to analyze",
        target: "logFile",
        placement: "right"
    },
    {
        title: "Click here to analyze",
        content: "Click to analyze the debug log and display timeline, SOQL and DML operation summaries",
        target: "showtimeline",
        placement: "right"
    }
  ]
};

// Start the tour!
hopscotch.startTour(tour_no_auth);