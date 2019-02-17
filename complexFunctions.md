# High complexity functions documentation

## isFeedbackParticipantNameVisibleToUserCoverage
The function can be found at https://github.com/software-fundamentals/teammates/blob/master/src/main/java/teammates/logic/core/FeedbackResponsesLogic.java

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