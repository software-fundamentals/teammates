# High complexity functions documentation

## equals
### Location of function
The function can be found at [here](https://github.com/software-fundamentals/teammates/blob/master/src/main/java/teammates/common/datatransfer/attributes/FeedbackQuestionAttributes.java).

### Description
The equals function checks if two objects of type `FeedBackQuestionAttributes` are equal. We call the object which calls the function A and the object passed as parameter for B. First of all, the function checks if the object A is object B, then true is returned. Then it checks if object B is null, then false is returned. Then each paramter is checked respectively. If any of object B's or object A's paramters is null or not equal to one another, false is returned. If all checks passes, true is returned.

### Complexity
The Lizard tool as well as our by hand calculations resulted in a complexity of 30 which is quite high. This is due to the fact that the function compares two objects checking each attribute in an if statement. A possible improvement is to move each attribute check to a separate function, that would also make testing easier.

## getRecipientsForQuestion
### Location of function
The function can be found [here](https://github.com/software-fundamentals/teammates/blob/master/src/main/java/teammates/logic/core/FeedbackQuestionsLogic.java)

### Description
The function checks the recipientType of the parameter "question" and returns all recipients for the question as a map "recipients" with email as key and name as value.

### Complexity
My own calculations, and the lizard tool, found the cyclomatic complexity to be 19.
Eight new branches are created for the eight recipientTypes checked (SELF, STUDENTS, INSTRUCTORS, etc) in the switch statement. In addition, most cases include a foreach loop and an if statement. Several of these cases do essentially the same thing: loop through a recipient list and add the recipient to "recipients" if the recipient isn't the question giver. So there's potential to reduce the complexity of the function, for example by creating a helper function.
