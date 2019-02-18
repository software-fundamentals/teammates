package teammates.test.cases.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.*;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;

/**
 * SUT: {@link FeedbackResponseCommentsLogic}.
 */
public class FeedbackResponseCommentsLogicTest extends BaseLogicTest {

    private static final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();

    @Override
    protected void prepareTestData() {
        // test data is refreshed before each test case
    }

    @BeforeMethod
    public void refreshTestData() {
        dataBundle = getTypicalDataBundle();
        removeAndRestoreTypicalDataBundle();
    }

    @Test
    public void testCreateFeedbackResponseComment() throws Exception {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        FeedbackResponseCommentAttributes[] finalFrc = new FeedbackResponseCommentAttributes[] { frComment };

        ______TS("fail: non-existent course");

        frComment.courseId = "no-such-course";

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> frcLogic.createFeedbackResponseComment(finalFrc[0]));
        assertEquals("Trying to create feedback response comments for a course that does not exist.",
                ednee.getMessage());
        frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        finalFrc[0] = frComment;

        ______TS("fail: giver is not an instructor for the course");

        frComment.commentGiver = "instructor1@course2.com";

        ednee = assertThrows(EntityDoesNotExistException.class, () -> frcLogic.createFeedbackResponseComment(finalFrc[0]));
        assertEquals(
                "User " + frComment.commentGiver + " is not a registered instructor for course " + frComment.courseId + ".",
                ednee.getMessage());
        frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        finalFrc[0] = frComment;

        ______TS("fail: feedback session is not a session for the course");

        frComment.feedbackSessionName = "Instructor feedback session";

        ednee = assertThrows(EntityDoesNotExistException.class, () -> frcLogic.createFeedbackResponseComment(finalFrc[0]));
        assertEquals(
                "Feedback session " + frComment.feedbackSessionName + " is not a session for course "
                        + frComment.courseId + ".",
                ednee.getMessage());

        frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");

        ______TS("typical successful case");

        frComment.setId(null);
        frComment.feedbackQuestionId = getQuestionIdInDataBundle("qn1InSession1InCourse1");
        frComment.feedbackResponseId = getResponseIdInDataBundle("response2ForQ1S1C1", "qn1InSession1InCourse1");

        frcLogic.createFeedbackResponseComment(frComment);
        verifyPresentInDatastore(frComment);

        ______TS("successful case: add duplicate frComment - comment will reuse ID of existing comment and update "
                + "itself");

        FeedbackResponseCommentAttributes actualComment =
                frcLogic.getFeedbackResponseComment(
                        frComment.feedbackResponseId, frComment.commentGiver, frComment.createdAt);

        frComment.commentText = "New Text";
        frcLogic.createFeedbackResponseComment(frComment);

        // verify that ID of duplicate comment has been set by method (uses existing ID of original comment)
        assertEquals(actualComment.getId(), frComment.getId());

        // re-fetch the comment from database
        actualComment = frcLogic.getFeedbackResponseComment(frComment.getId());

        // check whether the comment text has been updated
        assertEquals(frComment.commentText, actualComment.commentText);

        //delete afterwards
        frcLogic.deleteFeedbackResponseCommentById(frComment.getId());
        assertNull(frcLogic.getFeedbackResponseComment(frComment.getId()));
    }

    @Test
    public void testCreateFeedbackResponseComment_invalidCommentGiverType_exceptionShouldBeThrown() {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        frComment.commentGiverType = FeedbackParticipantType.SELF;
        frComment.isCommentFromFeedbackParticipant = true;
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> frcLogic.createFeedbackResponseComment(frComment));
        assertEquals("Unknown giver type: " + FeedbackParticipantType.SELF, ednee.getMessage());
    }

    @Test
    public void testCreateFeedbackResponseComment_unknownFeedbackParticipant_exceptionShouldBeThrown() {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        frComment.commentGiverType = FeedbackParticipantType.STUDENTS;
        frComment.isCommentFromFeedbackParticipant = true;
        frComment.commentGiver = "XYZ";
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> frcLogic.createFeedbackResponseComment(frComment));
        assertEquals("User XYZ is not a registered student for course idOfTypicalCourse1.", ednee.getMessage());
    }

    @Test
    public void testCreateFeedbackResponseComment_invalidVisibilitySettings_exceptionShouldBeThrown() {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        frComment.isCommentFromFeedbackParticipant = true;
        frComment.isVisibilityFollowingFeedbackQuestion = false;
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> frcLogic.createFeedbackResponseComment(frComment));
        assertEquals("Comment by feedback participant not following visibility setting of the question.",
                ipe.getMessage());
    }

    @Test
    public void testGetFeedbackResponseComments() {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        List<FeedbackResponseCommentAttributes> expectedFrComments = new ArrayList<>();

        ______TS("fail: invalid parameters");

        frComment.courseId = "invalid course id";
        frComment.commentGiver = "invalid giver email";

        verifyNullFromGetFrCommentForSession(frComment);
        verifyNullFromGetFrComment(frComment);
        frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");

        ______TS("Typical successful case");

        List<FeedbackResponseCommentAttributes> actualFrComments =
                frcLogic.getFeedbackResponseCommentForSession(
                                 frComment.courseId, frComment.feedbackSessionName);
        FeedbackResponseCommentAttributes actualFrComment = actualFrComments.get(0);

        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(frComment.commentGiver, actualFrComment.commentGiver);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);

        ______TS("Typical successful case by feedback response comment details");

        actualFrComment =
                frcLogic.getFeedbackResponseComment(
                                 frComment.feedbackResponseId, frComment.commentGiver, frComment.createdAt);

        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(frComment.commentGiver, actualFrComment.commentGiver);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);

        ______TS("Typical successful case by feedback response id");

        actualFrComments = frcLogic.getFeedbackResponseCommentForResponse(frComment.feedbackResponseId);
        actualFrComment = actualFrComments.get(0);

        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(frComment.commentGiver, actualFrComment.commentGiver);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);

        ______TS("Typical successful case by feedback response comment id");

        actualFrComment = frcLogic.getFeedbackResponseComment(frComment.getId());

        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(frComment.commentGiver, actualFrComment.commentGiver);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);

        ______TS("Typical successful case for giver");

        actualFrComments = frcLogic.getFeedbackResponseCommentsForGiver(
                                            frComment.courseId, frComment.commentGiver);
        FeedbackResponseCommentAttributes tempFrComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        expectedFrComments.add(tempFrComment);
        tempFrComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q2S1C1");
        expectedFrComments.add(tempFrComment);
        tempFrComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q3S1C1");
        expectedFrComments.add(tempFrComment);

        assertEquals(expectedFrComments.size(), actualFrComments.size());

        for (int i = 0; i < expectedFrComments.size(); i++) {
            assertEquals(expectedFrComments.get(i).courseId, actualFrComments.get(i).courseId);
            assertEquals(expectedFrComments.get(i).commentGiver, actualFrComments.get(i).commentGiver);
            assertEquals(expectedFrComments.get(i).feedbackSessionName,
                         actualFrComments.get(i).feedbackSessionName);
        }
    }

    @Test
    public void testUpdateFeedbackResponseComment() throws Exception {
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");

        ______TS("fail: invalid params");

        frComment.courseId = "invalid course name";
        FeedbackResponseCommentAttributes finalFrca = frComment;
        String expectedError =
                "\"" + frComment.courseId + "\" is not acceptable to TEAMMATES as a/an course ID "
                + "because it is not in the correct format. A course ID can contain letters, "
                + "numbers, fullstops, hyphens, underscores, and dollar signs. It cannot be longer "
                + "than 40 characters, cannot be empty and cannot contain spaces.";
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> frcLogic.updateFeedbackResponseComment(finalFrca));
        assertEquals(expectedError, ipe.getMessage());
        frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");

        ______TS("typical success case");

        frComment.commentText = "Updated feedback response comment";
        frcLogic.updateFeedbackResponseComment(frComment);
        verifyPresentInDatastore(frComment);
        List<FeedbackResponseCommentAttributes> actualFrComments =
                frcLogic.getFeedbackResponseCommentForSession(frComment.courseId, frComment.feedbackSessionName);

        FeedbackResponseCommentAttributes actualFrComment = null;
        for (FeedbackResponseCommentAttributes comment : actualFrComments) {
            if (comment.commentText.equals(frComment.commentText)) {
                actualFrComment = comment;
                break;
            }
        }
        assertNotNull(actualFrComment);

        ______TS("typical success case update feedback response comment giver email");

        String oldEmail = frComment.commentGiver;
        String updatedEmail = "newEmail@gmail.tmt";
        frcLogic.updateFeedbackResponseCommentsEmails(frComment.courseId, oldEmail, updatedEmail);

        actualFrComment = frcLogic.getFeedbackResponseComment(
                                           frComment.feedbackResponseId, updatedEmail, frComment.createdAt);

        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(updatedEmail, actualFrComment.commentGiver);
        assertEquals(updatedEmail, actualFrComment.lastEditorEmail);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);

        // reset email
        frcLogic.updateFeedbackResponseCommentsEmails(frComment.courseId, updatedEmail, oldEmail);

        ______TS("typical success case update feedback response comment feedbackResponseId");

        String oldId = frComment.feedbackResponseId;
        String updatedId = "newResponseId";
        frcLogic.updateFeedbackResponseCommentsForChangingResponseId(oldId, updatedId);

        actualFrComment = frcLogic.getFeedbackResponseComment(
                updatedId, frComment.commentGiver, frComment.createdAt);

        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(updatedId, actualFrComment.feedbackResponseId);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);

        // reset id
        frcLogic.updateFeedbackResponseCommentsForChangingResponseId(updatedId, oldId);
    }

    @Test
    public void testDeleteFeedbackResponseCommentById() throws Exception {

        ______TS("silent fail nothing to delete");

        assertNull(frcLogic.getFeedbackResponseComment(1234567L));
        frcLogic.deleteFeedbackResponseCommentById(1234567L);

        ______TS("typical success case");
        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q1S1C1");
        FeedbackResponseCommentAttributes actualFrComment =
                frcLogic.getFeedbackResponseCommentForSession(
                                 frComment.courseId, frComment.feedbackSessionName).get(1);
        frcLogic.deleteFeedbackResponseCommentById(actualFrComment.getId());
        verifyAbsentInDatastore(actualFrComment);
    }

    @Test
    public void testDeleteFeedbackResponseCommentsForResponse() {

        ______TS("typical success case");

        FeedbackResponseCommentAttributes frComment = restoreFrCommentFromDataBundle("comment1FromT1C1ToR1Q3S1C1");
        verifyPresentInDatastore(frComment);
        frcLogic.deleteFeedbackResponseCommentsForResponse(frComment.feedbackResponseId);
        verifyAbsentInDatastore(frComment);
    }

    @Test
    public void testDeleteFeedbackResponseCommentFromCourse() {

        ______TS("typical case");
        String courseId = "idOfTypicalCourse1";

        List<FeedbackResponseCommentAttributes> frcList =
                frcLogic.getFeedbackResponseCommentForSession(courseId, "First feedback session");
        assertFalse(frcList.isEmpty());

        frcLogic.deleteFeedbackResponseCommentsForCourse(courseId);

        frcList = frcLogic.getFeedbackResponseCommentForSession(courseId, "First feedback session");
        assertEquals(0, frcList.size());
    }

    private void verifyNullFromGetFrCommentForSession(FeedbackResponseCommentAttributes frComment) {
        List<FeedbackResponseCommentAttributes> frCommentsGot =
                frcLogic.getFeedbackResponseCommentForSession(frComment.courseId, frComment.feedbackSessionName);
        assertEquals(0, frCommentsGot.size());
    }

    private void verifyNullFromGetFrComment(FeedbackResponseCommentAttributes frComment) {
        FeedbackResponseCommentAttributes frCommentGot =
                frcLogic.getFeedbackResponseComment(
                                 frComment.feedbackResponseId, frComment.commentGiver, frComment.createdAt);
        assertNull(frCommentGot);
    }

    private FeedbackResponseCommentAttributes restoreFrCommentFromDataBundle(String existingFrCommentInDataBundle) {

        FeedbackResponseCommentAttributes existingFrComment =
                dataBundle.feedbackResponseComments.get(existingFrCommentInDataBundle);

        FeedbackResponseCommentAttributes frComment = FeedbackResponseCommentAttributes
                .builder(existingFrComment.courseId, existingFrComment.feedbackSessionName,
                        existingFrComment.commentGiver, existingFrComment.commentText)
                .withFeedbackQuestionId(existingFrComment.feedbackQuestionId)
                .withFeedbackResponseId(existingFrComment.feedbackResponseId)
                .withCreatedAt(existingFrComment.createdAt)
                .withCommentGiverType(existingFrComment.commentGiverType)
                .withCommentFromFeedbackParticipant(false)
                .build();

        restoreFrCommentIdFromExistingOne(frComment, existingFrComment);

        return frComment;
    }

    private void restoreFrCommentIdFromExistingOne(
            FeedbackResponseCommentAttributes frComment,
            FeedbackResponseCommentAttributes existingFrComment) {

        List<FeedbackResponseCommentAttributes> existingFrComments =
                frcLogic.getFeedbackResponseCommentForSession(
                                 existingFrComment.courseId,
                                 existingFrComment.feedbackSessionName);

        FeedbackResponseCommentAttributes existingFrCommentWithId = null;
        for (FeedbackResponseCommentAttributes c : existingFrComments) {
            if (c.commentText.equals(existingFrComment.commentText)) {
                existingFrCommentWithId = c;
                break;
            }
        }
        if (existingFrCommentWithId != null) {
            frComment.setId(existingFrCommentWithId.getId());
            frComment.feedbackResponseId = existingFrCommentWithId.feedbackResponseId;
        }
    }

    private String getQuestionIdInDataBundle(String questionInDataBundle) {
        FeedbackQuestionAttributes question = dataBundle.feedbackQuestions.get(questionInDataBundle);
        question = fqLogic.getFeedbackQuestion(
                                   question.feedbackSessionName, question.courseId, question.questionNumber);
        return question.getId();
    }

    private String getResponseIdInDataBundle(String responseInDataBundle, String questionInDataBundle) {
        FeedbackResponseAttributes response = dataBundle.feedbackResponses.get(responseInDataBundle);
        response = frLogic.getFeedbackResponse(
                                   getQuestionIdInDataBundle(questionInDataBundle),
                                   response.giver,
                                   response.recipient);
        return response.getId();
    }

    @Test(dependsOnMethods = {"testCreateFeedbackResponseComment",
            "testCreateFeedbackResponseComment_invalidCommentGiverType_exceptionShouldBeThrown",
            "testCreateFeedbackResponseComment_invalidVisibilitySettings_exceptionShouldBeThrown",
            "testCreateFeedbackResponseComment_unknownFeedbackParticipant_exceptionShouldBeThrown",
            "testDeleteFeedbackResponseCommentById",
            "testDeleteFeedbackResponseCommentFromCourse",
            "testDeleteFeedbackResponseCommentsForResponse",
            "testGetFeedbackResponseComments",
            "testUpdateFeedbackResponseComment",
            "testIsNameVisibleToUser"})
    private void checkCoverage() {
        frcLogic.testVisibleCommentCoverage(); // 0 branches covered by tests
    }

    /**
     * Test for isNameVisibleToUser
     * needs a comment, response user email, and course roster
     */
    @Test
    public void testIsNameVisibleToUser() {
        String courseId = "1";
        String feedbackSessionName = "testSession";
        String student1Name = "Student Testsson";
        String teacherName = "Teacher Testberg";
        String student1Email = "student1@email.com";
        String teacherEmail = "teacher@email.com";
        String googleId = "123";
        String comment1Giver = student1Email;
        String comment1Text = "This is some feedback for you.";
        String student2Name = "Student2 Testsson";
        String student2Email = "student2@email.com";

        //list of students
        StudentAttributes student1Attributes = new StudentAttributes.Builder(courseId, student1Name, student1Email).build();
        StudentAttributes student2Attributes = new StudentAttributes.Builder(courseId, student2Name, student2Email).build();
        ArrayList<StudentAttributes> studentList = new ArrayList<>();
        studentList.add(student1Attributes);
        studentList.add(student2Attributes);

        //list of teachers
        InstructorAttributes instructorAttributes = new InstructorAttributes.Builder(googleId, courseId, teacherName, teacherEmail).build();
        ArrayList<InstructorAttributes> instructorList = new ArrayList<>();
        instructorList.add(instructorAttributes);

        //course roster
        CourseRoster roster = new CourseRoster(studentList, instructorList);

        //given comment
        FeedbackResponseCommentAttributes comment = FeedbackResponseCommentAttributes.builder(courseId, feedbackSessionName, comment1Giver, comment1Text).build();
        comment.isVisibilityFollowingFeedbackQuestion = false;
        //comment permissons
        List<FeedbackParticipantType> showGiverNameTo = new ArrayList<>();
        showGiverNameTo.add(FeedbackParticipantType.INSTRUCTORS);
        comment.showGiverNameTo = showGiverNameTo;

        //response
        FeedbackResponseAttributes response = new FeedbackResponseAttributes(feedbackSessionName, courseId,
                null, student1Email, null, student2Email, null, new FeedbackTextResponseDetails());

        //teacher should always see
        assertTrue(frcLogic.isNameVisibleToUser(comment, response, teacherEmail, roster));

        //random person should not see
        assertFalse(frcLogic.isNameVisibleToUser(comment, response, "whoIsDis@email.com", roster));

        //response giver should see
        assertTrue(frcLogic.isNameVisibleToUser(comment, response, student1Email, roster));
    }

}
