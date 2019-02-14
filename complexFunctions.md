# High complexity functions documentation

## Function name
### Location of Function

### Description

### Complexity


## getRecipientsForQuestion
### Location of function
The function can be found [here](https://github.com/software-fundamentals/teammates/blob/master/src/main/java/teammates/logic/core/FeedbackQuestionsLogic.java)

### Description
The function checks the recipientType of the parameter "question" and returns all recipients for the question as a map "recipients" with email as key and name as value.

### Complexity
My own calculations, and the lizard tool, found the cyclomatic complexity to be 19.
Eight new branches are created for the eight recipientTypes checked (SELF, STUDENTS, INSTRUCTORS, etc) in the switch statement. In addition, most cases include a foreach loop and an if statement. Several of these cases do essentially the same thing: loop through a recipient list and add the recipient to "recipients" if the recipient isn't the question giver. So there's potential to reduce the complexity of the function, for example by creating a helper function.
