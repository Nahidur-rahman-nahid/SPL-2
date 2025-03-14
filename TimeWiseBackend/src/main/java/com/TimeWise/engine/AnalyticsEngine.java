package com.TimeWise.engine;


import com.TimeWise.model.PerformanceReport;
import com.TimeWise.model.ProgressReport;
import com.TimeWise.model.Session;
import com.TimeWise.model.Task;
import com.TimeWise.repository.*;
import com.TimeWise.utils.DeepWorkHours;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class AnalyticsEngine {

    @Autowired
    private TeamRepository teamRepositoryInstance;

    @Autowired
    private TaskRepository taskRepositoryInstance;

    @Autowired
    private UserRepository userRepositoryInstance;

    @Autowired
    private SessionRepository sessionRepositoryInstance;

    @Autowired
    private ProgressReportRepository progressReportRepositoryInstance;



    private static UserRepository userRepository;
    private static TeamRepository teamRepository;
    private static TaskRepository taskRepository;
    private static SessionRepository sessionRepository;
    private static ProgressReportRepository progressReportRepository;

    @PostConstruct
    private void initStaticDependencies() {
        userRepository = userRepositoryInstance;
        teamRepository=teamRepositoryInstance;
        taskRepository=taskRepositoryInstance;
        sessionRepository=sessionRepositoryInstance;
        progressReportRepository=progressReportRepositoryInstance;
    }

    public static PerformanceReport generateWeeklyPerformanceReport(String userName) {
        PerformanceReport report = new PerformanceReport();
        report.setUserName(userName);
        report.setReportGeneratedDate(new Date());

        // Generate Account Statistics
        report.setUsersAccountStatistics(StatisticsEngine.calculateUserAccountStatistics(userName,7));


        // Generate Task Statistics
        report.setUsersTaskStatistics(StatisticsEngine.calculateTaskStatistics(userName,7));

        // Generate Session Statistics
        report.setUsersSessionStatistics(StatisticsEngine.calculateSessionStatistics(userName, 7));

        // Generate Feedback Statistics
        report.setUsersFeedbackStatistics(StatisticsEngine.calculateFeedbackStatistics(userName,7));


        return report;
    }
    public static PerformanceReport generatePerformanceReport(String userName,int previousNumberOfDays) {
        PerformanceReport report = new PerformanceReport();
        report.setUserName(userName);
        report.setReportGeneratedDate(new Date());

        // Generate Account Statistics
        report.setUsersAccountStatistics(StatisticsEngine.calculateUserAccountStatistics(userName,previousNumberOfDays));


        // Generate Task Statistics
        report.setUsersTaskStatistics(StatisticsEngine.calculateTaskStatistics(userName,previousNumberOfDays));

        // Generate Session Statistics
        report.setUsersSessionStatistics(StatisticsEngine.calculateSessionStatistics(userName, previousNumberOfDays));

        // Generate Feedback Statistics
        report.setUsersFeedbackStatistics(StatisticsEngine.calculateFeedbackStatistics(userName,previousNumberOfDays));


        return report;
    }

    public static ProgressReport generateProgressReport(String userName) {
        List<Task> userTasks=taskRepository.findByTaskParticipantsContains(userName);
        if(userTasks.isEmpty()){
            return null;
        }
        ProgressReport userProgressReport=new ProgressReport();
        userProgressReport.setUserName(userName);
        userProgressReport.setTaskStatuses(new ArrayList<>());
        List<ProgressReport.TaskStatus> taskStatuses=new ArrayList<>();
        Date currentTime=new Date();
        for(Task task:userTasks) {
            if(task.getTaskCurrentProgress()<100) {
                userProgressReport.addTaskStatuses(task.getTaskName(),task.getTaskOwner(),task.getTaskPriority(), task.getTaskCurrentProgress(),task.getTaskCreationDate(), task.getTaskDeadline(), task.getTaskDeadline().before(currentTime));
            }

        }
        userProgressReport.setTimeStamp(currentTime); // Set the timestamp to the current date
        return progressReportRepository.save(userProgressReport);
    }


    // Fetch progress reports by userName
    public List<ProgressReport> getProgressReportsByUser(String userName) {
        return progressReportRepository.findByUserName(userName);
    }



    // Delete a progress report
    public void deleteProgressReport(String userName,Date timeStamp) {
        progressReportRepository.deleteByUserNameAndTimeStamp(userName,timeStamp);
    }
    

    public static List<DeepWorkHours> getUsersDeepWorkingHours(String userName, int numberOfDays) {
        List<DeepWorkHours> deepWorkHours = new ArrayList<>();
        // Fetch all sessions for the user
        List<Session> userSessions = sessionRepository.findBySessionCreator(userName);
        if (userSessions.isEmpty()) {
            return deepWorkHours; // No sessions performed in the given period
        }

        // Filter sessions based on the number of days
        Date currentDate = new Date();
        Calendar cutoffDate = Calendar.getInstance();
        cutoffDate.setTime(currentDate);
        cutoffDate.add(Calendar.DAY_OF_YEAR, -numberOfDays);

        List<Session> filteredSessions = new ArrayList<>();
        for (Session session : userSessions) {
            if (!session.getSessionTimeStamp().before(cutoffDate.getTime())) {
                filteredSessions.add(session);
            }
        }

        if (filteredSessions.isEmpty()) {
            return deepWorkHours; // No sessions found in the specified time range
        }

        // Map to hold hourly data
        Map<Integer, HourlyData> hourlyDataMap = new HashMap<>();

        // Initialize map for 24 hours
        for (int i = 0; i < 24; i++) {
            hourlyDataMap.put(i, new HourlyData());
        }

        // Process each session and intelligently assign it to the correct time slot
        for (Session session : filteredSessions) {
            Date sessionStart = session.getSessionTimeStamp();
            int sessionDurationMinutes = session.getDuration(); // Duration is in minutes (5 to 60)

            Map<Integer, Integer> hourDurationMap = calculateHourDistribution(sessionStart, sessionDurationMinutes);

            // Assign the session to the hour where it has the majority duration
            int preferredHour = hourDurationMap.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .get()
                    .getKey(); // Get the hour with the maximum duration

            // Update hourly data for the preferred hour
            HourlyData data = hourlyDataMap.get(preferredHour);
            data.totalMinutesWorked += sessionDurationMinutes;
            data.totalTasksOperated += session.getTasksOperated().size();
            data.totalEfficiencyScore += session.getSessionEfficiency();
            data.sessionCount++;
        }

        // Convert hourly data into DeepWorkHours objects
        for (int hour = 0; hour < 24; hour++) {
            HourlyData data = hourlyDataMap.get(hour);
            if (data.sessionCount > 0) {
                String timeSlot = getTimeSlot(hour);
                double totalHoursWorked = data.totalMinutesWorked / 60.0; // Convert total minutes to hours
                double averageEfficiency = data.totalEfficiencyScore / data.sessionCount;
                deepWorkHours.add(new DeepWorkHours(timeSlot, totalHoursWorked, data.totalTasksOperated, data.totalEfficiencyScore, data.sessionCount, averageEfficiency));
            }
        }

        return deepWorkHours;
    }

    // Helper method to calculate the hour distribution of a session
    private static Map<Integer, Integer> calculateHourDistribution(Date sessionStart, int sessionDurationMinutes) {
        Map<Integer, Integer> hourDurationMap = new HashMap<>();

        // Create a Calendar instance with the LOCAL time zone
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault()); // Use system's default time zone
        calendar.setTime(sessionStart);

        int remainingDuration = sessionDurationMinutes;

        while (remainingDuration > 0) {
            // Fetch the updated hour at the **start** of each iteration
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int minutesUntilNextHour = 60 - calendar.get(Calendar.MINUTE); // Minutes left in the current hour

            int allocatedMinutes = Math.min(minutesUntilNextHour, remainingDuration);

            // Store the allocated minutes for this hour
            hourDurationMap.put(currentHour, hourDurationMap.getOrDefault(currentHour, 0) + allocatedMinutes);

            remainingDuration -= allocatedMinutes;

            // Move forward by allocated minutes
            calendar.add(Calendar.MINUTE, allocatedMinutes);

            // Now fetch the updated hour for debugging
            System.out.println("Updated Hour: " + calendar.get(Calendar.HOUR_OF_DAY) + ", Minutes: " + calendar.get(Calendar.MINUTE));
        }

        return hourDurationMap;
    }


    // Helper method to get the hour of a given date
    private static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    // Helper method to generate a time slot string (24-hour compatible)
    private static String getTimeSlot(int hour) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:00");
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        String startTime = sdf.format(calendar.getTime());

        calendar.set(Calendar.MINUTE, 59);
        String endTime = sdf.format(calendar.getTime());

        return startTime + " - " + endTime;
    }

    // Inner class to store intermediate hourly data
    private static class HourlyData {
        int totalMinutesWorked = 0; // Store total minutes worked
        int totalTasksOperated = 0;
        double totalEfficiencyScore = 0.0;
        int sessionCount = 0;
    }





}


