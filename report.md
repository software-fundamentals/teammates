# Report for assignment 3

## Project

Name: TEAMMATES

URL: https://github.com/software-fundamentals/teammates

TEAMMATES is a free online tool for managing peer evaluations and other feedback paths of your students.
It is provided as a cloud-based service for educators/students and is currently used by hundreds of universities across the world.

## Onboarding experience
The whole process of downloading the necessary things and running the frontend and backend respectively was very smooth.
We have tested the onboarding on Windows, macOS and Unix. The setup documentation can be found [here](https://github.com/TEAMMATES/teammates/blob/master/docs/setting-up.md)
and the documentation on how to run the program can be found [here](https://github.com/TEAMMATES/teammates/blob/master/docs/development.md).


## Complexity (complexFunctions.md)
1. What are your results for the ten most complex functions? (If ranking
is not easily possible: ten complex functions)?
   * Did all tools/methods get the same result?
   * Are the results clear?
2. Are the functions just complex, or also long?
3. What is the purpose of the functions?
4. Are exceptions taken into account in the given measurements?
5. Is the documentation clear w.r.t. all the possible outcomes?

## Coverage

### Tools

Document your experience in using a "new"/different coverage tool.

How well was the tool documented? Was it possible/easy/difficult to
integrate it with your build environment?

### DYI

Show a patch that show the instrumented code in main (or the unit
test setup), and the ten methods where branch coverage is measured.

The patch is probably too long to be copied here, so please add
the git command that is used to obtain the patch instead:

git diff b79b7fcc4

What kinds of constructs does your tool support, and how accurate is
its output?

### Evaluation

Report of old coverage: [link]

Report of new coverage: [link]

Test cases added:

git diff ...

## Refactoring

Plan for refactoring complex code:

Carried out refactoring (optional)

git diff ...

## Effort spent

For each team member, how much time was spent in

1. plenary discussions/meetings;
    All team members: 6 hours were spent working/discussing/planing at plenary sessions

2. discussions within parts of the group;
    William - 20 min
    Moa -
    Sebastian -
    Josefin -
    Miguel -

3. reading documentation;
    William - 1 hour
    Moa -
    Sebastian -
    Josefin - 2 hours
    Miguel -

4. configuration;
    William - 1 hour
    Moa -
    Sebastian -
    Josefin - 30 minutes
    Miguel -

5. analyzing code/output;
    William - 3 hours
    Moa -
    Sebastian -
    Josefin - 2 hours
    Miguel -

6. writing documentation;
    William - 1 hour
    Moa -
    Sebastian -
    Josefin - 1.5 hours
    Miguel -

7. writing code;
    William - 1 hour
    Moa -
    Sebastian -
    Josefin - 2 hours
    Miguel -

8. running code?
    William - only some test suites which were almost instantaneous
    Moa -
    Sebastian -
    Josefin - same as William
    Miguel -

## Overall experience

What are your main take-aways from this project? What did you learn?

Overall the project was highly illuminating in the sense that we learnt a lot that we haven't really worked with before
but still recon is highly useful as a software developer. Being able to read, understand and code in large-scale projects
is a skill that we all value and also how we learnt to use several tools to analyze and improve code we found very rewarding.

More concretely, we learnt how to set up an existing large-scale project on our personal computers and how to make it run
with its tools and development environment. We learned how to use lizard as a way of analyzing software code and to draw
ideas of how this data may be used to improve our code. Further, we learned how to read and understand complex pieces of
code and stretch our minds in order to grasp the bigger picture and how interconnected components relate in order to be
able to modify and add to the existing code. Moreover, how to analyze existing tests and how to implement our own in
alignment with the existing code and practices being used. Lastly, we learned how to manual check for branch coverage using
the neat method provided with the assignment (filling an array depending on branch coverage) and thus how to find
weak spots in the present tests.

It was a useful project and we will most likely draw benefit from these bits of knowledge we've gained in our
future journeys as software-developers.

