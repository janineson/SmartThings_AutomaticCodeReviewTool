/**
 * Created by Janine on 2017-12-18.
 */
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class Parser{

    int reliabilityViolationCount, securityViolationCount, maintainabilityViolationCount
    def cyclomaticComplexity, abcMetric, methodCount, methodSize
    Logger log
    ArrayList<String> ruleViolationList

    public Parser(Logger logger) {

         reliabilityViolationCount = 0
         securityViolationCount = 0
         maintainabilityViolationCount= 0
         cyclomaticComplexity= 0
         abcMetric= 0
         log = logger
         ruleViolationList = new ArrayList()

    }

    ArrayList<String> reliabilityList = new ArrayList<String>(
            Arrays.asList("AvoidRecurringShortSchedules", "NoBusyLoop", "NoSynchronized",
                    "AvoidChainedRunInCall", "UnusedArray", "UnusedObject",
                    "UseConsistentReturnValue", "VerifyArrayIndex", "HandleNullValue","MissingSwitchDefault",
                    "AssignmentInConditional", "BitwiseOperatorInConditional", "BrokenNullCheck," ,
                    "BrokenOddnessCheck", "ComparisonOfTwoConstants",  "ComparisonWithSelf",
                    "ConstantIfExpression", "ConstantTernaryExpression", "DuplicateMapKey",
                    "DuplicateSetValue", "EmptyCatchBlock", "EmptyElseBlock", "EmptyFinallyBlock",
                    "EmptyForStatement", "EmptyIfStatement", "EmptyMethod", "EmptySwitchStatement",
                    "EmptyTryBlock", "EmptyWhileStatement", "RandomDoubleCoercedToZero", "ReturnFromFinallyBlock",
                    "ThrowExceptionFromFinallyBlock", "ParameterReassignment", "MethodSize", "MethodCount",
                    "NoMissingEventHandler", "SingleArgEventHandler", "NoGlobalVariable", "AtomicStateUsage",
                    "AtomicStateUpdateUsage", "DeadCode")

    );
    ArrayList<String> maintainabilityList = new ArrayList<String>(
            Arrays.asList("SeparateParentChildApp", "CyclomaticComplexity", "NestedBlockDepth","AbcMetric",
                    "ConfusingTernary", "CouldBeElvis", "IfStatementCouldBeTernary", "InvertedIfElse",
                    "TernaryCouldBeElvis", "ForLoopShouldBeWhileLoop", "DoubleNegative")
    );
    ArrayList<String> securityList = new ArrayList<String>(
            Arrays.asList("DocumentExternalHTTPRequests", "DocumentExposedEndpoints", "ClearSubscription",
                    "SpecificSubscription", "NoDynamicMethodExecution", "NoHardcodeSMS", "NoRestrictedMethodCalls")
    );

    void process(String obj) {
        def ruleName, rulePriority, ruleLineNo, ruleSource, linesOfCode
        String[] fileRuleSource

        Document doc = Jsoup.parse(obj)

        Elements divs = doc.select('div.summary');

        for (Element div : divs) {
            Iterator<Element> headerIterator = div.select("h3").iterator() //header

            while(headerIterator.hasNext()){
                log.append("FILENAME : "+headerIterator.next().text())

                Iterator<Element> tableIterator = div.select("td").iterator()

                while (tableIterator.hasNext()) {

                    //violations and priority num
                    ruleName = tableIterator.next().text()
                    rulePriority = tableIterator.next().text()
                    ruleLineNo = tableIterator.next().text()
                    ruleSource = tableIterator.next().text()
                    fileRuleSource = ruleSource.split(" ")

                    /* Checks the cyclomatic complexity for methods/classes. A method
                    (or "closure field") with a cyclomatic complexity value greater
                    than the maxMethodComplexity property (20) causes a violation.
                    Likewise, a class that has an (average method) cyclomatic complexity
                    value greater than the maxClassAverageMethodComplexity property (20) causes a violation.
                     */
                    if (ruleName == "CyclomaticComplexity")
                        cyclomaticComplexity = fileRuleSource[fileRuleSource.length - 1] //position of the value in the source line message
                    /*Checks the ABC size metric for methods/classes. A method (or "closure field")
                     with an ABC score greater than the maxMethodAbcScore property (60) causes a violation.
                     Likewise, a class that has an (average method) ABC score greater than the
                     maxClassAverageMethodAbcScore property (60) causes a violation.*/
                    if (ruleName == "AbcMetric")
                        abcMetric = fileRuleSource[fileRuleSource.length - 1]
                    /*A class with too many methods is probably a good suspect for refactoring,
                    in order to reduce its complexity and find a way to have more fine grained
                    objects.The maxMethods property (30) specifies the threshold.
                     */
                    if (ruleName == "MethodCount")
                        methodCount = fileRuleSource[fileRuleSource.length - 2]
                    //Checks if the size of a method exceeds the number of lines specified by the maxLines property (100).
                    if (ruleName == "MethodSize")
                        methodSize = fileRuleSource[fileRuleSource.length - 2]

                    if (ruleName == "TotalLinesOfCode")
                        linesOfCode = fileRuleSource[fileRuleSource.length - 2]
                    else{
                        log.append("rule name : " + ruleName + " line : " + ruleLineNo)
                        log.append("source line/ message : " + ruleSource)

                        ruleViolationList.add(ruleName)
                    }

                }

                log.append("lines of code : " + linesOfCode)
                displayQualityAttribute(linesOfCode)
                resetCount()

            }

        }

    }

    void resetCount(){
        reliabilityViolationCount = 0
        securityViolationCount = 0
        maintainabilityViolationCount = 0

        ruleViolationList = new ArrayList()

        cyclomaticComplexity = 0
        abcMetric = 0
    }
    void defectCount(){

        for (String rule : ruleViolationList) {
            if(reliabilityList.contains(rule)){
                reliabilityViolationCount++
            }

            if(securityList.contains(rule)){
                securityViolationCount++
            }

            if(maintainabilityList.contains(rule)){
                maintainabilityViolationCount++
            }
        }

    }

    Float calculateRate(int count, def linesOfCode){

        return (count / Integer.parseInt(linesOfCode)) * 100
    }

    void metrics(){
        boolean noViolation = true

        for (String rule : reliabilityList) {
            if (ruleViolationList.count(rule) > 0){
                if (rule == 'MethodCount')
                    log.append(rule + " : "  + methodCount)
                else if (rule == 'MethodSize')
                    log.append(rule + " : "  + methodSize)
                else
                    log.append(rule + " : "  + ruleViolationList.count(rule))
                noViolation = false
            }
        }

        for (String rule : securityList) {
            if (ruleViolationList.count(rule) > 0){
                log.append(rule + " : "  + ruleViolationList.count(rule))
                noViolation = false
            }

        }

        for (String rule : maintainabilityList) {
            if (ruleViolationList.count(rule) > 0){
                if (rule == 'CyclomaticComplexity')
                    log.append(rule + " : "  + cyclomaticComplexity)
                else if (rule == 'AbcMetric')
                    log.append(rule + " : "  + abcMetric)
                else
                    log.append(rule + " : "  + ruleViolationList.count(rule))
                noViolation = false
            }
        }

        if (noViolation)
            log.append("NONE")
    }

    void displayQualityAttribute(def linesOfCode){
        log.append("---Code Defect Rate---")
        defectCount()

        log.append("Reliability - " + calculateRate(reliabilityViolationCount, linesOfCode).round(2) + "%")
        log.append("Security - "  + calculateRate(securityViolationCount, linesOfCode).round(2)  + "%")
        log.append("Maintainability - "  + calculateRate(maintainabilityViolationCount, linesOfCode).round(2)  + "%")

        log.append("---Violation Metrics---")
        metrics()
        log.append(" ")
    }

}