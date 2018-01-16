# Code Review Metrics Tool for SmartThings
<i>Software Language Lab, Computer Science, Sookmyung Women’s University</i>

This is a metrics tool analyzer based on the HTML report generated by CodeNarc after running to automate the code review for SmartApps.
It generates the Defect Density Metrics per quality attribute - Reliability, Maintainability, and Security of the SmartApps based on the detected violations from CodeNarc custom rules.<br/>
It also has a summary at the bottom part of the report (text file).

# How to Run
You only need the HTML file generated from CodeNarc to run this program.<br/>
To generate the HTML report, you would need to do a few things.<br/>
You need to run another program located in a different repository (CodenarcPluginRunTest).<br/>
It is a program that contains groovy SmartApps and they are run using CodeNarc Plugin for IntellijIDEA IDE. <br/>
You need to run an ANT build file that is configured for CodeNarc and it will generate an HTML report after analyzing a bunch of SmartApps all at once.<br/>
Place the HTML file under out/dump_html folder.<br/>
Run the program.<br/>
A text file will be generated in the root folder of the source program.<br/>
<img src="https://github.com/janineson/SmartThings_CodeReviewMetricsTool/blob/master/out/scrnshot.PNG"> 

# Required Libraries
And by the way, you need the following libraries to make this run!<br/>
jsoup-1.11.1.jar<br/>
java jdk 1.8<br/>
groovy 2.4.7<br/>

