# High complexity functions documentation

## Function name
### Location of Function

### Description

### Complexity


## 3. getRecipientsForQuestion
### src/main/java/teammates/logic/core/FeedbackQuestionsLogic.java:338-410

### Checks the recipientType of parameter question and returns all recipients for the question as a map "recipients" with email as key and name as value.
Eight new branches are created for the eight recipientTypes checked (SELF, STUDENTS, INSTRUCTORS, etc) in the switch statement. In addition, most cases include a foreach loop and an if statement. Several of these cases do essentially the same thing: loop through a recipient list and add the recipient to "recipients" if the recipient isn't the question giver. So there's potential to reduce the complexity of the function, for example by creating a helper function.

### Cyclomatic complexity: 19
