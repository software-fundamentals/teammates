package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.ExceedingRangeException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.SectionDetail;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.StringHelper;
import teammates.ui.datatransfer.InstructorFeedbackResultsPageViewType;
import teammates.ui.pagedata.InstructorFeedbackResultsPageData;

public class InstructorFeedbackResultsPageAction extends Action {

    private static final String ALL_SECTION_OPTION = "All";
    private static final int DEFAULT_SECTION_QUERY_RANGE = 2500;

    private boolean[] executeCoverage = new boolean[20];

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        executeCoverage[0] = true;
        //+1 complexity
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        boolean showStats = getRequestParamAsOnOffBoolean(Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS);

        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);

        statusToAdmin = "Show instructor feedback result page<br>"
                      + "Session Name: " + feedbackSessionName + "<br>"
                      + "Course ID: " + courseId;

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);

        gateKeeper.verifyAccessible(instructor, session);

        InstructorFeedbackResultsPageData data = new InstructorFeedbackResultsPageData(account, sessionToken);
        String selectedSection = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION);
        String sectionDetailValue = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTIONDETAIL);
        SectionDetail selectedSectionDetail = SectionDetail.NOT_APPLICABLE;

        if (selectedSection == null) { //+1 complexity
            executeCoverage[1] = true;
            selectedSection = ALL_SECTION_OPTION;
        } else if (sectionDetailValue != null && !sectionDetailValue.isEmpty()) { //+2 complexity
            executeCoverage[2] = true;
            Assumption.assertNotNull(SectionDetail.containsSectionDetail(sectionDetailValue));
            selectedSectionDetail = SectionDetail.valueOf(sectionDetailValue);
        }

        //4 complexity

        boolean isMissingResponsesShown = getRequestParamAsBoolean(
                Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES);

        // this is for ajax loading of the html table in the modal
        // "(Non-English characters not displayed properly in the downloaded file? click here)"
        // TODO move into another action and another page data class
        boolean isLoadingCsvResultsAsHtml = getRequestParamAsBoolean(Const.ParamsNames.CSV_TO_HTML_TABLE_NEEDED);
        if (isLoadingCsvResultsAsHtml) { //+1 complexity
            executeCoverage[3] = true;
            return createAjaxResultForCsvTableLoadedInHtml(
                    courseId, feedbackSessionName, instructor, data, selectedSection, selectedSectionDetail,
                    isMissingResponsesShown, Boolean.valueOf(showStats));
        }
        data.setSessionResultsHtmlTableAsString("");
        data.setAjaxStatus("");

        boolean groupByTeam = getRequestParamAsOnOffBoolean(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM);
        String sortType = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE);
        String startIndex = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_MAIN_INDEX);

        if (startIndex != null) { //+1 complexity
            executeCoverage[4] = true;
            data.setStartIndex(Integer.parseInt(startIndex));
        }

        if (sortType == null) { //+1 complexity
            executeCoverage[5] = true;
            // default view: sort by question, statistics shown, grouped by team.
            showStats = true;
            groupByTeam = true;
            sortType = Const.FeedbackSessionResults.QUESTION_SORT_TYPE;
            isMissingResponsesShown = true;
        }

        //4 + 3 complexity

        String questionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_QUESTION_ID);
        String isTestingAjax = getRequestParamValue(Const.ParamsNames.FEEDBACK_RESULTS_NEED_AJAX);

        if (ALL_SECTION_OPTION.equals(selectedSection) && questionId == null //+3 complexity
                && !Const.FeedbackSessionResults.QUESTION_SORT_TYPE.equals(sortType)) {
            executeCoverage[6] = true;
            // bundle for all questions and all sections
            data.setBundle(
                     logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                                                           feedbackSessionName, courseId,
                                                                           instructor.email,
                                                                           DEFAULT_SECTION_QUERY_RANGE, sortType));
        } else if (Const.FeedbackSessionResults.QUESTION_SORT_TYPE.equals(sortType)) { //+1 complexity
            executeCoverage[7] = true;
            data.setBundle(getBundleForQuestionView(isTestingAjax, courseId, feedbackSessionName, instructor, data,
                                                    selectedSection, selectedSectionDetail, sortType, questionId));
        } else if (Const.FeedbackSessionResults.GQR_SORT_TYPE.equals(sortType)
                || Const.FeedbackSessionResults.GRQ_SORT_TYPE.equals(sortType)) { //+2 complexity
            executeCoverage[8] = true;
            data.setBundle(logic
                    .getFeedbackSessionResultsForInstructorFromSectionWithinRange(feedbackSessionName, courseId,
                                                                                  instructor.email,
                                                                                  selectedSection,
                                                                                  DEFAULT_SECTION_QUERY_RANGE));
        } else if (Const.FeedbackSessionResults.RQG_SORT_TYPE.equals(sortType)
                || Const.FeedbackSessionResults.RGQ_SORT_TYPE.equals(sortType)) { //+2 complexity
            executeCoverage[9] = true;
            data.setBundle(logic
                    .getFeedbackSessionResultsForInstructorToSectionWithinRange(feedbackSessionName, courseId,
                                                                                instructor.email,
                                                                                selectedSection,
                                                                                DEFAULT_SECTION_QUERY_RANGE));
        }

        //4 + 3 + 8 complexity

        if (data.getBundle() == null) { //+1 complexity
            executeCoverage[10] = true;
            throw new EntityDoesNotExistException("Feedback session " + feedbackSessionName
                                                  + " does not exist in " + courseId + ".");
        }

        // Warning for section wise viewing in case of many responses.
        boolean isShowSectionWarningForQuestionView = data.isLargeNumberOfRespondents()
                                                   && Const.FeedbackSessionResults.QUESTION_SORT_TYPE.equals(sortType);
        boolean isShowSectionWarningForParticipantView = !data.getBundle().isComplete
                                                   && !Const.FeedbackSessionResults.QUESTION_SORT_TYPE.equals(sortType);

        // Warning for section wise does not make sense if there are no multiple sections.
        boolean isMultipleSectionAvailable = data.getBundle().getRosterSectionTeamNameTable().size() > 1;

        //tot 4 complexity
        if (selectedSection.equals(ALL_SECTION_OPTION) && (isShowSectionWarningForParticipantView
                                                           || isShowSectionWarningForQuestionView)) { //+3 complexity
            executeCoverage[11] = true;
            if (isMultipleSectionAvailable) { //+1 complexity
                executeCoverage[12] = true;
                statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_RESULTS_SECTIONVIEWWARNING,
                                                   StatusMessageColor.WARNING));
            } else { //+1 complexity
                executeCoverage[13] = true;
                statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_RESULTS_QUESTIONVIEWWARNING,
                                                   StatusMessageColor.WARNING));
            }
            isError = true;
        }

        //4 + 3 + 8 + 6 complexity

        switch (sortType) { //tot 6 complexity
        case Const.FeedbackSessionResults.QUESTION_SORT_TYPE: //+1 complexity
            executeCoverage[14] = true;
            data.initForViewByQuestion(instructor, selectedSection, selectedSectionDetail, showStats,
                    groupByTeam, isMissingResponsesShown);
            return createShowPageResult(
                    Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_QUESTION, data);
        case Const.FeedbackSessionResults.RGQ_SORT_TYPE: //+1 complexity
            executeCoverage[15] = true;
            data.initForSectionPanelViews(instructor, selectedSection, showStats, groupByTeam,
                                          InstructorFeedbackResultsPageViewType.RECIPIENT_GIVER_QUESTION,
                                          isMissingResponsesShown);
            return createShowPageResult(
                    Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION, data);
        case Const.FeedbackSessionResults.GRQ_SORT_TYPE: //+1 complexity
            executeCoverage[16] = true;
            data.initForSectionPanelViews(instructor, selectedSection, showStats, groupByTeam,
                                          InstructorFeedbackResultsPageViewType.GIVER_RECIPIENT_QUESTION,
                                          isMissingResponsesShown);
            return createShowPageResult(
                    Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_RECIPIENT_QUESTION, data);
        case Const.FeedbackSessionResults.RQG_SORT_TYPE: //+1 complexity
            executeCoverage[17] = true;
            data.initForSectionPanelViews(instructor, selectedSection, showStats, groupByTeam,
                                          InstructorFeedbackResultsPageViewType.RECIPIENT_QUESTION_GIVER,
                                          isMissingResponsesShown);
            return createShowPageResult(
                    Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_QUESTION_GIVER, data);
        case Const.FeedbackSessionResults.GQR_SORT_TYPE: //+1 complexity
            executeCoverage[18] = true;
            data.initForSectionPanelViews(instructor, selectedSection, showStats, groupByTeam,
                                          InstructorFeedbackResultsPageViewType.GIVER_QUESTION_RECIPIENT,
                                          isMissingResponsesShown);
            return createShowPageResult(
                    Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER_QUESTION_RECIPIENT, data);
        default: //+1 complexity
            executeCoverage[19] = true;
            sortType = Const.FeedbackSessionResults.RGQ_SORT_TYPE;
            data.initForSectionPanelViews(instructor, selectedSection, showStats, groupByTeam,
                                          InstructorFeedbackResultsPageViewType.RECIPIENT_GIVER_QUESTION,
                                          isMissingResponsesShown);
            return createShowPageResult(
                    Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT_GIVER_QUESTION, data);
        }

        //4 + 3 + 8 + 6 + 6 = 27 complexity
    }

    public void testVisibleCommentCoverage() {
        int covered = 0;
        for (int i = 0; i < executeCoverage.length; i++) {
            if (executeCoverage[i])
                covered ++;
        }
        System.out.println("execute() coverage:");
        System.out.println("Covered branches: " + covered + " of total branches: " + executeCoverage.length);
    }

    private FeedbackSessionResultsBundle getBundleForQuestionView(
            String needAjax, String courseId, String feedbackSessionName, InstructorAttributes instructor,
            InstructorFeedbackResultsPageData data, String selectedSection, SectionDetail selectedSectionDetail,
            String sortType, String questionId)
                    throws EntityDoesNotExistException {
        FeedbackSessionResultsBundle bundle;
        if (questionId == null) {
            if (ALL_SECTION_OPTION.equals(selectedSection)) {
                // load page structure without responses

                data.setLargeNumberOfRespondents(needAjax != null);

                // all sections and all questions for question view
                // set up question tables, responses to load by ajax
                bundle = logic.getFeedbackSessionResultsForInstructorWithinRangeFromView(
                                               feedbackSessionName, courseId,
                                               instructor.email,
                                               1, sortType);
                // set isComplete to true to prevent behavior when there are too many responses,
                // such as the display of warning messages
                bundle.isComplete = true;
            } else {
                // bundle for all questions, with a selected section and selected section detail
                // depending on the section detail, this function will filter accordingly
                bundle = logic.getFeedbackSessionResultsForInstructorInSection(feedbackSessionName, courseId,
                                                                                instructor.email, selectedSection,
                                                                                selectedSectionDetail);
            }
        } else {
            if (ALL_SECTION_OPTION.equals(selectedSection)) {
                // bundle for a specific question, with all sections
                bundle = logic.getFeedbackSessionResultsForInstructorFromQuestion(feedbackSessionName, courseId,
                                                                                  instructor.email, questionId);
            } else {
                // bundle for a specific question and a specific section
                bundle = logic.getFeedbackSessionResultsForInstructorFromQuestionInSection(
                                                feedbackSessionName, courseId,
                                                instructor.email, questionId, selectedSection, selectedSectionDetail);
            }
        }

        return bundle;
    }

    private ActionResult createAjaxResultForCsvTableLoadedInHtml(String courseId, String feedbackSessionName,
                                    InstructorAttributes instructor, InstructorFeedbackResultsPageData data,
                                    String selectedSection, SectionDetail selectedSectionDetail,
                                    boolean isMissingResponsesShown, boolean isStatsShown)
                                    throws EntityDoesNotExistException {
        try {
            if (selectedSection.contentEquals(ALL_SECTION_OPTION)) {
                data.setSessionResultsHtmlTableAsString(
                        StringHelper.csvToHtmlTable(
                                logic.getFeedbackSessionResultSummaryAsCsv(
                                        courseId, feedbackSessionName, instructor.email,
                                        isMissingResponsesShown, isStatsShown, null)));
            } else {
                data.setSessionResultsHtmlTableAsString(
                        StringHelper.csvToHtmlTable(
                                logic.getFeedbackSessionResultSummaryInSectionAsCsv(
                                        courseId, feedbackSessionName, instructor.email,
                                        selectedSection, selectedSectionDetail, null,
                                        isMissingResponsesShown, isStatsShown)));
            }
        } catch (ExceedingRangeException e) {
            // not tested as the test file is not large enough to reach this catch block
            data.setSessionResultsHtmlTableAsString("");
            data.setAjaxStatus("There are too many responses. Please download the feedback results by section.");
        }

        return createAjaxResult(data);
    }

}
