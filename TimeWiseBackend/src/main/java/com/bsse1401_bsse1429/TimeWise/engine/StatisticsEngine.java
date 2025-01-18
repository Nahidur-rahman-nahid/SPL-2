package com.bsse1401_bsse1429.TimeWise.engine;

import com.bsse1401_bsse1429.TimeWise.model.Feedback;
import com.bsse1401_bsse1429.TimeWise.model.Session;
import com.bsse1401_bsse1429.TimeWise.model.Task;
import com.bsse1401_bsse1429.TimeWise.model.User;
import com.bsse1401_bsse1429.TimeWise.repository.*;
import com.bsse1401_bsse1429.TimeWise.utils.UsersAccountStatistics;
import com.bsse1401_bsse1429.TimeWise.utils.UsersFeedbackStatistics;
import com.bsse1401_bsse1429.TimeWise.utils.UsersSessionStatistics;
import com.bsse1401_bsse1429.TimeWise.utils.UsersTaskStatistics;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class StatisticsEngine {

    @Autowired
    private TeamRepository teamRepositoryInstance;

    @Autowired
    private TaskRepository taskRepositoryInstance;

    @Autowired
    private UserRepository userRepositoryInstance;

    @Autowired
    private SessionRepository sessionRepositoryInstance;

    @Autowired
    private FeedbackRepository feedbackRepositoryInstance;

    @Autowired
    private MessageRepository messageRepositoryInstance;

    @Autowired
    private ProgressReportRepository progressReportRepositoryInstance;

    private static UserRepository userRepository;
    private static TeamRepository teamRepository;
    private static TaskRepository taskRepository;
    private static SessionRepository sessionRepository;
    private static FeedbackRepository feedbackRepository ;
    private static MessageRepository messageRepository ;
    private static ProgressReportRepository progressReportRepository;

    @PostConstruct
    private void initStaticDependencies() {
        userRepository = userRepositoryInstance;
        teamRepository=teamRepositoryInstance;
        taskRepository=taskRepositoryInstance;
        sessionRepository=sessionRepositoryInstance;
        progressReportRepository=progressReportRepositoryInstance;
        feedbackRepository=feedbackRepositoryInstance;
        messageRepository=messageRepositoryInstance;
    }

    public static UsersTaskStatistics calculateTaskStatistics(String userName, int numberOfDays) {
        // Find all tasks for the user
        List<Task> tasks = taskRepository.findByTaskParticipantsContains(userName);

        // Calculate the cutoff date
        Date cutoffDate = calculateCutoffDate(numberOfDays);

        UsersTaskStatistics taskPerformance = new UsersTaskStatistics(
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
        );

        Date currentDate = new Date();
        for (Task task : tasks) {
            // Filter tasks based on their creation or relevant timestamp
            if (task.getTaskCreationDate().after(cutoffDate)) {
                boolean isCompleted = task.getTaskCurrentProgress() >= 100;
                boolean isDeadlineCrossed = task.getTaskDeadline().before(currentDate);

                if (isCompleted && !isDeadlineCrossed) {
                    taskPerformance.getTasksCompletedBeforeDeadline().add(task.getTaskName());
                } else if (isCompleted) {
                    taskPerformance.getTasksCompletedAfterDeadline().add(task.getTaskName());
                } else if (!isDeadlineCrossed) {
                    taskPerformance.getDeadlineUncrossedAndUnfinishedTasks().add(task.getTaskName());
                } else {
                    taskPerformance.getDeadlineCrossedAndUnfinishedTasks().add(task.getTaskName());
                }
            }
        }

        return taskPerformance;
    }

    public static UsersSessionStatistics calculateSessionStatistics(String userName, int numberOfDays) {
        List<Session> sessions = sessionRepository.findBySessionCreator(userName);
        Date cutoffDate = calculateCutoffDate(numberOfDays);

        int sessionCount = 0;
        double totalSessionTime = 0; // in minutes
        double totalEfficiency = 0;
        int totalTasksOperated = 0;
        List<String> sessionNames = new ArrayList<>();

        for (Session session : sessions) {
            if (session.getSessionTimeStamp().after(cutoffDate)) {
                sessionCount++;
                totalSessionTime += session.getDuration();
                totalEfficiency += session.getSessionEfficiency();
                totalTasksOperated += session.getTasksOperated().size();
                sessionNames.add(session.getSessionGoal());
            }
        }

        double averageEfficiency = sessionCount > 0 ? totalEfficiency / sessionCount : 0;
        return new UsersSessionStatistics(
                sessionCount,
                totalSessionTime / 60.0, // Convert to hours
                averageEfficiency,
                totalTasksOperated,
                sessionNames
        );
    }

    public static UsersFeedbackStatistics calculateFeedbackStatistics(String userName, int numberOfDays) {
        // Find all feedbacks for the user
        List<Feedback> feedbacks = feedbackRepository.findByFeedbackRecipientsContains(userName);

        // Calculate the cutoff date
        Date cutoffDate = calculateCutoffDate(numberOfDays);

        int feedbackCount = 0;
        double totalScore = 0;
        List<String> feedbackMessages = new ArrayList<>();

        for (Feedback feedback : feedbacks) {
            // Filter feedbacks based on their timestamp
            if (feedback.getTimeStamp().after(cutoffDate)) {
                feedbackCount++;
                totalScore += feedback.getFeedbackScore();
                feedbackMessages.add(feedback.getFeedbackMessage());
            }
        }

        double averageScore = feedbackCount > 0 ? totalScore / feedbackCount : 0;
        return new UsersFeedbackStatistics(feedbackCount, averageScore, feedbackMessages);
    }



    private static Date calculateCutoffDate(int numberOfDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -numberOfDays);
        return calendar.getTime();
    }

    public static UsersAccountStatistics calculateUserAccountStatistics(String userName, int numberOfDays) {
        // Fetch the user by username
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            throw new IllegalArgumentException("User not found for username: " + userName);
        }

        // Calculate the cutoff date
        Date cutoffDate = calculateCutoffDate(numberOfDays);

        // Calculate the number of users the user is following
        Long numberOfUserFollowing = user.getUsersFollowing() != null ? (long) user.getUsersFollowing().size() : 0;

        // Calculate the number of teams the user is participating in (filter by cutoff date)
        Long teamsParticipated = teamRepository.countByTeamMembersContains(userName);

        // Filter tasks the user participated in based on the cutoff date
        List<Task> tasksParticipated = taskRepository.findByTaskParticipantsContains(userName);
        Long numberOfTasksParticipated = tasksParticipated.stream()
                .filter(task -> task.getTaskCreationDate().after(cutoffDate))
                .count();

        // Filter sessions created by the user based on the cutoff date
        List<Session> sessionsCreated = sessionRepository.findBySessionCreator(userName);
        Long numberOfSessionsCreated = sessionsCreated.stream()
                .filter(session -> session.getSessionTimeStamp().after(cutoffDate))
                .count();

        // Fetch feedback scores (filtered by cutoff date)
        List<Feedback> feedbacks = feedbackRepository.findByFeedbackRecipientsContains(userName);
        List<Integer> previousFeedbackScores = new ArrayList<>();
        for (Feedback feedback : feedbacks) {
            if (feedback.getTimeStamp().after(cutoffDate)) {
                previousFeedbackScores.add(feedback.getFeedbackScore());
            }
        }

        // Count messages sent and received within the cutoff date
        Long numberOfMessagesSent = messageRepository.countBySenderAndTimeStampAfter(userName, cutoffDate);
        Long numberOfMessagesReceived = messageRepository.countByRecipientsContainsAndTimeStampAfter(userName, cutoffDate);

        return new UsersAccountStatistics(
                numberOfUserFollowing,
                teamsParticipated,
                numberOfTasksParticipated,
                numberOfSessionsCreated,
                previousFeedbackScores,
                numberOfMessagesSent,
                numberOfMessagesReceived
        );
    }

}




