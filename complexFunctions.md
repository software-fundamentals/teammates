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

## isResponseVisibleForUser
The function can be found [here](https://github.com/software-fundamentals/teammates/blob/master/src/main/java/teammates/logic/core/FeedbackSessionsLogic.java)

### Description
The function checks if a response to a specific question is visible to a particular user specified by email and role. First there are 6 if statements with 12 boolean conditions that can set the varaible `isVisibleResponse` to true. An example of such is if the user is an instructor and the response is visible to users of type instructor. Then follows another if statement that can set `isVisibleResponse` to false again if the user is an instructor and it doesn't have the correct privilege etc.

### Complexity
The lizard tool found a cyclomatic complexity of 24 due to the fact that there is 8 statements and 14 boolean conditions making it a complex function. Since a lot of the if statements are nested and got many boolean conditions, it's difficault to get an overview of the function. I do believe that everything that is checked has to be checked but I think that it should be separated into different functions. For instance, if the user is an instructor, another function that checks all the instructor related variables could be called.