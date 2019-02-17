# High complexity functions documentation

## isFeedbackParticipantNameVisibleToUserCoverage
### Location of function
The function can be found [here](https://github.com/software-fundamentals/teammates/blob/master/src/main/java/teammates/logic/core/FeedbackResponsesLogic.java)

### Description
The function evaluates for an array of feedback participants whether their names should be presented or not to the user.
Depending on the participants 'types' e.g. instructor, teammate, student, this function works through a switch statement
were the types are the different cases. Moreover within these cases, the function does a few if statements as a final
verification before deciding whether to present the participant name to the user or not.

### Complexity
The Lizard tool as well as our by hand calculations resulted in a complexity of 18. The complexity can largely be explained
due to the multitude of switch cases for every possible type (7 to be exact). The majority of the rest emerge from
different if statements within the cases. The complexity can be reduced by extracting code from within these statements
to helper functions which would lower the branching. However, the code itself is quite clean and not very messy, thus
refactoring the code in this way could arguably lead to a function that might be harder to understand from the get-go.

## getRecipientsForQuestion
### Location of function
The function can be found [here](https://github.com/software-fundamentals/teammates/blob/master/src/main/java/teammates/logic/core/FeedbackQuestionsLogic.java)

### Description
The function checks the recipientType of the parameter "question" and returns all recipients for the question as a map "recipients" with email as key and name as value.

### Complexity
My own calculations, and the lizard tool, found the cyclomatic complexity to be 19.
Eight new branches are created for the eight recipientTypes checked (SELF, STUDENTS, INSTRUCTORS, etc) in the switch statement. In addition, most cases include a foreach loop and an if statement. Several of these cases do essentially the same thing: loop through a recipient list and add the recipient to "recipients" if the recipient isn't the question giver. So there's potential to reduce the complexity of the function, for example by creating a helper function.

## isResponseVisibleForUser
### Location of function
The function can be found [here](https://github.com/software-fundamentals/teammates/blob/master/src/main/java/teammates/logic/core/FeedbackSessionsLogic.java)

### Description
The function checks if a response to a specific question is visible to a particular user specified by email and role. First there are 6 if statements with 12 boolean conditions that can set the varaible `isVisibleResponse` to true. An example of such is if the user is an instructor and the response is visible to users of type instructor. Then follows another if statement that can set `isVisibleResponse` to false again if the user is an instructor and it doesn't have the correct privilege etc.

### Complexity
The lizard tool found a cyclomatic complexity of 24 due to the fact that there is 8 statements and 14 boolean conditions making it a complex function. Since a lot of the if statements are nested and got many boolean conditions, it's difficault to get an overview of the function. I do believe that everything that is checked has to be checked but I think that it should be separated into different functions. For instance, if the user is an instructor, another function that checks all the instructor related variables could be called.

## validateQuestionDetails
### Location of function
The function can be found [here](https://github.com/software-fundamentals/teammates/blob/master/src/main/java/teammates/common/datatransfer/questions/FeedbackMcqQuestionDetails.java)

### Description
The purpose of this function is to validate the questions provided by the participants during the feedback.
In essence the function goes through the potential errors that can arise when creating a question as such. Using
and array called 'Errors', every invalid input the functions finds results in an error message that are added to this
array and then returned. If the error array has any such messages in it - it will be handled in another function.

### Complexity
The Lizard tool as well as our by hand calculations resulted in a complexity of 17. The complexity arises to the multitude
of if-statements in the function. Also due to the several uses of && and || operators that count towards the CCN.
The if-statements could be refactored in order to reduce the complexity, for example, the if statements that concerns the
case when the variable 'weights' are enabled could belong to one helper function and when 'weights' are disable could belong
to another.
